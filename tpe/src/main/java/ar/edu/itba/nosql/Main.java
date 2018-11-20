package ar.edu.itba.nosql;

import ar.edu.itba.nosql.algorithms.DistributedModelling;
import ar.edu.itba.nosql.algorithms.JanusPopulator;
import ar.edu.itba.nosql.algorithms.TrajectoryCreator;
import ar.edu.itba.nosql.algorithms.TrajectoryPrunner;
import ar.edu.itba.nosql.io.CSVManager;
import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Generate random trajectories
            /* categories csv should be in tpe folder */
            String path = Paths.get(".").toAbsolutePath().normalize().toString();
            Map<String,Venue> venues = CSVManager.csvToVenues(path + "/venues.csv", ',');
            TrajectoryCreator trajectoryCreator = new TrajectoryCreator(venues);
            TrajectoryPrunner trajectoryPrunner = new TrajectoryPrunner(venues);

            // TODO ver que el avg largo de trayectorias no sean - max y min
            List<Trajectory> TrajectoriesSS = generateTrajectories(trajectoryCreator, trajectoryPrunner, path, 1000, 5, 5, path + "/trajectoriesSS.csv", path + "/prunnedTrajectoriesSS.csv");
            List<Trajectory> TrajectoriesSL = generateTrajectories(trajectoryCreator, trajectoryPrunner, path, 1000, 200, 200, path + "/trajectoriesSL.csv", path + "/prunnedTrajectoriesSL.csv");
            List<Trajectory> TrajectoriesLS = generateTrajectories(trajectoryCreator, trajectoryPrunner, path, 10000, 100, 100, path + "/trajectoriesLS.csv", path + "/prunnedTrajectoriesLS.csv");
            List<Trajectory> TrajectoriesLL = generateTrajectories(trajectoryCreator, trajectoryPrunner, path, 10000, 200, 200, path + "/trajectoriesLL.csv", path + "/prunnedTrajectoriesLL.csv");

            CSVManager.trajectoriesToCSV(TrajectoriesSS, "TrajectoriesSS.csv", ';');
            CSVManager.trajectoriesToCSV(TrajectoriesSL, "TrajectoriesSL.csv", ';');
            CSVManager.trajectoriesToCSV(TrajectoriesLS, "TrajectoriesLS.csv", ';');
            CSVManager.trajectoriesToCSV(TrajectoriesLL, "TrajectoriesLL.csv", ';');

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<Trajectory> generateTrajectories(TrajectoryCreator trajectoryCreator, TrajectoryPrunner trajectoryPrunner, String path, int userAmount, int minLocations, int maxLocations, String outputPath, String outputPathPrunned) throws IOException {
        LocalDateTime from = LocalDateTime.of(2018, 10, 10, 12, 0);
        LocalDateTime to = LocalDateTime.of(2018, 10, 11, 12, 0);
        List<Trajectory> trajectories = trajectoryCreator.createTrajectories(userAmount, from, to, minLocations, maxLocations);
        CSVManager.trajectoriesToCSV(trajectories, outputPath,',');
        trajectories = trajectoryPrunner.trajectoryPrunner(trajectories, 50.0);
        CSVManager.trajectoriesToCSV(trajectories, outputPathPrunned,',');
        return trajectories;
    }

}
