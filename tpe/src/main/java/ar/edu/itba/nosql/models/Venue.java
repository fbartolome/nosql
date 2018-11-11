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



    /*
    function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1);
  var a =
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ;
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c; // Distance in km
  return d;
}
     */

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
