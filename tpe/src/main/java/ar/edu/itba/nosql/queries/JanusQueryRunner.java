package ar.edu.itba.nosql.queries;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import java.util.concurrent.TimeUnit;

import static ar.edu.itba.nosql.algorithms.JanusPopulator.*;
import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

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
      case 2:
        query2(graph);
        break;
      case 3:
        query3(graph);
        break;
      case 4:
        query4(graph);
        break;
      default:
        throw new IllegalArgumentException("Invalid query number");
    }
    System.out.println("Finished in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms");
  }

    private static void query1(final JanusGraph graph) {
        final GraphTraversalSource g = graph.traversal();
        final GraphTraversal<Vertex, Path> result = g.V()
                .where(hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
                        .has(CATEGORY_PROPERTY, "Home"))
                .out(HAS_STEP_EDGE)
                .where(hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
                        .has(CATEGORY_PROPERTY, "Station"))
                .out(HAS_STEP_EDGE)
                .where(hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
                        .has(CATEGORY_PROPERTY, "Airport"))
                .path();
        printResult(result);
    }

  private static void query2(final JanusGraph graph) {
    final GraphTraversalSource g = graph.traversal();
    final GraphTraversal<Vertex, Path> result = g.V()
        .as("start")
        .where(hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
            .has(CATEGORY_PROPERTY, "Home"))
        .repeat(out(HAS_STEP_EDGE))
        .until(hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).out(HAS_SUBCATEGORY_EDGE).out(HAS_CATEGORY_EDGE)
            .has(CATEGORY_PROPERTY, "Airport"))
        .as("finish")
        .filter(select("start", "finish").by(TIMESTAMP_PROPERTY).where("start", P.test((o, o2) -> {
          final String start = (String) o;
          final String finish = (String) o2;
          return start.substring(0, 10).equals(finish.substring(0, 10));
        }, "finish")))
        .path();
    printResult(result);
  }

  private static void query3(final JanusGraph graph) {
    final GraphTraversalSource g = graph.traversal();
    final GraphTraversal<Vertex, Path> result = g.V()
        .where(hasLabel(STOP_VERTEX))
        .as("firstStop")
        .repeat(out(HAS_STEP_EDGE))
        .until(hasLabel(STOP_VERTEX).out(HAS_VENUE_EDGE).in(HAS_VENUE_EDGE).where(eq("firstStop")))
        .as("lastStop")
        .filter(select("firstStop", "lastStop").by(TIMESTAMP_PROPERTY).where("firstStop", sameDay("lastStop")))
        .path();
    printResult(result);
  }

  private static void query4(final JanusGraph graph) {
    final GraphTraversalSource g = graph.traversal();
    final GraphTraversal<Vertex, Path> result = g.V()
        .where(hasLabel(STOP_VERTEX)).as("stopVertex")
        .group("stopVertex").by(USER_ID_PROPERTY)
        .repeat(out(HAS_STEP_EDGE))
        .emit().path().count(Scope.local).max().path();
    printResult(result);

  }

  private static P sameDay(final Object value) {
    return P.test((o, o2) -> {
      final String start = (String) o;
      final String finish = (String) o2;
      return start.substring(0, 10).equals(finish.substring(0, 10));
    }, value);
  }

  private static <T, V> void printResult(final GraphTraversal<T, V> result) {
    System.out.println("Result:");
    result.toStream().forEach(System.out::println);
  }

//  private boolean sameDay(Str)

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
