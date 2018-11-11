package ar.edu.itba.nosql.io;

import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class CSVManager {

    public static List<Venue> csvToVenues(String pathname, Character separator) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(pathname), separator);
        List<String[]> entries = reader.readAll();
        List<Venue> venues = new LinkedList<Venue>();

        for (String[] entry : entries) {
            venues.add(new Venue(entry[0], Double.parseDouble(entry[2]), Double.parseDouble(entry[3])));
        }

        return venues;
    }

    // TODO
    public static List<Trajectory> csvToTrajectories(String pathname, Character separator){
        return null;
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
