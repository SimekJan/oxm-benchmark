package cz.cuni.mff.simekjan.generator.orderer;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipType;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DataGeneratorOrdererTest {

    @Test
    void generatesOneSideBeforeManySide() {
        Fixture oneToMany = fixture(RelationshipType.ONE_TO_MANY);
        DataGeneratorOrderer oneToManyOrderer = orderer(oneToMany);
        assertEquals(List.of(oneToMany.source().getId()), oneToManyOrderer.nextItemBatch());
        assertEquals(List.of(oneToMany.target().getId()), oneToManyOrderer.nextItemBatch());
        assertFalse(oneToManyOrderer.hasNextItemBatch());

        Fixture manyToOne = fixture(RelationshipType.MANY_TO_ONE);
        DataGeneratorOrderer manyToOneOrderer = orderer(manyToOne);
        assertEquals(List.of(manyToOne.target().getId()), manyToOneOrderer.nextItemBatch());
        assertEquals(List.of(manyToOne.source().getId()), manyToOneOrderer.nextItemBatch());
        assertFalse(manyToOneOrderer.hasNextItemBatch());
    }

    @Test
    void generatesManyToManyRelationshipAfterBothTables() {
        Fixture fixture = fixture(RelationshipType.MANY_TO_MANY);
        DataGeneratorOrderer orderer = orderer(fixture);

        assertEquals(List.of(fixture.source().getId(), fixture.target().getId()), orderer.nextItemBatch());
        assertEquals(List.of(fixture.relationship().getId()), orderer.nextItemBatch());
        assertFalse(orderer.hasNextItemBatch());
    }

    private DataGeneratorOrderer orderer(Fixture fixture) {
        DataGeneratorOrderer orderer = new DataGeneratorOrderer();
        orderer.decideOrder(fixture.dataset());
        return orderer;
    }

    private Fixture fixture(RelationshipType type) {
        TableEntity source = table();
        TableEntity target = table();
        ColumnEntity sourceColumn = column(source);
        ColumnEntity targetColumn = column(target);

        RelationshipEntity relationship = new RelationshipEntity();
        relationship.setId(UUID.randomUUID());
        relationship.setRelationshipType(type);
        relationship.setSourceColumn(sourceColumn);
        relationship.setTargetColumn(targetColumn);

        DatasetEntity dataset = new DatasetEntity();
        dataset.setTables(List.of(source, target));
        dataset.setRelationships(List.of(relationship));
        return new Fixture(dataset, source, target, relationship);
    }

    private TableEntity table() {
        TableEntity table = new TableEntity();
        table.setId(UUID.randomUUID());
        return table;
    }

    private ColumnEntity column(TableEntity table) {
        ColumnEntity column = new ColumnEntity();
        column.setId(UUID.randomUUID());
        column.setTable(table);
        return column;
    }

    private record Fixture(
        DatasetEntity dataset,
        TableEntity source,
        TableEntity target,
        RelationshipEntity relationship
    ) {
    }
}
