package helpers;

import com.sun.tools.javac.Main;
import jsonHelper.ReadFromJson;
import supportive.MusicBand;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class ClientToServer {

    protected final DatagramChannel channel;
    protected SocketAddress address;
    LinkedHashSet<MusicBand> collection;
    ByteBuffer buf = ByteBuffer.allocate(65536);
    Scanner inputsc = new Scanner(System.in);
    public ClientToServer(InetAddress host, int port, LinkedHashSet<MusicBand> collection) throws IOException {
        this.collection = collection;
        DatagramChannel dc;
        dc = DatagramChannel.open();
        address = new InetSocketAddress(host, port);
        this.channel = dc;
    }

    public void connect() throws IOException {
        if (!channel.isConnected()) {
            channel.connect(address);
            channel.configureBlocking(false);
            try {
                send(new Request("test server status", "", collection, "", null));;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public LinkedHashSet<MusicBand> send(Request data) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        if ((data.getArgs().length() + data.getUser().length() + data.getCommand().length()) * 2 > 64000){
            System.out.println("Can not send large data");
            return new LinkedHashSet<MusicBand>();
        }
        else {
            oos.writeObject(data);
            buf.clear();
            buf = ByteBuffer.wrap(bos.toByteArray());
            channel.send(buf, address);
            return listen();
        }
    }

    public LinkedHashSet<MusicBand> listen() throws IOException, ClassNotFoundException {
        while (true){
                channel.configureBlocking(false);
                buf.clear();
                buf = ByteBuffer.allocate(65536);
                channel.receive(buf);

                try { //check, that buf.arrau isnt [0, 0, 0, ..., 0]
                    byte[] bts = buf.array();
                    ByteArrayInputStream bais = new ByteArrayInputStream(bts);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    Request req = (Request) ois.readObject();

                    if (req.getArgs().contains("LARGEDATA")){
                        for (int i=0; i < Integer.parseInt(req.getArgs().replace("LARGEDATA", "")); i++){
                            send(new Request(String.format("%s",i), "SENDPLS", collection, req.getUser(), null));
                        }
                        send(new Request("", "STOPSENDING", collection, req.getUser(), null));
                        req.setArgs("|");
                    }
                    
                    collection = req.getElement();
                    if (Objects.equals(req.getCommand(), "exit")) {
                        if (req.getArgs().strip().equals("unsaved")) {
                            ContinueAction cont = new ContinueAction();
                            System.out.println("Collection unsaved. Save and exit? y/n");
                            System.out.printf("%s >>> ", CreateUserName.userName);
                            int c = cont.continueAction("Saving", "Not saved. Exit cancelled", "Action skipped. Invalid answer");
                            if (c == 1) {
                                send(new Request("exit",  String.format("%ssaveit|%s", CreateUserName.userName, ReadFromJson.fileName), collection, CreateUserName.userName, null));
                            }
                        }
                        disconnect();
                        System.exit(0);
                    } else if (req.getArgs() != null && req.getArgs().contains("FILENAME ")) {
                        if (req.getArgs().contains("true|")) {
                            try {
                                ReadFromJson.fileName = req.getArgs().split(" ")[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                ReadFromJson.fileName = "";
                            }
                            collection = req.getElement();
                            ReadFromJson.fileName = ReadFromJson.fileName.replace("|Y", "");
                        } else {
                            collection = null;
                        }
                    } else if (req.getArgs().contains("|NEEDCREATE")) {
                        req.setArgs(req.getArgs().strip());
                        Scanner input = new Scanner(System.in);

                        while (true) {
                            ReadFromJson.fileName = req.getArgs().replace("|NEEDCREATE", "").replace("|NCREATEFILE", "").replace("|CREATEFILE", "");
                            System.out.printf("File not found\nCrate file \"%s\"? y/n\n", ReadFromJson.fileName);
                            ContinueAction cont = new ContinueAction();
                            System.out.printf("%s >>> ", CreateUserName.userName);
                            int c = cont.continueAction(String.format("File \"%s\" has been created", ReadFromJson.fileName), "File has not been created", "Action skipped. Invalid answer");
                            if (c == 1) {
                                ReadFromJson.fileName = req.getArgs().replace("|NEEDCREATE", "");
                                collection = send(new Request("CONNECTCLIENT", String.format("%s|CREATEFILE", ReadFromJson.fileName), collection, CreateUserName.userName, null));
                                break;
                            } else {
                                System.out.println("Enter file name");
                                System.out.printf("%s >>> ", CreateUserName.userName);
                                ReadFromJson.fileName = input.nextLine();
                                collection = send(new Request("CONNECTCLIENT", String.format("%s|NCREATEFILE", ReadFromJson.fileName), collection, CreateUserName.userName, null));
                                break;
                            }
                        }
                    } else if (req.getArgs().strip().equals("ERRORINFILE")) {
                    }
                    if (!req.getArgs().contains("|")){
                        System.out.print(req.getArgs());
                    }
                    return collection;
                } catch (StreamCorruptedException ignored) {}
                catch (EOFException e){
                    System.out.println("A large amount of data, it is impossible to transfer");
                }
        }
    }

    public void disconnect() throws IOException {
        channel.disconnect();
        channel.close();
    }

    public LinkedHashSet<MusicBand> getCollection(){
        return collection;
    }
}
