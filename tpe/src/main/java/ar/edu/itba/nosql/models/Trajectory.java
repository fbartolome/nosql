package ar.edu.itba.nosql.models;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Trajectory {

    private final int userId;
    private List<Visit> visits;

    public Trajectory(int userId) {
        this.userId = userId;
        this.visits = new LinkedList<>();
    }

    public void addLocation(Venue venue, LocalDateTime timestamp){
        visits.add(new Visit(venue.getId(), timestamp));
    }

    public int getUserId() {
        return userId;
    }

    public List<Visit> getVisits() {
        return visits;
    }
}
