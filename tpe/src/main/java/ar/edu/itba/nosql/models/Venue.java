package ar.edu.itba.nosql.models;

public class Venue {

    private final String id;
    private final double latitude;
    private final double longitude;

    public Venue(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Venue(String id) {
        this.id = id;
        latitude = 0;
        longitude = 0;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public double getDistanceTo(Venue otherVenue){
        return Math.pow(Math.abs(latitude - otherVenue.getLatitude()),2) + Math.pow(Math.abs(longitude - otherVenue.getLongitude()), 2);
    }

}
