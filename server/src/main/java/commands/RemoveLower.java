package commands;

import helpers.*;
import supportive.MusicBand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This class for remove lower element than input
 *
 * @author frizyy
 */
public class RemoveLower implements CommandInterface{
    private final LinkedHashSet<MusicBand> collection;
    public RemoveLower (LinkedHashSet collection){
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
        List<Integer> indexes = new ArrayList<>();
        boolean isRemoved = false;
        for (int i=0; i <= collection.size() - 1; i++){
            //FindMax maxer = new FindMax();
            //MusicBand maxElement = maxer.getMax(mb.get(i), myMap);
            LinkedHashSet<MusicBand> coll = new LinkedHashSet<>();
            coll.add(mb.get(i));
            coll.add(myMap);
            MusicBand maxElement = coll.stream().max(new AlbumsCountComparator()).orElse(null);
            if (maxElement != mb.get(i))
                indexes.add(i);
            else
                break;
        }
                for (int i = 0; i <= indexes.size() - 1; i++) {
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
     * Description command
     *
     * @return
     */
    @Override
    public String description() {
        System.out.println("Remove elements that are lower than input\nusage remove_lower or remove_greater {element}");
        return null;
    }

    @Override
    public String executeWithObject(String args, MusicBand mbElement) throws IOException {
        /*ClassObjectCreator creator = new ClassObjectCreator(collection);
        MusicBand myMap = creator.create(args);*/
        List<MusicBand> mb = new ArrayList<>(collection);
        List<Integer> indexes = new ArrayList<>();
        boolean isRemoved = false;
        for (int i=0; i <= collection.size() - 1; i++){
            //FindMax maxer = new FindMax();
            //MusicBand maxElement = maxer.getMax(mb.get(i), mbElement);
            LinkedHashSet<MusicBand> coll = new LinkedHashSet<>();
            coll.add(mb.get(i));
            coll.add(mbElement);
            MusicBand maxElement = coll.stream().max(new AlbumsCountComparator()).orElse(null);
            if (maxElement != mb.get(i))
                indexes.add(i);
            else
                break;
        }
        for (int i = 0; i <= indexes.size() - 1; i++) {
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
