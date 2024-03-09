package helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jsonHelper.ReadFromJson;
import supportive.MusicBand;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateUsersMap {
    public static HashMap<String, LinkedHashSet<MusicBand>> users;
    String readedFile;

    public CreateUsersMap() throws IOException {
        users = new HashMap<String, LinkedHashSet<MusicBand>>();
        /*FileInputStream inputStream = new FileInputStream(new File("users.json").getAbsolutePath());
        BufferedInputStream buffInputStr = null;
        buffInputStr
                = new BufferedInputStream(
                inputStream);

        while (buffInputStr.available() > 0) {


            char c = (char) buffInputStr.read();

            readedFile += c;
        }
        System.out.println(readedFile.replace("null", ""));
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonMappingException.Reference reference = mapper.readValue(readedFile, JsonMappingException.Reference.class);
        System.out.println(reference + " ASDADS");
        HashMap car = mapper.readValue(new File("users.json"), HashMap.class);
        System.out.println(car);*/
        /*HashMap map = new ObjectMapper().readValue(readedFile, HashMap.class);
        //System.out.println(map.);
        for (int i=0; i<map.size(); i++){
            LinkedHashSet sett = LinkedHashSet.class.cast(map.get(map.keySet().toArray()[i].toString()));
            ArrayList mb = new ArrayList<>(sett);
            LinkedHashSet<MusicBand> rs = new LinkedHashSet<>();
            for (int j=0; j < mb.size(); j++){
                rs.add((MusicBand) mb.get(j));
            }
            users.put(map.keySet().toArray()[i].toString(), rs);
        }
        System.out.println(users);*/
    }
}
