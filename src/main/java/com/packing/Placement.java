package com.packing;

/**
 * Placement as in box in a space.
 *
 * The box does not necessarily fill the whole space.
 */

public class Placement {

    private Space space;
    private Box box;

    public Placement(Space space, Box box) {
        this.space = space;
        this.box = box;
    }

    public Placement(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }
    public void setSpace(Space space) {
        this.space = space;
    }
    public Box getBox() {
        return box;
    }
    public void setBox(Box box) {
        this.box = box;
    }

    @Override
    public String toString() {
        return "Placement [" + space.getX() + "x" + space.getY() + "x" + space.getZ() + ", width=" + box.getWidth() + ", depth=" + box.getDepth()+ ", height="
                + box.getHeight()+ "]";
    }

    public int getCenterX() {
        return space.getX() + (box.getWidth() / 2);
    }

    public int getCenterY() {
        return space.getY() + (box.getDepth() / 2);
    }

    public boolean intercets(Placement placement) {
        return intercetsX(placement) && intercetsY(placement);
    }

    public boolean intercetsY(Placement placement) {

        int startY = space.getY();
        int endY = startY + box.getDepth() - 1;

        if(startY <= placement.getSpace().getY() && placement.getSpace().getY() <= endY) {
            return true;
        }

        if(startY <= placement.getSpace().getY() + placement.getBox().getDepth() - 1 && placement.getSpace().getY() + placement.getBox().getDepth() - 1 <= endY) {
            return true;
        }

        return false;
    }

    public boolean intercetsX(Placement placement) {

        int startX = space.getX();
        int endX = startX + box.getWidth() - 1;

        if(startX <= placement.getSpace().getX() && placement.getSpace().getX() <= endX) {
            return true;
        }

        if(startX <= placement.getSpace().getX() + placement.getBox().getWidth() - 1 && placement.getSpace().getX() + placement.getBox().getWidth() - 1  <= endX) {
            return true;
        }

        return false;
    }
}
