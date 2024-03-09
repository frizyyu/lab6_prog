package helpers;

import commandHelpers.CommandInitializator;
import commands.Save;
import helpers.Request;
import jsonHelper.ReadFromJson;
import org.apache.logging.log4j.Level;
import supportive.MusicBand;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ServerToClient {
    private final int DEFAULT_PORT = 63342;
    protected final DatagramChannel channel;
    protected SocketAddress address;
    Request r;
    LinkedHashSet<supportive.MusicBand> collection = new LinkedHashSet<>();
    ByteBuffer buf = ByteBuffer.allocate(4096);
    final Reader rdr = new InputStreamReader(System.in);
    org.apache.logging.log4j.Logger logger = new helpers.Logger().getLogger();
    Scanner input = new Scanner(rdr);
    public ServerToClient(InetAddress host, LinkedHashSet<supportive.MusicBand> collection) throws IOException { //Start server
        DatagramChannel dc;
        this.collection = collection;
        dc = DatagramChannel.open();
        address = new InetSocketAddress(63342);
        this.channel = dc;
        channel.bind(address);
    }


    public void listen(){ //listen client
        try{
            if (rdr.ready()) {
                String st = input.nextLine();
                if (Objects.equals(st, "exit")) {
                    System.out.println("Saving and exit");
                    if(ReadFromJson.fileName == null)
                        ReadFromJson.fileName = "backup";
                    if (!ReadFromJson.fileName.contains(".json"))
                        ReadFromJson.fileName += ".json";
                    Save saver = new Save(collection, ReadFromJson.fileName);
                    saver.execute(null);
                    System.exit(0);
                } else if (Objects.equals(st, "save")) {
                    Save saver = new Save(collection, null);
                    saver.execute(null);
                }
            }
            channel.configureBlocking(false);
            buf.clear();
            buf = ByteBuffer.allocate(65536);
            address = channel.receive(buf);
            byte[] bts = buf.array();
            ByteArrayInputStream bais = new ByteArrayInputStream(bts);
            ObjectInputStream ois = new ObjectInputStream(bais);
            r = (Request) ois.readObject();
            logger.log(Level.INFO, String.format("Get request from client %s", r.getUser()));
            //выполнение команды
            String data = commandExecution(r.getCommand(), r);
            r = new Request(r.getCommand(), data, collection, "server", null);
            logger.log(Level.INFO, String.format("Send response to client %s", r.getUser()));
            send(r);
            }
             catch (IOException e) {
            } catch (ClassNotFoundException e) {
            logger.log(Level.WARN, "catch ClassNotFound error");
                throw new RuntimeException(e);
        }
    }
    public void send(Request r) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(r);
        buf.clear();
        buf = ByteBuffer.wrap(bos.toByteArray());
        channel.send(buf, address);
    }

    public String commandExecution(String command, Request data) throws IOException {
        logger.log(Level.INFO, "Executing command");
        if (CreateUsersMap.users.containsKey(data.getUser()))
            collection = CreateUsersMap.users.get(data.getUser());

        CommandInitializator commandsInitializator = new CommandInitializator(collection, ReadFromJson.fileName);
        String r = commandsInitializator.validateAndExecute(data, false);
        //System.out.println(data.getCommand());
        //System.out.println(CreateUsersMap.users.size());
        if (Objects.equals(data.getCommand(), "test server status") || Objects.equals(data.getCommand(), "NEWUSER") || Objects.equals(data.getCommand(), "CONNECTCLIENT")) {
            collection = commandsInitializator.collection;
            //System.out.println(collection);
        }
        else {
            collection = CreateUsersMap.users.get(data.getUser());
        }
        if (collection == null){
            r = commandsInitializator.validateAndExecute(new Request(data.getCommand(), data.getArgs(), null, "NEEDCOLLECTION", null), false);
        }
        //System.out.println(r);
        return r;
    }

    public Request getRequest(){
        return r;
    }
}
