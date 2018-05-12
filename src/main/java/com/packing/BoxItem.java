package com.packing;

/**
 * A {@linkplain Box} repeated one or more times. Typically corresponding to an order-line, but
 * can also represent multiple products which share the same size.
 *
 */

public class BoxItem {

    private int count;

    private Box box;

    public BoxItem() {
    }

    public BoxItem(Box box) {
        this(box, 1);
    }

    public BoxItem(Box box, int count) {
        super();
        this.box = box;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }


}