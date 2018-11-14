package ar.edu.itba.nosql.models;

public class Venue {

    private final String id;
    private final double latitude;
    private final double longitude;
    private final String category;
    private final String cattype;

    public Venue(String id, double latitude, double longitude, String category, String cattype) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.cattype = cattype;
    }

    public Venue(String id) {
        this.id = id;
        // TODO ver si esto queda asi o menos kbeza
        latitude = 0;
        longitude = 0;
        category = "";
        cattype = "";

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

    public String getCategory() {
        return category;
    }

    public String getCattype() {
        return cattype;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    private double degreesToRadians(Double degrees){
        return degrees * (Math.PI /180);
    }

    public double getDistanceTo(Venue otherVenue){
        double earthRadius = 6371.0;
        double dLat = degreesToRadians(latitude - otherVenue.getLatitude());
        double dLon = degreesToRadians(longitude - otherVenue.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(degreesToRadians(otherVenue.latitude)) * Math.cos(degreesToRadians(latitude)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

}
