package ar.edu.itba.nosql;

import ar.edu.itba.nosql.algorithms.TrajectoryCreator;
import ar.edu.itba.nosql.io.CSVManager;
import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<Venue> venues = CSVManager.csvToVenues("/Users/seguido/Downloads/postgres_public_categories.csv",
                    ',');
            TrajectoryCreator trajectoryCreator = new TrajectoryCreator(venues);
            LocalDateTime from = LocalDateTime.of(2018, 10, 10, 12, 0);
            LocalDateTime to = LocalDateTime.of(2018, 10, 11, 12, 0);
            List<Trajectory> trajectories = trajectoryCreator.createTrajectories(2, from, to, 1, 5);

            CSVManager.trajectoriesToCSV(trajectories, "/Users/seguido/ITBA/NoSQL/TPE/nosql/trajectories.csv",
                    ',');
            List<Trajectory> trajectoriesRetrieved = CSVManager.csvToTrajectories("/Users/seguido/ITBA/NoSQL/TPE/nosql/trajectories.csv",',');
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
