package ar.edu.itba.nosql.io;

import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;
import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class CSVManager {

    public static Map<String, Venue> csvToVenues(String pathname, Character separator) throws IOException {
      try (CSVReader reader = new CSVReader(new FileReader(pathname), separator)) {
        List<String[]> entries = reader.readAll();
        Map<String, Venue> venues = new HashMap<>();

        for (String[] entry : entries) {
            venues.put(entry[0], new Venue(entry[0], Double.parseDouble(entry[2]), Double.parseDouble(entry[3]),
                    entry[1], entry[4]));
        }

        return venues;
      }
    }

    // TODO
    public static List<Trajectory> csvToTrajectories(String pathname, Character separator) throws IOException {
        Map<Integer,Trajectory> trajectoryMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(pathname), StandardCharsets.ISO_8859_1)){
            stream.forEach(line -> {
                String[] fields = line.split(separator.toString());
                if(!trajectoryMap.keySet().contains(Integer.parseInt(fields[0]))) {
                    trajectoryMap.put(Integer.valueOf(fields[0]), new Trajectory(Integer.valueOf(fields[0])));
                }
                trajectoryMap.get(Integer.parseInt(fields[0])).addLocation(new Venue(fields[1]), LocalDateTime.parse(fields[2]));
            });
        }

        return new ArrayList<>(trajectoryMap.values());
    }

    public static void trajectoriesToCSV(List<Trajectory> trajectories, String pathname, Character separator) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(pathname));
        StringBuilder sb = new StringBuilder();

        for(Trajectory trajectory : trajectories){
            int tpos = 1;
            for(Visit visit : trajectory.getVisits()){
                sb.append(trajectory.getUserId());
                sb.append(separator);
                sb.append(visit.getVenueId());
                sb.append(separator);
                // TODO: ver si se puede leer bien este timestamp por como se guarda
                sb.append(visit.getTimestamp());
                sb.append(separator);
                sb.append(tpos++);
                sb.append('\n');
            }
        }

        pw.write(sb.toString());
        pw.close();
    }

}
