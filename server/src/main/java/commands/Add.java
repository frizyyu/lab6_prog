package commands;

import supportive.*;
import helpers.*;

import java.io.IOException;
import java.util.*;

/**
 * This class for add element to collection
 *
 * @author frizyy
 */
public class Add implements CommandInterface {
    private final LinkedHashSet<MusicBand> collection;

    public Add(LinkedHashSet collection) {
        this.collection = collection;
    }

    /**
     * Execute method
     *
     * @param args json-type string for create class instance
     * @return
     * @throws IOException if happened some strange
     */
    @Override
    public String execute(String args) throws IOException {
        long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        ClassObjectCreator creator = new ClassObjectCreator(collection);
        MusicBand myMap = creator.create(args);
        collection.add(myMap);
        SortCollection sorter = new SortCollection(collection);
        sorter.sort(null);
        return "Element added to collection";
    }

    /**
     * Method for print description of command
     *
     * @return
     */
    @Override
    public String description() {
        return "This command adds an item to the collection\nusage: add {element} or add";
    }
    ClassObjectCreator creator;
    SortCollection sorter;
    @Override
    public String executeWithObject(String args, MusicBand mb) throws IOException {
        //System.out.println(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        creator = new ClassObjectCreator(collection);
        MusicBand myMap = creator.addIdAndDate(mb);
        collection.add(myMap);
        //System.out.println(collection.size());
        sorter = new SortCollection(collection);
        sorter.sort(null);
        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        //System.out.println(afterUsedMem);
        return "Element added to collection";
    }
}
