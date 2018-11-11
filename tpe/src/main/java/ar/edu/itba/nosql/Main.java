package ar.edu.itba.nosql;

import ar.edu.itba.nosql.algorithms.TrajectoryCreator;
import ar.edu.itba.nosql.io.CSVManager;
import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {

            /* categories csv should be in tpe folder */
            String path = Paths.get(".").toAbsolutePath().normalize().toString();
            Map<String,Venue> venues = CSVManager.csvToVenues(path + "/postgres_public_categories.csv", ',');
            TrajectoryCreator trajectoryCreator = new TrajectoryCreator(venues);
            LocalDateTime from = LocalDateTime.of(2018, 10, 10, 12, 0);
            LocalDateTime to = LocalDateTime.of(2018, 10, 11, 12, 0);
            List<Trajectory> trajectories = trajectoryCreator.createTrajectories(2, from, to, 1, 5);


            CSVManager.trajectoriesToCSV(trajectories, path + "/trajectories.csv",',');
            List<Trajectory> trajectoriesRetrieved = CSVManager.csvToTrajectories(path + "/trajectories.csv",',');
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
