package com.dynamiclogic.tams.database.model;

public class TAMSAsset {

    private String id;
    private Coordinates coords;
    private String description;

    public TAMSAsset(Coordinates coords) {
        this.coords = coords;
    }

    public TAMSAsset(Coordinates coords, String description) {
        this.coords = coords;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Coordinates getCoordinates() {
        return coords;
    }


    @Override
    public String toString() {
        return String.format("asset at %s", coords);
    }

    public static class Coordinates {

        double x, y;

        public Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }

        public void setX(double x) { this.x = x; }

        public double getY() { return y; }

        public void setY(double y) { this.y = y; }

        public static double round(double dbl) {
            return Math.round(dbl * 100.0) / 100.0;
        }

        @Override
        public String toString() {
            return Coordinates.class.getSimpleName();
        //    return String.format("[ (x,y) = (%,%f)", round(x), round(y));
        }
    }
}
