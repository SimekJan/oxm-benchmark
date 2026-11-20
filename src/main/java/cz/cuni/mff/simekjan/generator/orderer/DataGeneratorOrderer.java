package cz.cuni.mff.simekjan.generator.orderer;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipType;
import cz.cuni.mff.simekjan.application.table.TableEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Decides the order of data generation for a given dataset.
 * Uses internal oriented graph representation to determine the order
 * of generation based on relationships between tables.
 */
public class DataGeneratorOrderer {

    private Map<Node, List<Edge>> graph = Map.of();
    private Map<Node, RelationshipType> relationshipTypes = Map.of();

    /**
     * Prepares the internal graph representation of the dataset and decides the order of data generation.
     *
     * @param dataset Dataset to analyze and determine the generation order for.
     */
    public void decideOrder(DatasetEntity dataset) {
        Objects.requireNonNull(dataset, "dataset must not be null");

        Map<Node, List<Edge>> newGraph = new LinkedHashMap<>();
        Map<Node, RelationshipType> newRelationshipTypes = new LinkedHashMap<>();
        for (TableEntity table : dataset.getTables()) {
            Node tableNode = new Node(NodeType.TABLE, Objects.requireNonNull(table.getId(), "table id must not be null"));
            newGraph.put(tableNode, new ArrayList<>());
        }

        for (RelationshipEntity relationship : dataset.getRelationships()) {
            Node relationshipNode = new Node(
                NodeType.RELATIONSHIP,
                Objects.requireNonNull(relationship.getId(), "relationship id must not be null")
            );
            newGraph.put(relationshipNode, new ArrayList<>());
            newRelationshipTypes.put(
                relationshipNode,
                Objects.requireNonNull(relationship.getRelationshipType(), "relationship type must not be null")
            );
        }

        for (RelationshipEntity relationship : dataset.getRelationships()) {
            TableEntity sourceTable = Objects.requireNonNull(
                Objects.requireNonNull(relationship.getSourceColumn(), "relationship source column must not be null")
                    .getTable(),
                "relationship source table must not be null"
            );
            TableEntity targetTable = Objects.requireNonNull(
                Objects.requireNonNull(relationship.getTargetColumn(), "relationship target column must not be null")
                    .getTable(),
                "relationship target table must not be null"
            );
            Node sourceNode = new Node(
                NodeType.TABLE,
                Objects.requireNonNull(sourceTable.getId(), "source table id must not be null")
            );
            Node targetNode = new Node(
                NodeType.TABLE,
                Objects.requireNonNull(targetTable.getId(), "target table id must not be null")
            );
            Node relationshipNode = new Node(
                NodeType.RELATIONSHIP,
                Objects.requireNonNull(relationship.getId(), "relationship id must not be null")
            );

            List<Edge> sourceEdges = newGraph.get(sourceNode);
            List<Edge> targetEdges = newGraph.get(relationshipNode);
            if (sourceEdges == null || targetEdges == null || !newGraph.containsKey(targetNode)) {
                throw new IllegalArgumentException("Relationship references a table or relationship outside the dataset");
            }

            Cardinalities cardinalities = Cardinalities.forType(
                Objects.requireNonNull(relationship.getRelationshipType(), "relationship type must not be null")
            );
            sourceEdges.add(new Edge(relationshipNode, cardinalities.source()));
            targetEdges.add(new Edge(targetNode, cardinalities.target()));
        }

        Map<Node, List<Edge>> immutableGraph = new LinkedHashMap<>();
        newGraph.forEach((node, edges) -> immutableGraph.put(node, List.copyOf(edges)));
        graph = Collections.unmodifiableMap(immutableGraph);
        relationshipTypes = Collections.unmodifiableMap(newRelationshipTypes);
    }

    /**
     * Returns the next batch of items to be generated based on the previously decided order.
     *
     * @return List of table/relationships IDs for next item batch to generate.
     */
    public List<String> nextItemBatch() {
        discardCompletedInlineRelationships();
        if (graph.isEmpty()) {
            return List.of();
        }

        List<Node> batch = new ArrayList<>();
        for (Map.Entry<Node, List<Edge>> entry : graph.entrySet()) {
            Node node = entry.getKey();
            if (node.type() == NodeType.RELATIONSHIP) {
                if (isReadyRelationship(node)) {
                    batch.add(node);
                }
            } else if (isReadyTable(node)) {
                batch.add(node);
            }
        }

        if (batch.isEmpty()) {
            throw new IllegalStateException("Unable to determine the next generation batch; the relationship graph contains a cycle");
        }

        List<String> itemIds = batch.stream().map(node -> node.id().toString()).toList();
        Map<Node, List<Edge>> remainingGraph = new LinkedHashMap<>();
        for (Map.Entry<Node, List<Edge>> entry : graph.entrySet()) {
            if (!batch.contains(entry.getKey())) {
                List<Edge> remainingEdges = entry.getValue().stream()
                    .filter(edge -> !batch.contains(edge.target()))
                    .toList();
                remainingGraph.put(entry.getKey(), remainingEdges);
            }
        }
        graph = Collections.unmodifiableMap(remainingGraph);
        return itemIds;
    }

    private boolean isReadyRelationship(Node relationshipNode) {
        RelationshipType type = relationshipTypes.get(relationshipNode);
        if (type != RelationshipType.ONE_TO_ONE && type != RelationshipType.MANY_TO_MANY) {
            return false;
        }

        for (Map.Entry<Node, List<Edge>> entry : graph.entrySet()) {
            for (Edge edge : entry.getValue()) {
                if (edge.target().equals(relationshipNode) || entry.getKey().equals(relationshipNode)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isReadyTable(Node tableNode) {
        List<Node> incomingRelationships = graph.entrySet().stream()
            .filter(entry -> entry.getKey().type() == NodeType.RELATIONSHIP)
            .filter(entry -> entry.getValue().stream().anyMatch(edge -> edge.target().equals(tableNode)))
            .map(Map.Entry::getKey)
            .toList();
        if (incomingRelationships.isEmpty()
            || incomingRelationships.stream().allMatch(this::hasNoIncomingEdges)) {
            return true;
        }

        List<Cardinality> cardinalities = new ArrayList<>();
        for (Map.Entry<Node, List<Edge>> entry : graph.entrySet()) {
            for (Edge edge : entry.getValue()) {
                if (entry.getKey().equals(tableNode) || edge.target().equals(tableNode)) {
                    cardinalities.add(edge.cardinality());
                }
            }
        }
        return !cardinalities.isEmpty()
            && cardinalities.stream().allMatch(cardinality -> cardinality == Cardinality.MANY);
    }

    private boolean hasNoIncomingEdges(Node node) {
        return graph.values().stream()
            .flatMap(List::stream)
            .noneMatch(edge -> edge.target().equals(node));
    }

    private void discardCompletedInlineRelationships() {
        List<Node> completedRelationships = graph.keySet().stream()
            .filter(node -> node.type() == NodeType.RELATIONSHIP)
            .filter(node -> {
                RelationshipType type = relationshipTypes.get(node);
                return type == RelationshipType.ONE_TO_MANY || type == RelationshipType.MANY_TO_ONE;
            })
            .filter(node -> {
                boolean hasIncidentEdges = graph.entrySet().stream().anyMatch(entry ->
                    entry.getKey().equals(node)
                        ? !entry.getValue().isEmpty()
                        : entry.getValue().stream().anyMatch(edge -> edge.target().equals(node))
                );
                return !hasIncidentEdges;
            })
            .toList();
        if (completedRelationships.isEmpty()) {
            return;
        }

        Map<Node, List<Edge>> remainingGraph = new LinkedHashMap<>();
        graph.forEach((node, edges) -> {
            if (!completedRelationships.contains(node)) {
                remainingGraph.put(
                    node,
                    edges.stream().filter(edge -> !completedRelationships.contains(edge.target())).toList()
                );
            }
        });
        graph = Collections.unmodifiableMap(remainingGraph);
    }

    /**
     * Returns true if there are more items to be generated, false otherwise.
     *
     * @return Whether there are more items to be generated.
     */
    public boolean hasNextItemBatch() {
        return !graph.isEmpty();
    }

    private enum NodeType {
        TABLE,
        RELATIONSHIP
    }

    private enum Cardinality {
        ONE,
        MANY
    }

    private record Node(NodeType type, UUID id) {
    }

    private record Edge(Node target, Cardinality cardinality) {
    }

    private record Cardinalities(Cardinality source, Cardinality target) {
        private static Cardinalities forType(RelationshipType type) {
            return switch (type) {
                case ONE_TO_ONE -> new Cardinalities(Cardinality.ONE, Cardinality.ONE);
                case ONE_TO_MANY -> new Cardinalities(Cardinality.ONE, Cardinality.MANY);
                case MANY_TO_ONE -> new Cardinalities(Cardinality.MANY, Cardinality.ONE);
                case MANY_TO_MANY -> new Cardinalities(Cardinality.MANY, Cardinality.MANY);
            };
        }
    }
}
