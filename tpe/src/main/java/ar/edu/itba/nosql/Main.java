package ar.edu.itba.nosql;

import ar.edu.itba.nosql.algorithms.TrajectoryCreator;
import ar.edu.itba.nosql.algorithms.TrajectoryPrunner;
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
            List<Trajectory> trajectories = trajectoryCreator.createTrajectories(1, from, to, 5, 5);


            CSVManager.trajectoriesToCSV(trajectories, path + "/trajectories.csv",',');
            List<Trajectory> trajectoriesRetrieved = CSVManager.csvToTrajectories(path + "/trajectories.csv",',');
            TrajectoryPrunner trajectoryPrunner = new TrajectoryPrunner(venues);
            List<Trajectory> prunnedTrajectories = trajectoryPrunner.trajectoryPrunner(trajectoriesRetrieved, 50.0);

            for(Trajectory t : prunnedTrajectories){
                for(Visit v : t.getVisits()){
                    System.out.println(v.getVenueId());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
