package commandHelpers;
import com.sun.tools.javac.Main;
import helpers.*;
import commands.*;
import jsonHelper.ReadFromJson;
import jsonHelper.WriteToJson;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import supportive.MusicBand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static jsonHelper.ReadFromJson.fileName;

/**
 * This class for find command from input string
 *
 * @author frizyy
 */
public class CommandInitializator {
    private final HashMap<String, Object> commands;
    public static List<String> scripts = new ArrayList<>();
    public LinkedHashSet<MusicBand> collection;
    List<String> needToParse = new ArrayList<>();
    String n;
    Logger logger = new helpers.Logger().getLogger();

    public CommandInitializator(LinkedHashSet<MusicBand> collection, String fileName){
        this.collection = collection;
        needToParse.add("add");
        needToParse.add("update");
        needToParse.add("remove_greater");
        needToParse.add("remove_lower");
        needToParse.add("add_if_max");
        needToParse.add("remove_by_id");
        needToParse.add("clear");
        needToParse.add("execute_script");

        commands = new HashMap<>();
        commands.put("help", new Help());
        commands.put("add", new Add(collection));
        commands.put("show", new Show(collection));
        commands.put("save", new Save(collection, fileName));
        commands.put("info", new Info(collection));
        commands.put("clear", new Clear(collection));
        commands.put("update", new Update(collection));
        commands.put("exit", new Exit(collection));
        commands.put("remove_by_id", new RemoveById(collection));
        commands.put("add_if_max", new AddIfMax(collection));
        commands.put("remove_greater", new RemoveGreater(collection));
        commands.put("remove_lower", new RemoveLower(collection));
        commands.put("average_of_albums_count", new AverageOfAlbumsCount(collection));
        commands.put("filter_by_albums_count", new FilterByAlbumsCount(collection));
        commands.put("filter_contains_name", new FilterContainsName(collection));
        commands.put("execute_script", new ExecuteScript(collection, fileName));
        commands.put("CONNECTCLIENT", new ClienConnect(collection));
    }

    /**
     * @param inp       string for parsing
     * @param onlyCheck in order to know if it is necessary to output text to the console
     * @return
     * @throws IOException if happened some strange
     */
    public String validateAndExecute(Request inp, boolean onlyCheck) throws IOException { // returns String
                if (Objects.equals(inp.getCommand(), "testing connection")){
                    //logger.log(Level.INFO, String"Client sending request");
                    return "|";
                }
                if (Objects.equals(inp.getCommand(), "test server status")){
                    logger.log(Level.INFO, "Client connected");
                    return "CONNECTED";
                }
                else if (Objects.equals(inp.getUser(), "NEEDCOLLECTION")){
                    logger.log(Level.INFO, "Server has been restarted. Get data from client");
                    return String.format("NEEDCOLLECTION|%s", inp.getArgs());
                }
                else if (Objects.equals(inp.getCommand(), "send collection")){
                    logger.log(Level.INFO, "Client connected");
                    collection = inp.getElement();
                    fileName = inp.getArgs();
                    CreateUsersMap.users.put(inp.getUser(), collection);
                    return "CONNECTED";
                }
                else if (Objects.equals(inp.getCommand(), "CONNECTCLIENT")){ //if client connect
                    /////// load collection from file ////////
                    logger.log(Level.INFO, "Load data from file to client");
                    LinkedHashSet<MusicBand> res;
                    String fname = null;
                    File tmpFile = new File(new File(inp.getUser(), "tmp.json").getAbsolutePath());
                    if (inp.getArgs().length() != 0)
                        fileName = inp.getArgs().replace(".json", "");
                    else
                        fileName = "";
                    if (tmpFile.exists() && !inp.getArgs().contains("|Y")) {
                        if (inp.getArgs().contains("LOAD|")) {
                            fileName = "tmp";
                            ReadFromJson readertmp = new ReadFromJson();
                            collection = readertmp.read(inp.getUser(), fileName);
                            CreateUsersMap.users.put(inp.getUser(), collection);
                            fname = null;
                            ReadFromJson.fileName = inp.getArgs().replace("LOAD|", "");
                        }
                        else if (!inp.getArgs().contains("|Y")){
                            return String.format("%s|FILENAME %s", false, fileName);
                        }
                    } else if (inp.getArgs().contains("|Y")) {
                        fileName = inp.getArgs().replace("|Y", "");
                        try {
                            Files.delete(Path.of(new File(inp.getUser(), "tmp.json").getAbsolutePath()));
                        }
                        catch (Exception ignored){}
                        while (true) {
                            if (Objects.equals(fileName, "tmp")) {
                                return String.format("%s|FILENAME %s", false, fileName);
                            } else
                                break;
                        }
                        ReadFromJson reader = new ReadFromJson();
                        if (fileName.contains("|CREATEFILE")) {
                            fileName = fileName.replace("|CREATEFILE", "");
                            CreateIfNotExist creator = new CreateIfNotExist();
                            creator.create(true, fileName, inp.getUser());
                        }
                        else if (fileName.contains("|NCREATEFILE")) {
                            fileName = fileName.replace("|NCREATEFILE", "");
                            fname = fileName;
                        }
                        res = reader.read(inp.getUser(), fileName);

                        if (res == null){
                            return String.format("%s|NEEDCREATE", fileName);
                        }
                        else if (res.size() == 1) {
                            List<MusicBand> mb = new ArrayList<>(res);
                            if (mb.get(0).getId() == -1)
                                return "ERRORINFILE";
                        }
                        collection = res;
                        CreateUsersMap.users.put(inp.getUser(), collection);
                        logger.log(Level.INFO, "File loaded");
                    }
                    else{
                        ReadFromJson reader = new ReadFromJson();
                        if (fileName.contains("|CREATEFILE")) {
                            fileName = fileName.replace("|CREATEFILE", "");
                            CreateIfNotExist creator = new CreateIfNotExist();
                            creator.create(true, fileName, inp.getUser());
                        }
                        else if (fileName.contains("|NCREATEFILE")) {
                            fileName = fileName.replace("|NCREATEFILE", "");
                            fname = fileName;
                        }
                        res = reader.read(inp.getUser(), fileName);

                        if (res == null){
                            return String.format("%s|NEEDCREATE", fileName);
                        }
                        else if (res.size() == 1) {
                            List<MusicBand> mb = new ArrayList<>(res);
                            if (mb.get(0).getId() == -1)
                                return "ERRORINFILE";
                        }
                        collection = res;
                        CreateUsersMap.users.put(inp.getUser(), collection);
                        logger.log(Level.INFO, "File loaded");
                    }

                    return String.format("%s|FILENAME %s", true, fileName);
                }
                else {
                    CommandInterface command = (CommandInterface) commands.get(inp.getCommand().toLowerCase());
                    if (inp.getmbElement() != null && !inp.getArgs().contains("-h"))
                    {
                        n = command.executeWithObject(inp.getArgs(), inp.getmbElement());
                    }
                    else {
                        switch (inp.getArgs()) {
                            case "" -> n = command.execute(null);
                            case "-h" -> n = command.description();
                            default -> n = command.execute(inp.getArgs());
                        }
                    }
                    WriteToJson savertmp = new WriteToJson(collection, "tmp.json", inp.getUser());
                    String currCommand;
                    boolean needSaveTmp = false;
                    if (inp.getCommand().length() != 0) {
                        currCommand = inp.getCommand();
                        if (needToParse.contains(currCommand.split(" ")[0].strip().toLowerCase())) {
                            needSaveTmp = true;
                        }
                        if (needSaveTmp) {
                            savertmp.write();
                        }
                    }
                }
        return n;
    }
}
