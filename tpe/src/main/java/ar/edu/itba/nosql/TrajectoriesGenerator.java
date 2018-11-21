package ar.edu.itba.nosql;

import ar.edu.itba.nosql.algorithms.TrajectoryCreator;
import ar.edu.itba.nosql.algorithms.TrajectoryPrunner;
import ar.edu.itba.nosql.io.CSVManager;
import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TrajectoriesGenerator {
    public static void main(String[] args) {
        final Arguments arguments = Arguments.from(args);

        try {
            // Generate random trajectories
            Map<String,Venue> venues = CSVManager.csvToVenues(arguments.venuesPath(), ',');
            TrajectoryCreator trajectoryCreator = new TrajectoryCreator(venues);
            TrajectoryPrunner trajectoryPrunner = new TrajectoryPrunner(venues);

            final String outputPath = arguments.outputPath();
            // Generate SS
            generateTrajectories(trajectoryCreator, trajectoryPrunner, 1000, 100, 100, outputPath + "/trajectoriesSS.csv", outputPath + "/prunnedTrajectoriesSS.csv");
            // Generate SL
            generateTrajectories(trajectoryCreator, trajectoryPrunner, 1000, 200, 200, outputPath + "/trajectoriesSL.csv", outputPath + "/prunnedTrajectoriesSL.csv");
            // Generate LS
            generateTrajectories(trajectoryCreator, trajectoryPrunner, 10000, 100, 100, outputPath + "/trajectoriesLS.csv", outputPath + "/prunnedTrajectoriesLS.csv");
            // Generate LL
            generateTrajectories(trajectoryCreator, trajectoryPrunner, 10000, 200, 200, outputPath + "/trajectoriesLL.csv", outputPath + "/prunnedTrajectoriesLL.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<Trajectory> generateTrajectories(TrajectoryCreator trajectoryCreator, TrajectoryPrunner trajectoryPrunner, int userAmount, int minLocations, int maxLocations, String outputPath, String outputPathPrunned) throws IOException {
        LocalDateTime from = LocalDateTime.of(2018, 10, 10, 12, 0);
        LocalDateTime to = LocalDateTime.of(2018, 10, 11, 12, 0);
        List<Trajectory> trajectories = trajectoryCreator.createTrajectories(userAmount, from, to, minLocations, maxLocations);
        CSVManager.trajectoriesToCSV(trajectories, outputPath,',');
        trajectories = trajectoryPrunner.trajectoryPrunner(trajectories, 50.0);
        CSVManager.trajectoriesToCSV(trajectories, outputPathPrunned,',');
        return trajectories;
    }

    private static final class Arguments {

        @Parameter(names = {"-v", "-venues", "--venues"}, required = true)
        private String venuesPath;

        @Parameter(names = {"-o", "-output", "--output"}, required = true)
        private String outputPath;

        public String venuesPath() {
            return venuesPath;
        }

        public String outputPath() {
            return outputPath;
        }

        public static Arguments from(final String[] args) {
            final Arguments arguments = new Arguments();
            JCommander.newBuilder().addObject(arguments).build().parse(args);
            return arguments;
        }
    }
}
