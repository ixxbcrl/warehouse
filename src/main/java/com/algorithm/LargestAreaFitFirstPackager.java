package com.algorithm;

import com.packing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.packing.Packager.Adapter;


/**
 * Fit boxes into container, i.e. perform bin packing to a single container.
 * <br><br>
 * Thread-safe implementation.
 */

public class LargestAreaFitFirstPackager extends Packager implements Adapter {

    protected final boolean footprintFirst;

    /**
     * Constructor
     *
     * @param containers list of containers
     */
    public LargestAreaFitFirstPackager(List<? extends Dimension> containers) {
        this(containers, true, true, true);
    }


    /**
     * Constructor
     *
     * @param containers list of containers
     * @param footprintFirst start with box which has the largest footprint. If not, the highest box is first.
     * @param rotate3D whether boxes can be rotated in all three directions (two directions otherwise)
     * @param binarySearch if true, the packager attempts to find the best box given a binary search. Upon finding a container that can hold the boxes, given time, it also tries to find a better match.
     */

    public LargestAreaFitFirstPackager(List<? extends Dimension> containers, boolean rotate3D, boolean footprintFirst, boolean binarySearch) {
        super(containers, rotate3D, binarySearch);

        this.footprintFirst = footprintFirst;
    }

    /**
     *
     * Return a container which holds all the boxes in the argument
     *
     * @param items list of boxes to fit in a container
     * @param dimension the container to fit within
     * @param deadline the system time in millis at which the search should be aborted
     * @return null if no match, or deadline reached
     */

    public Container pack(List<BoxItem> items, Dimension dimension, long deadline) {
        List<Box> containerProducts = new ArrayList<Box>(items.size() * 2);

        for(BoxItem item : items) {
            Box box = item.getBox();
            containerProducts.add(box);
            for(int i = 1; i < item.getCount(); i++) {
                containerProducts.add(box.clone()); //to repeat Boxes in BoxItems
            }
        }
        System.out.println("containerProd size: " + containerProducts.size());

        Container holder = new Container(dimension);

      System.out.println("HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE: "+ holder.getType());

        Dimension freeSpace = dimension;

        while(!containerProducts.isEmpty()) {
            if(System.currentTimeMillis() > deadline) {
                // fit2d below might have returned due to deadline

                break;
            }

            // choose the box with the largest surface area, that fits
            // if the same then the one with minimum height

            // use a special case for boxes with full height
            Box currentBox = null;
            int currentIndex = -1;
            System.out.println("gies in here first");

            boolean fullHeight = false;
            for (int i = 0; i < containerProducts.size(); i++) {
                System.out.println("goes in multiple");
                Box box = containerProducts.get(i);

                boolean fits;
                if(rotate3D) {
                    fits = box.rotateLargestFootprint3D(freeSpace);
                } else {
                    fits = box.fitRotate2D(freeSpace);
                }
                if(fits) {
                    System.out.println("which box fits: " + box.getName());
                    if(currentBox == null) {
                        currentBox = box;
                        currentIndex = i;

                        fullHeight = box.getHeight() == freeSpace.getHeight();
                    } else {
                        if(fullHeight) {
                            if(box.getHeight() == freeSpace.getHeight()) {
                                if(currentBox.getFootprint() < box.getFootprint()) {
                                    currentBox = box;
                                    currentIndex = i;
                                }
                            }
                        } else {
                            if(box.getHeight() == freeSpace.getHeight()) { //check if full height
                                fullHeight = true;

                                currentBox = box;
                                currentIndex = i;
                            } else if(footprintFirst) {
                                if(currentBox.getFootprint() < box.getFootprint()) {
                                    currentBox = box;
                                    currentIndex = i;
                                } else if(currentBox.getFootprint() == box.getFootprint() && currentBox.getHeight() < box.getHeight()) {
                                    currentBox = box;
                                    currentIndex = i;
                                }
                            } else {
                                if(currentBox.getHeight() < box.getHeight()) {
                                    currentBox = box;
                                    currentIndex = i;
                                } else if(currentBox.getHeight() == box.getHeight() && currentBox.getFootprint() < box.getFootprint()) {
                                    currentBox = box;
                                    currentIndex = i;
                                }
                            }
                        }
                    }
                } else {
                    // no fit in the current container within the remaining space
                    // try the next container
                    System.out.println("it comes here" + box.getName());
                    box.carryForward=true;
//                    return null;
                }
            }

          Predicate<Box> box2 = e -> e.carryForward;
          for(Box box : containerProducts) {

            System.out.println("remaining box attribute: " + box.carryForward + " -- " + box.getName());
          }
          if(containerProducts.stream().allMatch(box2)) {
            return holder;
          }

//            System.out.println("current box is? " + currentBox.getName());

            // current box should have the optimal orientation already
            // create a space which holds the full level
            // height is truncated coz you only have that much space to work with
            Space levelSpace = new Space(
                    dimension.getWidth(),
                    dimension.getDepth(),
                    currentBox.getHeight(),
                    0,
                    0,
                    holder.getStackHeight()
            );
            System.out.println("container count: " + containerProducts.size());
            holder.addLevel(); //adds a Level element to the current container
            containerProducts.get(currentIndex).toRemove=true;
          System.out.println("container listssss: " + containerProducts.get(currentIndex));
            containerProducts.remove(currentIndex); //removing i-th object coz we added it in levelSpace

            System.out.println("container count2: " + containerProducts.size() + " -- " + currentBox.getName());



            fit2D(containerProducts, holder, currentBox, levelSpace, deadline);

            freeSpace = holder.getFreeSpace();

//            Predicate<Box> box1 = e -> e.carryForward;
//            for(Box box : containerProducts) {
//
//                System.out.println("remaining box attribute: " + box.carryForward + " -- " + box.getName());
//            }
//            if(containerProducts.stream().allMatch(box1)) {
//                return holder;
//            }
        }

        return holder;
    }

    /**
     * Remove from list, more explicit implementation than {@linkplain List#remove} with no equals.
     * @param containerProducts list of products
     * @param currentBox item to remove
     */

    protected void removeIdentical(List<Box> containerProducts, Box currentBox) {
        for(int i = 0; i < containerProducts.size(); i++) {
            if(containerProducts.get(i) == currentBox) {
                containerProducts.remove(i);

                return;
            }
        }
        throw new IllegalArgumentException();
    }

    protected void fit2D(List<Box> containerProducts, Container holder, Box usedSpace, Space freeSpace, long deadline) {
        if(rotate3D) {
          System.out.println("ROOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

          // minimize footprint
            usedSpace.fitRotate3DSmallestFootprint(freeSpace);
        }
      System.out.println("CURRENT BOX MEASUREMENTSSSS: " + usedSpace.toString());
        System.out.println("free space: " + freeSpace.toString());

        // add used space box now, but possibly rotate later - this depends on the actual remaining free space selected further down
        // there is up to possible 4 free spaces, 2 in which the used space box is rotated
        holder.add(new Placement(freeSpace, usedSpace)); // adds the box to the previously added Level object. Box is now PLACED
      for(Level levels : holder.getLevels()) {
        System.out.println(" HOLDERRR: " + levels.toString());
      }


//      containerProducts.get(currentIndex).toRemove=true;

        if(containerProducts.isEmpty()) { //possible to add checking for remaining space
            // no additional boxes
            // just make sure the used space fits in the free space
          System.out.println("list is empty");
            usedSpace.fitRotate2D(freeSpace);

            return;
        }
        for(Box box : containerProducts) {
            System.out.println("boxAASSSS: " + box.getName() + " -- " + holder.getName() + " -- " + usedSpace.getName());
        }

        if(System.currentTimeMillis() > deadline) {
            return;
        }

        Space[] spaces = getFreespaces(freeSpace, usedSpace);

        Placement nextPlacement = bestVolumePlacement(containerProducts, spaces);
        if(nextPlacement == null) {
          System.out.println("ohno");
            // no additional boxes OR the specified box/boxes can't fit in the remaining space
            // just make sure the used space fits in the free space
            usedSpace.fitRotate2D(freeSpace);

            return;
        }

        // check whether the selected free space requires the used space box to be rotated
        if(nextPlacement.getSpace() == spaces[2] || nextPlacement.getSpace() == spaces[3]) {
            // the desired space implies that we rotate the used space box
            usedSpace.rotate2D();
        }

        // holder.validateCurrentLevel(); // uncomment for debugging

        removeIdentical(containerProducts, nextPlacement.getBox());

        // attempt to fit in the remaining (usually smaller) space first

        // stack in the 'sibling' space - the space left over between the used box and the selected free space
        Space remainder = nextPlacement.getSpace().getRemainder();
        if(!remainder.isEmpty()) {
            Box box = bestVolume(containerProducts, remainder);
            if(box != null) {
                removeIdentical(containerProducts, box);

                fit2D(containerProducts, holder, box, remainder, deadline);
            }
        }

        // fit the next box in the selected free space
        fit2D(containerProducts, holder, nextPlacement.getBox(), nextPlacement.getSpace(), deadline);

        // TODO use free spaces between box and level, if any
    }

    protected Space[] getFreespaces(Space freespace, Box used) {

        // Two free spaces, on each rotation of the used space.
        // Height is always the same, used box is assumed within free space height.
        // First:
        // ........................  ........................  .............
        // .                      .  .                      .  .           .
        // .                      .  .                      .  .           .
        // .          A           .  .          A           .  .           .
        // .                      .  .                      .  .           .
        // .                B     .  .                      .  .    B      .
        // ............           .  ........................  .           .
        // .          .           .                            .           .
        // .          .           .                            .           .
        // ........................                            .............
        //
        // Second:
        //
        // ........................   ........................  ..................
        // .                      .   .                      .  .                .
        // .          C           .   .         C            .  .                .
        // .                      .   .                      .  .                .
        // .......                .   ........................  .                .
        // .     .       D        .                             .        D       .
        // .     .                .                             .                .
        // .     .                .                             .                .
        // .     .                .                             .                .
        // ........................                             ..................
        //
        // So there is always a 'big' and a 'small' leftover area (the small is not shown).

        Space[] freeSpaces = new Space[4];
        if(freespace.getWidth() >= used.getWidth() && freespace.getDepth() >= used.getDepth()) {

            // if B is empty, then it is sufficient to work with A and the other way around

            // B
            if(freespace.getWidth() > used.getWidth()) {
                Space right = new Space(
                        freespace.getWidth() - used.getWidth(), freespace.getDepth(), freespace.getHeight(),
                        freespace.getX() + used.getWidth(), freespace.getY(), freespace.getZ()
                );

                Space rightRemainder = new Space(
                        used.getWidth(), freespace.getDepth() - used.getDepth(), freespace.getHeight(),
                        freespace.getX(), freespace.getY()+ used.getDepth(), freespace.getZ()
                );
                right.setRemainder(rightRemainder);
                rightRemainder.setRemainder(right);
                freeSpaces[0] = right;
            }

            // A
            if(freespace.getDepth() > used.getDepth()) {
                Space top = new Space(
                        freespace.getWidth(), freespace.getDepth() - used.getDepth(), freespace.getHeight(),
                        freespace.getX(), freespace.getY() + used.depth, freespace.getHeight()
                );
                Space topRemainder = new Space(
                        freespace.getWidth() - used.getWidth(), used.getDepth(), freespace.getHeight(),
                        freespace.getX() + used.getWidth(), freespace.getY(), freespace.getZ()
                );
                top.setRemainder(topRemainder);
                topRemainder.setRemainder(top);
                freeSpaces[1] = top;
            }
        }

        if(freespace.getWidth() >= used.getDepth() && freespace.getDepth() >= used.getWidth()) {
            // if D is empty, then it is sufficient to work with C and the other way around

            // D
            if(freespace.getWidth() > used.getDepth()) {
                Space right = new Space(
                        freespace.getWidth() - used.getDepth(), freespace.getDepth(), freespace.getHeight(),
                        freespace.getX() + used.getDepth(), freespace.getY(), freespace.getHeight()
                );
                Space rightRemainder = new Space(
                        used.getDepth(), freespace.getDepth() - used.getWidth(), freespace.getHeight(),
                        freespace.getX(), freespace.getY() + used.getWidth(), freespace.getZ()
                );
                right.setRemainder(rightRemainder);
                rightRemainder.setRemainder(right);
                freeSpaces[2] = right;
            }

            // C
            if(freespace.getDepth() > used.getWidth()) {
                Space top = new Space(
                        freespace.getWidth(), freespace.getDepth() - used.getWidth(), freespace.getHeight(),
                        freespace.getX(), freespace.getY() + used.getWidth(), freespace.getHeight()
                );
                Space topRemainder = new Space(
                        freespace.getWidth() - used.getDepth(), used.getWidth(), freespace.getHeight(),
                        freespace.getX() + used.getDepth(), freespace.getY(), freespace.getZ()
                );
                top.setRemainder(topRemainder);
                topRemainder.setRemainder(top);
                freeSpaces[3] = top;
            }
        }
        return freeSpaces;
    }

    protected Box bestVolume(List<Box> containerProducts, Space space) {

        Box bestBox = null;
        for(Box box : containerProducts) {

            if(rotate3D) {
                if(box.canFitInside3D(space)) {
                    if(bestBox == null) {
                        bestBox = box;

                        bestBox.fitRotate3DSmallestFootprint(space);
                    } else if(bestBox.getVolume() < box.getVolume()) {
                        bestBox = box;

                        bestBox.fitRotate3DSmallestFootprint(space);
                    } else if(bestBox.getVolume() == box.getVolume()) {
                        // determine lowest fit
                        box.fitRotate3DSmallestFootprint(space);

                        if(box.getFootprint() < bestBox.getFootprint()) {
                            bestBox = box;
                        }
                    }
                }
            } else {
                if(box.canFitInside2D(space)) {
                    if(bestBox == null) {
                        bestBox = box;
                    } else if(bestBox.getVolume() < box.getVolume()) {
                        bestBox = box;
                    } else if(bestBox.getVolume() < box.getVolume()) {
                        // TODO use the aspect ratio in some meaningful way
                    }
                }
            }
        }
        return bestBox;
    }

    protected Placement bestVolumePlacement(List<Box> containerProducts, Space[] spaces) {

        Box bestBox = null;
        Space bestSpace = null;
        for(Space space : spaces) {
            if(space == null) {
                continue;
            }
            for(Box box : containerProducts) {

                if(rotate3D) {
                    if(box.canFitInside3D(space)) {
                        if(bestBox == null) {
                            bestBox = box;
                            bestSpace = space;

                            bestBox.fitRotate3DSmallestFootprint(bestSpace);
                        } else if(bestBox.getVolume() < box.getVolume()) {
                            bestBox = box;
                            bestSpace = space;

                            bestBox.fitRotate3DSmallestFootprint(bestSpace);
                        } else if(bestBox.getVolume() == box.getVolume()) {
                            // determine lowest fit
                            box.fitRotate3DSmallestFootprint(space);

                            if(box.getFootprint() < bestBox.getFootprint()) {
                                bestBox = box;
                                bestSpace = space;
                            }

                            // TODO if all else is equal, which free space is preferred?
                        }
                    }
                } else {
                    if(box.canFitInside2D(space)) {
                        if(bestBox == null) {
                            bestBox = box;
                            bestSpace = space;
                        } else if(bestBox.getVolume() < box.getVolume()) {
                            bestBox = box;
                            bestSpace = space;
                        }
                        // TODO use the aspect ratio in some meaningful way

                        // TODO if all else is equal, which free space is preferred?
                    }
                }
            }
        }
        if(bestBox != null) {
            return new Placement(bestSpace, bestBox);
        }
//        for(Box box : containerProducts) {
//            box.carryForward=true;
//        }
        return null;
    }

    @Override
    protected Adapter adapter(List<BoxItem> boxes) {
        return this;
    }
}
