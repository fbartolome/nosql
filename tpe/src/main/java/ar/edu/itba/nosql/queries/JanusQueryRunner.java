package ar.edu.itba.nosql.queries;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.concurrent.TimeUnit;
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
    System.out.println("Finished in " + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime) + " seconds");
  }

  private static void query1(final JanusGraph graph) {
    final Long countAll = graph.traversal().V().count().next();
    System.out.println("Size: " + countAll);
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
