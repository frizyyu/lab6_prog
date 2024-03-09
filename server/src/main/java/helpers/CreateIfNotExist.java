package helpers;

import helpers.ContinueAction;
import jsonHelper.ReadFromJson;
import supportive.MusicBand;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class CreateIfNotExist {
    public void create(Boolean createOrNot, String fileName, String path) throws IOException {
        //fileName =fileName;
        
        if (createOrNot){
            fileName += ".json";
            //System.out.println(new File(path, fileName).getAbsolutePath());
            File f = new File(new File(path, fileName).getAbsolutePath());
            if (!f.exists()){
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            /*FileOutputStream out = new FileOutputStream(String.format("%s", new File(path, fileName).getAbsolutePath()).replace("\\", "/"));
            out.write("".getBytes());
            out.close();*/
            //inputStream = new FileInputStream(new File(fileName).getAbsolutePath());
        }
    }
}
