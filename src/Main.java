import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class Main {
    private static final List<String> entries = List.of("319","680","180","690","129","620","762","689","762","318","368","710","720","710","629","168","160","689","716","731","736","729","316","729","729","710","769","290","719","680","318","389","162","289","162","718","729","319","790","680","890","362","319","760","316","729","380","319","728","716");

    public static void main(String[] args) {
        // Initialise edges
        final Set<Edge> allEdges = entries.stream()
                .map(entry -> List.of(
                        new Edge(
                                Integer.parseInt(entry.substring(0, 1)),
                                Integer.parseInt(entry.substring(1, 2))),
                        new Edge(
                                Integer.parseInt(entry.substring(1, 2)),
                                Integer.parseInt(entry.substring(2, 3)))
                ))
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // Initialise nodes
        Map<Integer, Node> allNodes = new HashMap<>();
        for (Edge edge : allEdges) {
            allNodes.putIfAbsent(edge.from, new Node(edge.from));
            allNodes.putIfAbsent(edge.to, new Node(edge.to));

            Node from = allNodes.get(edge.from);
            Node to = allNodes.get(edge.to);
            from.addToEdge(to);
            to.addFromEdge(from);
        }

        // Find the entrypoint
        List<Node> entryPoints = allNodes
                .values()
                .stream()
                .filter(Node::isAnEntryPoint)
                .toList();

        if (entryPoints.size() != 1) {
            throw new RuntimeException("There should be exactly one possible character at the start of the password");
        }
        final Node primaryNode = entryPoints.stream().findAny().get();

        // Find final destination
        final List<Node> endPoints = allNodes
                .values()
                .stream()
                .filter(Node::isAnEndPoint)
                .toList();

        if (endPoints.size() != 1) {
            throw new RuntimeException("There should be exactly one possible character at the end of the password");
        }
        final Node finalNode = endPoints.stream().findAny().get();

        // Find maximum path
        Set<Node> to = Set.of(finalNode);
        while (!to.isEmpty()) {
            Set<Node> nextTos = new HashSet<>();

            for (Node current : to) {
                Set<Node> from = current.getFrom();
                nextTos.addAll(from);
                for (Node next : from) {
                    next.compareNewRoute(current, current.getMaximalPathLength() + 1);
                }
            }

            to = nextTos;
        }

        // Print the maximum length path
        StringBuilder stringBuilder = new StringBuilder();
        Node departure = primaryNode;
        while (!isNull(departure)) {
            stringBuilder.append(departure.getId());
            departure = departure.getNextNodeOnMaximalPath();
        }

        System.out.println(stringBuilder);
    }

    private record Edge(Integer from, Integer to) {
    }
}

class Node {
    private final Integer id;
    private final Set<Node> from = new HashSet<>();
    private final Set<Node> to = new HashSet<>();
    private Integer maximalPathLength = 0;
    private Node getNextNodeOnMaximalPath;

    Node(final Integer id) {
        this.id = id;
        this.getNextNodeOnMaximalPath = null;
    }

    Integer getId() {
        return id;
    }

    Set<Node> getFrom() {
        return from;
    }

    Integer getMaximalPathLength() {
        return maximalPathLength;
    }

    Node getNextNodeOnMaximalPath() {
        return getNextNodeOnMaximalPath;
    }

    void addFromEdge(Node node) {
        from.add(node);
    }

    void addToEdge(Node node) {
        to.add(node);
    }

    void compareNewRoute(Node node, Integer distanceTravelled) {
        if (distanceTravelled > maximalPathLength) {
            maximalPathLength = distanceTravelled;
            this.getNextNodeOnMaximalPath = node;
        }
    }

    boolean isAnEntryPoint() {
        return from.isEmpty();
    }

    boolean isAnEndPoint() {
        return to.isEmpty();
    }
}
