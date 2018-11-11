package ar.edu.itba.nosql;

import ar.edu.itba.nosql.algorithms.TrajectoryCreator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try {
            TrajectoryCreator trajectoryCreator = new TrajectoryCreator("/Users/franbartolome/Documents/ITBA/Electivas/Paradigma NoSQL/TPE/postgres_public_categories.csv",
                    ',');
            LocalDateTime from = LocalDateTime.of(2018, 10, 10, 12, 0);
            LocalDateTime to = LocalDateTime.of(2018, 11, 10, 12, 0);
            trajectoryCreator.createTrajectories(2, from, to, 1, 5);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
