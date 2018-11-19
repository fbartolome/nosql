package ar.edu.itba.nosql.algorithms;

import static org.janusgraph.core.Cardinality.SINGLE;
import static org.janusgraph.core.Multiplicity.MANY2ONE;

import ar.edu.itba.nosql.io.CSVManager;
import ar.edu.itba.nosql.models.Trajectory;
import ar.edu.itba.nosql.models.Venue;
import ar.edu.itba.nosql.models.Visit;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.schema.JanusGraphManagement;

public class JanusPopulator {

  public static final String USER_ID_PROPERTY = "userid";
  public static final String TIMESTAMP_PROPERTY = "utctimestamp";
  public static final String VISIT_INDEX_PROPERTY = "tpos";
  public static final String VENUE_ID_PROPERTY = "venueid";
  public static final String SUBCATEGORY_PROPERTY = "venuecategory";
  public static final String CATEGORY_PROPERTY = "cattype";

  public static final String HAS_STEP_EDGE = "trajStep";
  public static final String HAS_VENUE_EDGE = "isVenue";
  public static final String HAS_SUBCATEGORY_EDGE = "hasCategory";
  public static final String HAS_CATEGORY_EDGE = "subCategoryOf";

  public static final String STOP_VERTEX = "Stop";
  public static final String VENUE_VERTEX = "Venue";
  public static final String SUBCATEGORY_VERTEX = "Categories";
  public static final String CATEGORY_VERTEX = "Category";

  public static void main(final String[] args) {
    final Arguments arguments = Arguments.from(args);

    try (final JanusGraph graph = JanusGraphFactory.open(arguments.configPath())) {
      if (arguments.withSchema()) {
        createSchema(graph);
      }
      final Map<String, Venue> venues = CSVManager.csvToVenues(arguments.venuesPath(), ',');
      final List<Trajectory> trajectories = CSVManager.csvToTrajectories(arguments.trajectoriesPath(), ',');
      final JanusGraphTransaction transaction = graph.newTransaction();
      final Map<String, Vertex> venueVertices = populateVenues(transaction, venues);
      populateTrajectories(transaction, trajectories, venueVertices);
      transaction.commit();
      if (arguments.outputPath() != null) {
        graph.io(IoCore.graphml()).writeGraph(arguments.outputPath() + ".graphml");
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void createSchema(final JanusGraph graph) {
    final JanusGraphManagement management = graph.openManagement();

    management.makePropertyKey(USER_ID_PROPERTY).dataType(Long.class).cardinality(SINGLE).make();
    management.makePropertyKey(TIMESTAMP_PROPERTY).dataType(Date.class).cardinality(SINGLE).make();
    management.makePropertyKey(VISIT_INDEX_PROPERTY).dataType(Integer.class).cardinality(SINGLE).make();
    management.makePropertyKey(VENUE_ID_PROPERTY).dataType(String.class).cardinality(SINGLE).make();
    management.makePropertyKey(SUBCATEGORY_PROPERTY).dataType(String.class).cardinality(SINGLE).make();
    management.makePropertyKey(CATEGORY_PROPERTY).dataType(String.class).cardinality(SINGLE).make();

    management.makeEdgeLabel(HAS_STEP_EDGE).multiplicity(MANY2ONE).make();
    management.makeEdgeLabel(HAS_VENUE_EDGE).multiplicity(MANY2ONE).make();
    management.makeEdgeLabel(HAS_SUBCATEGORY_EDGE).multiplicity(MANY2ONE).make();
    management.makeEdgeLabel(HAS_CATEGORY_EDGE).multiplicity(MANY2ONE).make();

    management.makeVertexLabel(STOP_VERTEX).make();
    management.makeVertexLabel(VENUE_VERTEX).make();
    management.makeVertexLabel(SUBCATEGORY_VERTEX).make();
    management.makeVertexLabel(CATEGORY_VERTEX).make();

    management.commit();
  }

  private static Map<String, Vertex> populateVenues(final JanusGraphTransaction transaction,
      final Map<String, Venue> venues) {
    final Map<String, Vertex> categoriesVertices = new HashMap<>();
    final Map<String, Vertex> subcategoriesVertices = new HashMap<>();
    final Map<String, Vertex> venueVertices = new HashMap<>();

    for (final Venue venue : venues.values()) {
      final Vertex categoryVertex, subcategoryVertex;
      final Vertex venueVertex = transaction.addVertex(T.label, VENUE_VERTEX, VENUE_ID_PROPERTY, venue.getId());

      if (subcategoriesVertices.containsKey(venue.getSubcategory())) {
        subcategoryVertex = subcategoriesVertices.get(venue.getSubcategory());
      } else {
        subcategoryVertex = transaction
            .addVertex(T.label, SUBCATEGORY_VERTEX, SUBCATEGORY_PROPERTY, venue.getSubcategory());
      }

      if (categoriesVertices.containsKey(venue.getCategory())) {
        categoryVertex = categoriesVertices.get(venue.getCategory());
      } else {
        categoryVertex = transaction.addVertex(T.label, CATEGORY_VERTEX, CATEGORY_PROPERTY, venue.getCategory());
        categoriesVertices.put(venue.getCategory(), categoryVertex);
      }

      // Add relationships
      venueVertex.addEdge(HAS_SUBCATEGORY_EDGE, subcategoryVertex);
      venueVertices.put(venue.getId(), venueVertex);
      if (!subcategoriesVertices.keySet().contains(venue.getSubcategory())) {
        subcategoryVertex.addEdge(HAS_CATEGORY_EDGE, categoryVertex);
        subcategoriesVertices.put(venue.getSubcategory(), subcategoryVertex);
      }
    }

    return venueVertices;
  }

  private static void populateTrajectories(final JanusGraphTransaction transaction, final List<Trajectory> trajectories,
      final Map<String, Vertex> venues) {
    for (final Trajectory trajectory : trajectories) {
      final int userId = trajectory.getUserId();
      int visitNumber = 1;
      Vertex lastStopVertex = null;
      for (final Visit visit : trajectory.getVisits()) {
        // https://stackoverflow.com/questions/19431234/converting-between-java-time-localdatetime-and-java-util-date
        final Date timestamp = Date.from(visit.getTimestamp().atZone(ZoneId.systemDefault()).toInstant());
        final Vertex venueVertex = venues.get(visit.getVenueId());
        final Vertex currentStopVertex = transaction.addVertex(T.label, STOP_VERTEX);
        currentStopVertex.property(USER_ID_PROPERTY, userId);
        currentStopVertex.property(TIMESTAMP_PROPERTY, timestamp);
        currentStopVertex.property(VISIT_INDEX_PROPERTY, visitNumber++);
        currentStopVertex.addEdge(HAS_VENUE_EDGE, venueVertex);
        if (lastStopVertex != null) {
          lastStopVertex.addEdge(HAS_STEP_EDGE, currentStopVertex);
        }
        lastStopVertex = currentStopVertex;
      }
    }
  }

  private static final class Arguments {

    @Parameter(names = {"-v", "-venues", "--venues"}, required = true)
    private String venuesPath;

    @Parameter(names = {"-t", "-trajectories", "--trajectories"}, required = true)
    private String trajectoriesPath;

    @Parameter(names = {"-j", "-janus-config", "--janus-config"}, required = true)
    private String configPath;

    @Parameter(names = {"-o", "-out", "--out"})
    private String outputPath;

    @Parameter(names = {"-no-schema", "--no-schema"}, description = "Do not create schema")
    private boolean noSchema = false;

    public String venuesPath() {
      return venuesPath;
    }

    public String trajectoriesPath() {
      return trajectoriesPath;
    }

    public String configPath() {
      return configPath;
    }

    public String outputPath() {
      return outputPath;
    }

    public boolean withSchema() {
      return !noSchema;
    }

    public static Arguments from(final String[] args) {
      final Arguments arguments = new Arguments();
      JCommander.newBuilder().addObject(arguments).build().parse(args);
      return arguments;
    }
  }
}