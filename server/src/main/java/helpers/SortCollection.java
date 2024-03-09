package helpers;
import supportive.*;

import commands.CommandInterface;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.TreeSet;

public class SortCollection{
    /**
     *
     *
     * @author frizyyu
     * @version 1.0
     */
    private final LinkedHashSet<MusicBand> collection;
    public static Comparator<MusicBand> c;
    public static Comparator<MusicBand> cId;

    public SortCollection(LinkedHashSet collection){
        this.collection = collection;
    }

    public void sort(String args) {
        Comparator<Coordinates> coordsC = Comparator
                .comparing(Coordinates::getY, Comparator.naturalOrder())
                .thenComparing(Coordinates::getX, Comparator.naturalOrder());

        Comparator<Studio> studioC = Comparator
                .comparing(Studio::getAddress, Comparator.naturalOrder())
                .thenComparing(Studio::getName, Comparator.naturalOrder());

        c = Comparator
                .comparing(MusicBand::getStudio, studioC)
                .thenComparing(MusicBand::getGenre, Comparator.naturalOrder())
                .thenComparing(MusicBand::getAlbumsCount, Comparator.naturalOrder())
                .thenComparing(MusicBand::getNumberOfParticipants, Comparator.naturalOrder())
                //.thenComparing(MusicBand::getCreationDate, Comparator.naturalOrder())
                .thenComparing(MusicBand::getCoordinates, coordsC)
                .thenComparing(MusicBand::getName, Comparator.naturalOrder())
                .thenComparing(MusicBand::getId, Comparator.naturalOrder());

        Comparator<MusicBand> c1 = Comparator.comparing(MusicBand::getAlbumsCount);
        NavigableSet<MusicBand> sortedSet = new TreeSet<>(c1);
        sortedSet.addAll(collection);
        collection.clear();
        collection.addAll(sortedSet);
    }

    public void sortById(String args) {
        cId = Comparator
                .comparing(MusicBand::getId, Comparator.naturalOrder());

        NavigableSet<MusicBand> sortedSet = new TreeSet<>(cId);
        sortedSet.addAll(collection);
        collection.clear();
        collection.addAll(sortedSet);
    }
}
