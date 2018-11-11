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
        List<Trajectory> prunnedTrajectories = new ArrayList<>();
        for(Trajectory t: trajectories){
            if(isValidTrajectory(t, maxSpeed)){
                prunnedTrajectories.add(t);
            }
        }
        return prunnedTrajectories;
    }

    /**
     * Check if the trajectory is valid based on the maximum speed
     * @param trajectory
     * @param maxSpeed in km/h to check if the trajectory is reasonable
     * @return
     */
    public Boolean isValidTrajectory(Trajectory trajectory, double maxSpeed){
        List<Visit> visits = trajectory.getVisits();
        Visit prev = null;
        for(Visit v: visits){
            if(prev == null){
                prev = v;
                continue;
            }
            Long duration = Duration.between(prev.getTimestamp(), v.getTimestamp()).toHours();
            Venue thisVenue = venues.get(v.getVenueId());
            Venue otherVenue = venues.get(prev.getVenueId());

            if(thisVenue.getDistanceTo(otherVenue)/maxSpeed < duration){
                return false;
            }
            prev = v;
        }
        return true;
    }


}
