package org.ulpgc.dacd.model;

public class Location {

    private String island;
    private Double lat;
    private Double lon;

    public Location(String island, Double lat, Double lon){
        this.island = island;
        this.lat = lat;
        this.lon = lon;
    }


    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
