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
    ByteBuffer buf = ByteBuffer.allocate(4096);
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
                send(new Request("test server status", "", collection, "", null));
                //System.out.println("CONNECTED");
            } catch (ClassNotFoundException e) {
                //System.out.println("WWWWWWW");
                throw new RuntimeException(e);
            }
        }
    }
    public LinkedHashSet<MusicBand> send(Request data) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(data);
        buf.clear();
        //System.out.println(data.getCommand());
        //System.out.println(data.getArgs());
        buf = ByteBuffer.wrap(bos.toByteArray());
        //System.out.println(Arrays.toString(buf.array()) + " " + address.toString());
        channel.send(buf, address);
        return listen();
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
                    //System.out.println("RESPONSE");
                    //try {

                        Request req = (Request) ois.readObject();
                    /*}
                    catch (EOFException e){
                        System.out.println("A large amount of data, it is impossible to transfer");
                    }*/
                    //System.out.println(req.getCommand());
                    /*if (Objects.equals(req.getCommand(), "SENDDATA")){
                        send(new Request("CONNECTCLIENT", ReadFromJson.fileName, null));
                    }*/
                    //System.out.println(req.getArgs());
                    if (req.getArgs().contains("NEEDCOLLECTION|")) {
                        Scanner input = new Scanner(System.in);
                        System.out.println("Can not send data to server, check server status and your internet connection. Retry? y/n");
                        System.out.printf("%s >>> ", CreateUserName.userName);
                        if (!Objects.equals(input.nextLine(), "y")) {
                            System.out.println("Reconnection cancelled. closing program");
                            System.exit(0);
                        } else
                            try {
                                send(new Request("send collection", ReadFromJson.fileName, getCollection(), CreateUserName.userName, null));
                            } catch (PortUnreachableException ignored) {
                            }
                    }
                    collection = req.getElement();
                    if (Objects.equals(req.getCommand(), "exit")) {
                        if (req.getArgs().equals("unsaved")) {
                            ContinueAction cont = new ContinueAction();
                            System.out.println("Collection unsaved. Save and exit? y/n");
                            System.out.printf("%s >>> ", CreateUserName.userName);
                            int c = cont.continueAction("Saving", "Not saved. Exit cancelled", "Action skipped. Invalid answer");
                            if (c == 1) {
                                send(new Request("exit",  String.format("%ssaveit|%s", CreateUserName.userName, ReadFromJson.fileName), collection, CreateUserName.userName, null));
                            }
                        }
                        //System.out.println("ASDASDASD");
                        disconnect();
                        System.exit(0);
                    } else if (req.getArgs() != null && req.getArgs().contains("FILENAME ")) {
                        if (req.getArgs().contains("true|")) {
                            //System.out.println(req.getArgs());
                            try {
                                ReadFromJson.fileName = req.getArgs().split(" ")[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                ReadFromJson.fileName = "";
                            }
                            collection = req.getElement();
                            ReadFromJson.fileName = ReadFromJson.fileName.replace("|Y", "");
                            /*if (ReadFromJson.fileName.equals("null"))
                                ;
                            else {
                                ReadFromJson.fileName = ReadFromJson.fileName.replace(".json", "");
                                System.out.printf("Collection loaded from file %s.json\n", ReadFromJson.fileName);
                            }*/
                        } else {
                            //ReadFromJson.fileName = req.getArgs().split(" ")[1];
                            collection = null;
                            //System.out.printf("Collection loaded from file %s\n", ReadFromJson.fileName);
                        }
                    } else if (req.getArgs().contains("|NEEDCREATE")) {
                        Scanner input = new Scanner(System.in);
                        //System.out.println(req.getArgs());

                        while (true) {
                            ReadFromJson.fileName = req.getArgs().replace("|NEEDCREATE", "").replace("|NCREATEFILE", "").replace("|CREATEFILE", "");
                            System.out.printf("File not found\nCrate file \"%s\"? y/n\n", ReadFromJson.fileName);
                            ContinueAction cont = new ContinueAction();
                            System.out.printf("%s >>> ", CreateUserName.userName);
                            int c = cont.continueAction(String.format("File \"%s\" has been created", ReadFromJson.fileName), "File has not been created", "Action skipped. Invalid answer");
                            if (c == 1) {
                                ReadFromJson.fileName = req.getArgs().replace("|NEEDCREATE", "");
                                //FileOutputStream out = new FileOutputStream(ReadFromJson.fileName);
                                //out.write("".getBytes());
                                //out.close();
                                collection = send(new Request("CONNECTCLIENT", String.format("%s|CREATEFILE", ReadFromJson.fileName), collection, CreateUserName.userName, null));
                                //inputStream = new FileInputStream(new File(ReadFromJson.fileName).getAbsolutePath());
                                break;
                            } else {
                                System.out.println("Enter file name");
                                System.out.printf("%s >>> ", CreateUserName.userName);
                                ReadFromJson.fileName = input.nextLine();
                                collection = send(new Request("CONNECTCLIENT", String.format("%s|NCREATEFILE", ReadFromJson.fileName), collection, CreateUserName.userName, null));
                                break;
                            }
                        }
                    } else if (req.getArgs().equals("ERRORINFILE")) {

                    }
                    //System.out.println(req.getCommand()); //object to return
                    if (!req.getArgs().contains("|"))
                        System.out.println(req.getArgs());
                /*System.out.println(req.getElement());
                collection = req.getElement();*/
                    //System.out.println(collection);
                    return collection;
                    //break;
                } catch (StreamCorruptedException ignored) {
                } catch (EOFException e){
                    System.out.println("A large amount of data, it is impossible to transfer");
                }
        }
    }

    public void disconnect() throws IOException {
        channel.disconnect();
        channel.close();
        //System.exit(0);
    }

    public LinkedHashSet<MusicBand> getCollection(){
        return collection;
    }
}
