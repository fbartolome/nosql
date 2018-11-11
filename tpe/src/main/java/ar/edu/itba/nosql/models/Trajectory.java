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

    private class Visit {
        private final String venueId;
        private final LocalDateTime timestamp;

        private Visit(String venueId, LocalDateTime timestamp) {
            this.venueId = venueId;
            this.timestamp = timestamp;
        }
    }
}
