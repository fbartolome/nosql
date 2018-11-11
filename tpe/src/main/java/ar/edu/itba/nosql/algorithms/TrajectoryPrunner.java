package ar.edu.itba.nosql.algorithms;

import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrajectoryPrunner {

    static Map<String, Venue> venues;

    public TrajectoryPrunner(Map<String, Venue> venues) {
        this.venues = venues;
    }

    public List<Trajectory> trajectoryPrunner(List<Trajectory> trajectories, double maxSpeed){
        for(Trajectory t: trajectories){
            t.setVisits(getValidVisits(t, maxSpeed));
        }
        return trajectories;
    }

    /**
     * Check if the trajectory is valid based on the maximum speed
     * @param trajectory
     * @param maxSpeed in km/h to check if the trajectory is reasonable
     * @return
     */
    public List<Visit> getValidVisits(Trajectory trajectory, double maxSpeed){
        List<Visit> visits = trajectory.getVisits();
        Visit prev = null;
        List<Visit> validVisits = new ArrayList<>();
        for(Visit v: visits){
            if(prev == null){
                prev = v;
                validVisits.add(v);
                continue;
            }
            Long duration = Duration.between(prev.getTimestamp(), v.getTimestamp()).toHours();
            Venue thisVenue = venues.get(v.getVenueId());
            Venue otherVenue = venues.get(prev.getVenueId());
            if(thisVenue.getDistanceTo(otherVenue)/maxSpeed <= duration){
                prev = v;
                validVisits.add(v);
            }
        }
        return validVisits;
    }


}
