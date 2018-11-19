package ar.edu.itba.nosql.queries;

import static ar.edu.itba.nosql.algorithms.JanusPopulator.CATEGORY_PROPERTY;
import static ar.edu.itba.nosql.algorithms.JanusPopulator.HAS_CATEGORY_EDGE;
import static ar.edu.itba.nosql.algorithms.JanusPopulator.HAS_STEP_EDGE;
import static ar.edu.itba.nosql.algorithms.JanusPopulator.HAS_SUBCATEGORY_EDGE;
import static ar.edu.itba.nosql.algorithms.JanusPopulator.HAS_VENUE_EDGE;
import static ar.edu.itba.nosql.algorithms.JanusPopulator.STOP_VERTEX;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

public class JanusQueryRunner {

  public static void main(final String[] args) {
    final Arguments arguments = Arguments.from(args);
    try (final JanusGraph graph = JanusGraphFactory.open(arguments.configPath())) {
      runQuery(arguments.queryNumber(), graph);
    }
  }

  private static void runQuery(final int queryNumber, final JanusGraph graph) {
    final long startTime = System.nanoTime();
    switch (queryNumber) {
      case 1:
        query1(graph);
        break;
      default:
        throw new IllegalArgumentException("Invalid query number");
    }
    System.out.println("Finished in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms");
  }

  private static void query1(final JanusGraph graph) {
    final GraphTraversalSource g = graph.traversal();
    final GraphTraversal<Vertex, Map<String, Object>> result = g.V().match(
        as("homeStop").hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
            .has(CATEGORY_PROPERTY, "Home"),
        as("stationStop").out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
            .has(CATEGORY_PROPERTY, "Station"),
        as("homeStop").out(HAS_STEP_EDGE).as("stationStop"),
        as("airportStop").out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
            .has(CATEGORY_PROPERTY, "Airport"),
        as("stationStop").out(HAS_STEP_EDGE).as("airportStop"))
        .select("homeStop", "stationStop", "airportStop");
    System.out.println("Result:");
    result.toStream().forEach(System.out::println);
  }

  private static final class Arguments {

    @Parameter(names = {"-j", "-janus-config", "--janus-config"}, required = true)
    private String configPath;

    @Parameter(names = {"-q", "-query", "--query"}, required = true)
    private int queryNumber;

    public String configPath() {
      return configPath;
    }

    public int queryNumber() {
      return queryNumber;
    }

    public static Arguments from(final String[] args) {
      final Arguments arguments = new Arguments();
      JCommander.newBuilder().addObject(arguments).build().parse(args);
      return arguments;
    }
  }
}
