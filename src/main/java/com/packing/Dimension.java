package com.packing;

import java.util.ArrayList;

public class Dimension {

    public static final Dimension EMPTY = new Dimension(0, 0, 0);

    public static Dimension decode(String size) {
        String[] dimensions = size.split("x");

        return newInstance(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]), Integer.parseInt(dimensions[2]));
    }

    public static String encode(Dimension dto) {
        return encode(dto.getWidth(), dto.getDepth(), dto.getHeight());
    }

    public static String encode(int width, int depth, int height) {
        return width + "x" + depth + "x" + height;
    }

    public static Dimension newInstance(int width, int depth, int height) {
        return new Dimension(width, depth, height);
    }

    public int width; // x
    public int depth; // y
    public int height; // z
    public long volume;
    public boolean canFit=true;
    public boolean carryForward=false;
    public boolean toRemove=false;
    public String fittedTruck;
    public int numberOfPackages=0;

    protected final String name;
    private int type;

    public Dimension(String name) {
        this.name = name;
    }

    public Dimension() {
        this(null);
    }

    public Dimension(String name, int w, int d, int h) {
        this.name = name;

        this.depth = d;
        this.width = w;
        this.height = h;

        this.volume = ((long)depth) * ((long)width) * ((long)height);
    }

    public Dimension(String name, int type, int w, int d, int h) {
      this.name = name;
      this.type = type;
      this.depth = d;
      this.width = w;
      this.height = h;

      this.volume = ((long)depth) * ((long)width) * ((long)height);
    }

    public Dimension(int w, int d, int h) {
        this(null, w, d, h);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int getType() { return type; }

    /**
     *
     * Check whether a dimension fits within the current dimensions, rotated in 3D.
     *
     * @param dimension the space to fit
     * @return true if any rotation of the argument can be placed inside this
     *
     */

    public boolean canHold3D(Dimension dimension) {
        return canHold3D(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
    }

    public boolean canHold3D(int w, int d, int h) {
        if(w <= width && h <= height && d <= depth) {
            return true;
        }

        if(h <= width && d <= height && w <= depth) {
            return true;
        }

        if(d <= width && w <= height && h <= depth) {
            return true;
        }

        if(h <= width && w <= height && d <= depth) {
            return true;
        }

        if(d <= width && h <= height && w <= depth) {
            return true;
        }

        if(w <= width && d <= height && h <= depth) {
            return true;
        }

        return false;
    }


    /**
     *
     * Check whether a dimension fits within the current object, rotated in 2D.
     *
     * @param dimension the dimension to fit
     * @return true if any rotation of the argument can be placed inside this
     *
     */

    public boolean canHold2D(Dimension dimension) {
        return canHold2D(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
    }

    public boolean canHold2D(int w, int d, int h) {
        if(h > height) {
            return false;
        }
        return (w <= width && d <= depth) || (d <= width && w <= depth);
    }

    public int getFootprint() {
        return width * depth;
    }

    public boolean isSquare2D() {
        return width == depth;
    }

    public boolean isSquare3D() {
        return width == depth && width == height;
    }

    /**
     * Check whether this object fits within a dimension (without rotation).
     *
     * @param dimension the dimensions to fit within
     * @return true if this can fit within the argument space
     */

    public boolean fitsInside3D(Dimension dimension) {
        return fitsInside3D(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
    }

    public boolean fitsInside3D(int w, int d, int h) {

        if(w >= width && h >= height && d >= depth) {
            return true;
        }

        return false;
    }



    /**
     * Check whether this object can fit within a dimension, with 3D rotation.
     *
     * @param dimension the dimensions to fit within
     * @return true if this can fit within the argument space in any rotation
     *
     */

    public boolean canFitInside3D(Dimension dimension) {
        return dimension.canHold3D(this);
    }

    /**
     * Check whether this object can fit within a dimension, with 2D rotation.
     *
     * @param dimension the dimensions to fit within
     * @return true if this can fit within the argument space in any 2D rotation
     *
     */

    public boolean canFitInside2D(Dimension dimension) {
        return dimension.canHold2D(this);
    }

    public long getVolume() {
        return volume;
    }

    public boolean isEmpty() {
        return width <= 0 || depth <= 0 || depth <= 0;
    }

    @Override
    public String toString() {
        return "Dimension for " + name + type + "[width=" + width + ", depth=" + depth + ", height=" + height + ", volume=" + volume + "]";
    }

    public ArrayList<String> getDbUpdateData() {
      ArrayList<String> truckSummary = new ArrayList<>();

      truckSummary.add(getName());
      truckSummary.add(String.valueOf(getType()));
      truckSummary.add(String.valueOf(width));
      truckSummary.add(String.valueOf(depth));
      truckSummary.add(String.valueOf(height));
      truckSummary.add(String.valueOf(numberOfPackages));


      return truckSummary;
    }

    public String encode() {
        return encode(width, depth, height);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + depth;
        result = prime * result + height;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (volume ^ (volume >>> 32));
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dimension other = (Dimension) obj;
        if (depth != other.depth)
            return false;
        if (height != other.height)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (volume != other.volume)
            return false;
        if (width != other.width)
            return false;
        return true;
    }



}