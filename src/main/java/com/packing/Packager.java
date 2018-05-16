package com.packing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fit boxes into container, i.e. perform bin packing to a single container.
 *
 * Thread-safe implementation.
 */

public abstract class Packager {

    /**
     * Logical packager for wrapping preprocessing / optimizations.
     *
     */

    public interface Adapter {
        Container pack(List<BoxItem> boxes, Dimension dimension, long deadline);
    }

    protected final Dimension[] containers;

    protected final boolean rotate3D; // if false, then 2d
    protected final boolean binarySearch;

    /**
     * Constructor
     *
     * @param containers list of containers
     */
    public Packager(List<? extends Dimension> containers) {
        this(containers, true, true);
    }

    /**
     * Constructor
     *
     * @param containers list of containers
     * @param rotate3D whether boxes can be rotated in all three directions (two directions otherwise)
     * @param binarySearch if true, the packager attempts to find the best box given a binary search. Upon finding a match, it searches the preceding boxes as well, until the deadline is passed.
     */

    public Packager(List<? extends Dimension> containers, boolean rotate3D, boolean binarySearch) {
        this.containers = containers.toArray(new Dimension[containers.size()]);
        this.rotate3D = rotate3D;
        this.binarySearch = binarySearch;
    }

    /**
     *
     * Return a container which holds all the boxes in the argument.
     *
     * @param boxes list of boxes to fit in a container
     * @return null if no match
     */

    public ArrayList<Container> pack(List<BoxItem> boxes) {
        return pack(boxes, Long.MAX_VALUE);
    }

    /**
     * Return a list of containers which can potentially hold the boxes.
     *
     * @param boxes list of boxes
     * @return list of containers
     */

    public List<Dimension> filterContainers(List<BoxItem> boxes) {
        long volume = 0;

        for(BoxItem box : boxes) {
            volume += box.getBox().getVolume() * box.getCount();
        }

        int totalContainerVolume = 0;
        List<Dimension> list = new ArrayList<>();
        for(Dimension container : containers) {
            totalContainerVolume += container.getVolume();
//            if(!canHold(container, boxes)) {
//                // discard this container
//                continue;
//            }
            canHold(container, boxes);

            list.add(container);
        }
        for(Iterator<BoxItem> iterator = boxes.iterator(); iterator.hasNext();) {
            BoxItem box = iterator.next();
            if(!box.getBox().canFit) {
                iterator.remove();
            }
        }

        System.out.println("total Container size: " + list.size() + " -- " + boxes.size());

        return list;
    }

    /**
     *
     * Return a container which holds all the boxes in the argument
     *
     * @param boxes list of boxes to fit in a container
     * @param deadline the system time in millis at which the search should be aborted
     * @return index of container if match, -1 if not
     */

    public ArrayList<Container> pack(List<BoxItem> boxes, long deadline) {
        return pack(boxes, filterContainers(boxes), deadline);
    }

    /**
     *
     * Return a container which holds all the boxes in the argument
     *
     * @param boxes list of boxes to fit in a container
     * @param dimensions list of containers
     * @param deadline the system time in milliseconds at which the search should be aborted
     * @return index of container if match, -1 if not
     */

    public ArrayList<Container> pack(List<BoxItem> boxes, List<Dimension> dimensions, long deadline) {
        ArrayList<Container> resultList = new ArrayList<>();

        if(dimensions.isEmpty()) {
            System.out.println("try1");
            return null;
        }
        System.out.println("try2");

        Adapter pack = adapter(boxes);

        if(!binarySearch || dimensions.size() <= 2 || deadline == Long.MAX_VALUE) {
            for (int i = 0; i < dimensions.size(); i++) {

                if(System.currentTimeMillis() > deadline) {
                    break;
                }

                Container result = pack.pack(boxes, dimensions.get(i), deadline);
                for(int j=0; j<boxes.size(); ++j) {
                    System.out.println("ahehe: " + boxes.get(j).getBox().getName() + " -- " + boxes.get(j).getBox().carryForward + " ++ " + boxes.get(j).getBox().toRemove);
                    if(boxes.get(j).getBox().toRemove) {
                        boxes.remove(j);
                    }
                }
                for(BoxItem boxItem : boxes) {
                    System.out.println("AFTERRRRRRRRR: " + boxItem.getBox().getName());
                }

                System.out.println("boxes length: " + boxes.size());
                if(result != null) {
                    System.out.println("retunr?");
                    resultList.add(result);

                    if(i == dimensions.size()-1) {
                        return resultList;
                    }
                }
            }
        }
//        else {
//            Container[] results = new Container[dimensions.size()];
//            boolean[] checked = new boolean[results.length];
//
//            ArrayList<Integer> current = new ArrayList<>();
//            for(int i = 0; i < dimensions.size(); i++) {
//                current.add(i);
//            }
//
//            BinarySearchIterator iterator = new BinarySearchIterator();
//
//            search:
//            do {
//                iterator.reset(current.size() - 1, 0);
//
//                do {
//                    int next = iterator.next();
//                    int mid = current.get(next);
//
//                    Container result = pack.pack(boxes, dimensions.get(mid), deadline);
//
//                    checked[mid] = true;
//                    if(result != null) {
//                        results[mid] = result;
//
//                        iterator.lower();
//                    } else {
//                        iterator.higher();
//                    }
//                    if(System.currentTimeMillis() > deadline) {
//                        break search;
//                    }
//                } while(iterator.hasNext());
//
//
//                // halt when have a result, and checked all containers at the lower indexes
//                for (int i = 0; i < current.size(); i++) {
//                    Integer integer = current.get(i);
//                    if(results[integer] != null) {
//                        // remove end items; we already have a better match
//                        while(current.size() > i) {
//                            current.remove(current.size() - 1);
//                        }
//                        break;
//                    }
//
//                    // remove item
//                    if(checked[integer]) {
//                        current.remove(i);
//                        i--;
//                    }
//                }
//            } while(!current.isEmpty());
//
//            for(int i = 0; i < results.length; i++) {
//                if(results[i] != null) {
//                    return results[i];
//                }
//            }
//        }
        System.out.println("here?");
        return null;
    }

    protected abstract Adapter adapter(List<BoxItem> boxes);

    protected boolean canHold(Dimension containerBox, List<BoxItem> boxes) {
        for(BoxItem box : boxes) {
            if(rotate3D) {
                if(!containerBox.canHold3D(box.getBox())) {
                    box.getBox().canFit=false;
//                    return false;
                }
            } else {
                if(!containerBox.canHold2D(box.getBox())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<Placement> getPlacements(int size) {
        // each box will at most have a single placement with a space (and its remainder).
        List<Placement> placements = new ArrayList<Placement>(size);

        for(int i = 0; i < size; i++) {
            Space a = new Space();
            Space b = new Space();
            a.setRemainder(b);
            b.setRemainder(a);

            placements.add(new Placement(a));
        }
        return placements;
    }


}