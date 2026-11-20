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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void generatesSelfReferentialRelationshipsAfterTheirTables() {
        for (RelationshipType type : RelationshipType.values()) {
            TableEntity table = table();
            RelationshipEntity relationship = relationship(table, table, type);
            DatasetEntity dataset = new DatasetEntity();
            dataset.setTables(List.of(table));
            dataset.setRelationships(List.of(relationship));
            DataGeneratorOrderer orderer = new DataGeneratorOrderer();
            orderer.decideOrder(dataset);

            assertEquals(List.of(table.getId()), orderer.nextItemBatch(), type.toString());
            assertTrue(orderer.hasNextItemBatch(), type.toString());
            assertEquals(List.of(relationship.getId()), orderer.nextItemBatch(), type.toString());
            assertFalse(orderer.hasNextItemBatch(), type.toString());
            assertEquals(List.of(), orderer.nextItemBatch(), type.toString());
        }
    }

    @Test
    void selfReferentialRelationshipDoesNotBypassOtherDependencies() {
        TableEntity department = table();
        TableEntity employees = table();
        TableEntity suppliers = table();
        RelationshipEntity employeeDepartment = relationship(employees, department, RelationshipType.MANY_TO_ONE);
        RelationshipEntity reportsTo = relationship(employees, employees, RelationshipType.MANY_TO_ONE);
        RelationshipEntity supplierRelationships = relationship(suppliers, suppliers, RelationshipType.MANY_TO_MANY);
        DatasetEntity dataset = new DatasetEntity();
        dataset.setTables(List.of(department, employees, suppliers));
        dataset.setRelationships(List.of(employeeDepartment, reportsTo, supplierRelationships));

        DataGeneratorOrderer orderer = new DataGeneratorOrderer();
        orderer.decideOrder(dataset);

        assertEquals(List.of(department.getId(), suppliers.getId()), orderer.nextItemBatch());
        assertEquals(List.of(employees.getId(), supplierRelationships.getId()), orderer.nextItemBatch());
        assertEquals(List.of(reportsTo.getId()), orderer.nextItemBatch());
        assertFalse(orderer.hasNextItemBatch());
    }

    @Test
    void stillReportsCyclesBetweenDifferentTables() {
        TableEntity first = table();
        TableEntity second = table();
        DatasetEntity dataset = new DatasetEntity();
        dataset.setTables(List.of(first, second));
        dataset.setRelationships(List.of(
            relationship(first, second, RelationshipType.MANY_TO_ONE),
            relationship(second, first, RelationshipType.MANY_TO_ONE)
        ));
        DataGeneratorOrderer orderer = new DataGeneratorOrderer();
        orderer.decideOrder(dataset);

        assertThrows(IllegalStateException.class, orderer::nextItemBatch);
        assertTrue(orderer.hasNextItemBatch());
    }

    private DataGeneratorOrderer orderer(Fixture fixture) {
        DataGeneratorOrderer orderer = new DataGeneratorOrderer();
        orderer.decideOrder(fixture.dataset());
        return orderer;
    }

    private Fixture fixture(RelationshipType type) {
        TableEntity source = table();
        TableEntity target = table();
        RelationshipEntity relationship = relationship(source, target, type);

        DatasetEntity dataset = new DatasetEntity();
        dataset.setTables(List.of(source, target));
        dataset.setRelationships(List.of(relationship));
        return new Fixture(dataset, source, target, relationship);
    }

    private RelationshipEntity relationship(TableEntity source, TableEntity target, RelationshipType type) {
        RelationshipEntity relationship = new RelationshipEntity();
        relationship.setId(UUID.randomUUID());
        relationship.setRelationshipType(type);
        relationship.setSourceColumn(column(source));
        relationship.setTargetColumn(column(target));
        return relationship;
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
