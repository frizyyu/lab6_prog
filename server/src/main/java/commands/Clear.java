package commands;

import helpers.ContinueAction;
import supportive.MusicBand;

import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * This class for clear collection
 *
 * @author frizyy
 */
public class Clear implements CommandInterface{
    private final LinkedHashSet<MusicBand> collection;

    /**
     *
     * @param collection our collection
     */
    public Clear(LinkedHashSet collection){
        this.collection = collection;
    }

    /**
     * Execute method
     *
     * @param args null, because command hasn`t got arguments
     * @return
     * @throws IOException if happened some strange
     */
    @Override
    public String execute(String args) throws IOException {
        collection.clear();
        return "Collection has been cleared";
        //return "Are you sure about deleting the collection? y/n\n>>> ";
        //System.out.print(">>> ");
        /*ContinueAction cont = new ContinueAction();
        int c = cont.continueAction("Collection has been cleared", "The collection has not been cleared", "Action skipped. Invalid answer");
        if (c == 1){
            collection.clear();
            return "Collection has been cleared";
        } else if (c == -1) {
            return "The collection has not been cleared";
        }
        else
            return "Action skipped. Invalid answer";
        //return args;*/
    }
    /**
     * Method for print description of command
     *
     * @return
     */
    @Override
    public String description() {
        return "Clear the collection\nusage: clear";
    }

    @Override
    public String executeWithObject(String args, MusicBand musicBand) throws IOException {
        return null;
    }
}
