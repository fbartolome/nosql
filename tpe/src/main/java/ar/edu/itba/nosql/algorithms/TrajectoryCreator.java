package ar.edu.itba.nosql.algorithms;

import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TrajectoryCreator {

    private static final TemporalUnit TIME_UNIT = ChronoUnit.MILLIS;

    private final List<Venue> venues;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public TrajectoryCreator(List<Venue> venues) {
        this.venues = venues;
    }


    public List<Trajectory> createTrajectories(int trajectoryAmount, LocalDateTime from, LocalDateTime to,
                                   int minLocations, int maxLocations) throws IOException {
        final List<Trajectory> trajectories = new LinkedList<>();

        for (int i = 1; i <= trajectoryAmount; i++) {
          trajectories.add(createTrajectory(i, from, to, minLocations, maxLocations));
        }

        return trajectories;
    }

    private Trajectory createTrajectory(int userId, LocalDateTime from, LocalDateTime to,
                                        int minLocations, int maxLocations) {
        final Trajectory trajectory = new Trajectory(userId);
        final int visits = this.random.nextInt(minLocations, maxLocations + 1);
        Venue lastVenue = null;

        for (int i = 0; i < visits; i++) {
            final Venue venue = getRandomVenue(lastVenue);
            lastVenue = venue;

            // Tiene el problema de que el random puede dar muy cerca del maximo,
            // lo cual hace que el resto de las visitas sea en un rango de tiempo muy pequeño.
            final long units = TIME_UNIT.between(from, to);
            final LocalDateTime timestamp = from.plus(random.nextLong(units), TIME_UNIT);

            trajectory.addLocation(venue, timestamp);

            from = timestamp;
        }

        return trajectory;
    }

    private Venue getRandomVenue(final Venue lastVenue) {
        Venue venue;
        do {
            venue = venues.get(random.nextInt(venues.size() + 1));
        } while (venue == lastVenue);
        return venue;
    }

}
