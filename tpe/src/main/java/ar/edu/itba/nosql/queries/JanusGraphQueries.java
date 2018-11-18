package ar.edu.itba.nosql.queries;

import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JanusGraphQueries {

    // Not using janusgraph, use to compare with janusgraph results
    public List<Trajectory> query1Java(List<Trajectory> trajectoryList, Map<String, Venue> venues){
        List<Trajectory> retTrajectories = new ArrayList<>();
        for(Trajectory trajectory : trajectoryList){
            boolean visitedHome = false;
            boolean visitedStation = false;
            for(Visit visit: trajectory.getVisits()){
                Venue visitVenue = venues.get(visit.getVenueId());
                if(visitedHome && visitedStation && visitVenue.getCategory() == "Airport"){
                    retTrajectories.add(trajectory);
                    continue;
                }else if(visitedHome && visitedStation){
                    visitedHome = false;
                    visitedStation = false;
                }
                if(visitedHome && visitVenue.getCategory() == "Station"){
                    visitedStation = true;
                }else if(visitedHome){ // Is not consecutive
                    visitedHome = false;
                }
            }
        }
        return retTrajectories;
    }



}
