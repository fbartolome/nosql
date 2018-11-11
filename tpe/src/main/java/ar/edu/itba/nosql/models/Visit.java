package ar.edu.itba.nosql.models;

import java.time.LocalDateTime;

public class Visit {

    private final String venueId;
    private final LocalDateTime timestamp;

    public Visit(String venueId, LocalDateTime timestamp) {
        this.venueId = venueId;
        this.timestamp = timestamp;
    }

    public String getVenueId() {
        return venueId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
