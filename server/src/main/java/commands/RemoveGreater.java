package commands;

import helpers.*;
import supportive.MusicBand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This class for remove greater elements than input
 *
 * @author frizyy
 */
public class RemoveGreater implements CommandInterface{
    private final LinkedHashSet<MusicBand> collection;
    public RemoveGreater(LinkedHashSet collection){
        this.collection = collection;
    }
    /**
     * Execute command
     *
     * @param args string with arguments
     * @return
     * @throws IOException if happened some strange
     */
    @Override
    public String execute(String args) throws IOException {
        ClassObjectCreator creator = new ClassObjectCreator(collection);
        MusicBand myMap = creator.create(args);
        List<MusicBand> mb = new ArrayList<>(collection);
        //FindMax maxer = new FindMax();
        boolean isRemoved = false;
        int ind=collection.size();
        for (int i=0; i <= collection.size() - 1; i++){
            //MusicBand maxElement = maxer.getMax(mb.get(i), myMap);
            LinkedHashSet<MusicBand> coll = new LinkedHashSet<>();
            coll.add(mb.get(i));
            coll.add(myMap);
            MusicBand maxElement = coll.stream().max(new AlbumsCountComparator()).orElse(null);
            if (maxElement == mb.get(i) && ind == collection.size()) {
                ind = i+1;
                break;
            }

        }
        for (int i = ind; i <= collection.size(); i++) {
            collection.remove(mb.get(i));
            isRemoved = true;
            //System.out.printf("Element with id %s has been removed\n", mb.get(i).getId());
        }
        if (isRemoved)
            return "Elements has been removed";
        else
            return "Elements has not been removed";
    }

    /**
     * Description of command
     *
     * @return
     */
    @Override
    public String description() {
        return "Remove elements that are greater than input\nusage remove_greater {element} or remove_greater";
    }

    @Override
    public String executeWithObject(String args, MusicBand mbElement) throws IOException {

        /*ClassObjectCreator creator = new ClassObjectCreator(collection);
        MusicBand myMap;
        myMap = creator.addIdAndDate(mbElement);*/

        List<MusicBand> mb = new ArrayList<>(collection);
        //FindMax maxer = new FindMax();
        boolean isRemoved = false;
        int ind=collection.size();
        for (int i=0; i <= collection.size() - 1; i++){
            //MusicBand maxElement = maxer.getMax(mb.get(i), mbElement);
            LinkedHashSet<MusicBand> coll = new LinkedHashSet<>();
            coll.add(mb.get(i));
            coll.add(mbElement);
            MusicBand maxElement = coll.stream().max(new AlbumsCountComparator()).orElse(null);
            //System.out.println(maxElement);
            if (maxElement == mb.get(i) && ind == collection.size()) {
                ind = i+1;
                break;
            }

        }
        for (int i = ind; i <= collection.size(); i++) {
            collection.remove(mb.get(i));
            isRemoved = true;
            //System.out.printf("Element with id %s has been removed\n", mb.get(i).getId());
        }
        if (isRemoved)
            return "Elements has been removed";
        else
            return "Elements has not been removed";
    }
}
