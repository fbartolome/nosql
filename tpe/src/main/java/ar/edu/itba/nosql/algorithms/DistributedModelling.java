package ar.edu.itba.nosql.algorithms;

import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedModelling {

    private Graph graph = TinkerGraph.open();
    private Map<String, Vertex> categoryVertices = new HashMap<>();
    private Map<String, Vertex> cattypeVertices = new HashMap<>();
    private Map<String, Vertex> venueVertices = new HashMap<>();


    public void createVenuesGraph(final Map<String, Venue> venues){

        for(Map.Entry<String,Venue> e: venues.entrySet()){

            Venue venue = e.getValue();
            Vertex categoryVertex, cattypeVertex, venueVertex;

            // Add all vertices if it doesn't exist in the graph already
            if(categoryVertices.containsKey(venue.getCategory())){
                categoryVertex = categoryVertices.get(venue.getCategory());
            }else{
                categoryVertex = graph.addVertex(T.label, "Category", "name", venue.getCategory());
                categoryVertices.put(venue.getCategory(), categoryVertex);
            }
            if(cattypeVertices.containsKey(venue.getCattype())){
                cattypeVertex = cattypeVertices.get(venue.getCattype());
            }else{
                cattypeVertex = graph.addVertex(T.label, "Categories", "name", venue.getCattype());
                cattypeVertices.put(venue.getCattype(), cattypeVertex);
            }
            if(venueVertices.containsKey(venue.getId())){
                venueVertex = venueVertices.get(venue.getId());
            }else{
                venueVertex = graph.addVertex(T.label, "Venue", "name", venue.getId());
                venueVertices.put(venue.getId(), venueVertex);
            }
            // Add relationships
            venueVertex.addEdge("hasCategory", cattypeVertex);
            cattypeVertex.addEdge("subCategoryOf", categoryVertex);

        }

    }

    public void addTrajectories(List<Trajectory> trajectories, Map<String, Venue> venues){
        for(Trajectory trajectory : trajectories){
            int tpos = 1;
            for(Visit visit : trajectory.getVisits()){
                Venue venue = venues.get(visit.getVenueId());
                Vertex stopVertex = graph.addVertex(T.label, "stop");
                stopVertex.property("userId", trajectory.getUserId());
                stopVertex.property("utctimestamp", visit.getTimestamp());
                stopVertex.property("tpos", tpos);
                tpos++;
                Vertex venueVertex = venueVertices.get(venue.getId());
                stopVertex.addEdge("isVenue", venueVertex);
            }
        }
    }

    public void printGraphFeatures(){
        System.out.println(graph);
    }




}
