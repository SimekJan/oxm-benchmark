package cz.cuni.mff.simekjan.generator.orderer;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipType;
import cz.cuni.mff.simekjan.application.table.TableEntity;

import java.util.ArrayList;
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

    private List<Node> graph = List.of();

    /**
     * Prepares the internal graph representation of the dataset and decides the order of data generation.
     *
     * @param dataset Dataset to analyze and determine the generation order for.
     */
    public void decideOrder(DatasetEntity dataset) {
        Objects.requireNonNull(dataset, "dataset must not be null");

        List<Node> nodes = new ArrayList<>();
        Map<UUID, Node> tableNodes = new LinkedHashMap<>();
        Map<UUID, Node> relationshipNodes = new LinkedHashMap<>();
        for (TableEntity table : dataset.getTables()) {
            UUID tableId = Objects.requireNonNull(table.getId(), "table id must not be null");
            Node tableNode = Node.table(tableId);
            tableNodes.put(tableId, tableNode);
            nodes.add(tableNode);
        }

        for (RelationshipEntity relationship : dataset.getRelationships()) {
            UUID relationshipId = Objects.requireNonNull(relationship.getId(), "relationship id must not be null");
            Node relationshipNode = Node.relationship(relationshipId);
            relationshipNodes.put(relationshipId, relationshipNode);
            nodes.add(relationshipNode);
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
            Node sourceNode = tableNodes.get(Objects.requireNonNull(
                sourceTable.getId(),
                "source table id must not be null"
            ));
            Node targetNode = tableNodes.get(Objects.requireNonNull(
                targetTable.getId(),
                "target table id must not be null"
            ));
            Node relationshipNode = relationshipNodes.get(Objects.requireNonNull(
                relationship.getId(),
                "relationship id must not be null"
            ));
            if (sourceNode == null || targetNode == null || relationshipNode == null) {
                throw new IllegalArgumentException("Relationship references a table or relationship outside the dataset");
            }

            relationshipNode.configureRelationship(
                sourceNode,
                targetNode,
                Objects.requireNonNull(relationship.getRelationshipType(), "relationship type must not be null")
            );
        }

        graph = nodes;
    }

    /**
     * Returns the next batch of items to be generated based on the previously decided order.
     *
     * @return List of table/relationships IDs for next item batch to generate.
     */
    public List<UUID> nextItemBatch() {
        discardCompletedInlineRelationships();
        if (graph.isEmpty()) {
            return List.of();
        }

        List<Node> batch = new ArrayList<>();
        for (Node node : graph) {
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

        List<UUID> itemIds = batch.stream().map(Node::id).toList();
        graph.removeAll(batch);
        discardCompletedInlineRelationships();
        return itemIds;
    }

    private boolean isReadyRelationship(Node relationshipNode) {
        if (relationshipNode.relationshipType() != RelationshipType.ONE_TO_ONE
            && relationshipNode.relationshipType() != RelationshipType.MANY_TO_MANY) {
            return false;
        }
        return !graph.contains(relationshipNode.sourceTable())
            && !graph.contains(relationshipNode.targetTable());
    }

    private boolean isReadyTable(Node tableNode) {
        for (Node relationshipNode : graph) {
            if (relationshipNode.type() != NodeType.RELATIONSHIP) {
                continue;
            }
            boolean isSource = relationshipNode.sourceTable().equals(tableNode);
            boolean isTarget = relationshipNode.targetTable().equals(tableNode);
            if (!isSource && !isTarget) {
                continue;
            }

            if (relationshipNode.relationshipType() == RelationshipType.ONE_TO_MANY
                || relationshipNode.relationshipType() == RelationshipType.MANY_TO_ONE) {
                Cardinality tableCardinality = relationshipNode.cardinalityFor(tableNode);
                Node prerequisite = isSource
                    ? relationshipNode.targetTable()
                    : relationshipNode.sourceTable();
                if (tableCardinality == Cardinality.MANY && graph.contains(prerequisite)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void discardCompletedInlineRelationships() {
        graph.removeIf(node ->
            node.type() == NodeType.RELATIONSHIP
                && (node.relationshipType() == RelationshipType.ONE_TO_MANY
                    || node.relationshipType() == RelationshipType.MANY_TO_ONE)
                && !graph.contains(node.sourceTable())
                && !graph.contains(node.targetTable())
        );
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

    private record Edge(Node target, Cardinality cardinality) {
    }

    private static final class Node {
        private final NodeType type;
        private final UUID id;
        private final List<Edge> edges = new ArrayList<>();
        private Node sourceTable;
        private Node targetTable;
        private RelationshipType relationshipType;

        private Node(NodeType type, UUID id) {
            this.type = type;
            this.id = id;
        }

        private static Node table(UUID id) {
            return new Node(NodeType.TABLE, id);
        }

        private static Node relationship(UUID id) {
            return new Node(NodeType.RELATIONSHIP, id);
        }

        private void configureRelationship(Node sourceTable, Node targetTable, RelationshipType relationshipType) {
            this.sourceTable = sourceTable;
            this.targetTable = targetTable;
            this.relationshipType = relationshipType;
            edges.add(new Edge(targetTable, cardinalityFor(relationshipType, false)));
            sourceTable.edges.add(new Edge(this, cardinalityFor(relationshipType, true)));
        }

        private Cardinality cardinalityFor(Node table) {
            if (table == sourceTable) {
                return sourceTable.edges.stream()
                    .filter(edge -> edge.target() == this)
                    .map(Edge::cardinality)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Missing source edge for relationship"));
            }
            if (table == targetTable) {
                return edges.stream()
                    .filter(edge -> edge.target() == table)
                    .map(Edge::cardinality)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Missing target edge for relationship"));
            }
            throw new IllegalArgumentException("Table is not an endpoint of this relationship");
        }

        private static Cardinality cardinalityFor(RelationshipType type, boolean source) {
            return switch (type) {
                case ONE_TO_ONE -> Cardinality.ONE;
                case MANY_TO_MANY -> Cardinality.MANY;
                case ONE_TO_MANY -> source ? Cardinality.ONE : Cardinality.MANY;
                case MANY_TO_ONE -> source ? Cardinality.MANY : Cardinality.ONE;
            };
        }

        private NodeType type() {
            return type;
        }

        private UUID id() {
            return id;
        }

        private Node sourceTable() {
            return sourceTable;
        }

        private Node targetTable() {
            return targetTable;
        }

        private RelationshipType relationshipType() {
            return relationshipType;
        }
    }
}
