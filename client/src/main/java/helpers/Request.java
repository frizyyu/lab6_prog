package helpers;

import supportive.MusicBand;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class Request implements Serializable {
    private static final long serialVersionUID = 666L;
    String command;
    String args;
    String user;
    MusicBand mbElement;

    LinkedHashSet<MusicBand> element;
    public Request(String command, String args, LinkedHashSet<MusicBand> element, String user, MusicBand mbElement){
        this.mbElement = mbElement;
        this.command = command;
        this.args = args;
        this.element = element;
        this.user = user;
    }

    public void setCommand(String command){
        this.command = command;
    }
    public void setArgs(String args){
        this.args = args;
    }
    public void setElement(LinkedHashSet<MusicBand> element){
        this.element = element;
    }
    public void setUser(String user){
        this.user = user;
    }
    public void setMbElement(MusicBand mbElement){
        this.mbElement = mbElement;
    }
    public LinkedHashSet<MusicBand> getElement(){
        return this.element;
    }
    public String getCommand(){
        return this.command;
    }
    public String getArgs(){
        return this.args;
    }
    public String getUser(){
        return this.user;
    }
    public MusicBand getmbElement(){
        return this.mbElement;
    }
}
