package helpers;

import commandHelpers.CommandInitializator;
import commands.Save;
import helpers.Request;
import jsonHelper.ReadFromJson;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
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
    ByteBuffer buf = ByteBuffer.allocate(65536);
    HashMap<String, List<String>> ar = new HashMap<>();
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
                    //System.out.println("Saving and exit");
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
            //buf.clear();
            ByteBuffer buf = ByteBuffer.allocate(65536);
            address = channel.receive(buf);
            byte[] bts = buf.array();
            ByteArrayInputStream bais = new ByteArrayInputStream(bts);
            ObjectInputStream ois = new ObjectInputStream(bais);
            r = (Request) ois.readObject();
            logger.log(Level.INFO, String.format("Get request from client %s", r.getUser()));
            //выполнение команды
            String data = commandExecution(r.getCommand(), r);
            r = new Request(r.getCommand(), data, new LinkedHashSet<>(), r.getUser(), null);
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
        if (r.getArgs().length() * 2 > 65536) { //обработка не тут
            ar.put(r.getUser(), new ArrayList<>());
            //ar = new ArrayList<>();
            int n = 8192;
            //System.out.println(r.getArgs().length());
            //System.out.println((Math.ceil((float) r.getArgs().length() / n))); //round
            for (int i = 0; i < (Math.ceil(r.getArgs().length() / (float) n)); i++){
                String res;
                try {
                    res = String.format("%s", r.getArgs().substring(n * i, n * i + n));
                } catch (StringIndexOutOfBoundsException er) {
                    res = String.format("%s", r.getArgs().substring(n * i));
                }
                //ar.put(r.getUser(), ar.get(r.getUser()).add(res));
                ar.get(r.getUser()).add(res);
                //System.out.println(ar);
                //Request request = new Request(r.getCommand(), res, new LinkedHashSet<>(), r.getUser(), null);
                /*oos.writeObject(request);
                //buf.clear();
                buf = ByteBuffer.wrap(bos.toByteArray());
                //System.out.println(bos.toByteArray().length);
                System.out.println(r.getArgs().length());
                //System.out.println(r.getArgs());
                System.out.println(res);
                channel.send(buf, address);
                channel.send(buf, address);*/
            }
            //System.out.println(ar);
            r.setArgs(String.format("%sLARGEDATA", ar.get(r.getUser()).size()));
            //r.setArgs("A large amount of data, it is impossible to transfer");
            //разделение на несколько датаграмм и передача их по очереди
        }
        if (r.getArgs().equals("SENDPLS")){
            //System.out.println(ar.get("artem"));
            r.setArgs(ar.get(r.getUser()).get(Integer.parseInt(r.getCommand())));
        }
        if (r.getArgs().equals("STOPSENDING")){
            //System.out.println("ASD");
            r.setArgs("|");
            ar.remove(r.getUser());
        }
        oos.writeObject(r);
        //buf.clear();
        buf = ByteBuffer.wrap(bos.toByteArray());
        //System.out.println(bos.toByteArray().length);
        channel.send(buf, address);
    }
    CommandInitializator commandsInitializator;
    public String commandExecution(String command, Request data) throws IOException {
        logger.log(Level.INFO, "Executing command");
        if (CreateUsersMap.users.containsKey(data.getUser()))
            collection = CreateUsersMap.users.get(data.getUser());

        commandsInitializator = new CommandInitializator(collection, ReadFromJson.fileName);
        String r = commandsInitializator.validateAndExecute(data, false);
        //System.out.println(data.getCommand());
        //System.out.println(CreateUsersMap.users.size());
        if (Objects.equals(data.getCommand(), "test server status") || Objects.equals(data.getCommand(), "NEWUSER") || Objects.equals(data.getCommand(), "CONNECTCLIENT")) {
            collection = commandsInitializator.collection; //обработка где-то тут
            //System.out.println(collection);
        }
        else {
            collection = CreateUsersMap.users.get(data.getUser());
        }
        if (collection == null){
            CreateUsersMap creator = new CreateUsersMap();
            collection = CreateUsersMap.users.get(data.getUser());
            //r = commandsInitializator.validateAndExecute(new Request(data.getCommand(), data.getArgs(), null, "NEEDCOLLECTION", null), false);
        }
        //System.out.println(r);
        return r;
    }

    public Request getRequest(){
        return r;
    }
}
