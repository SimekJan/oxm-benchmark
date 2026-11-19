package cz.cuni.mff.simekjan.generator.orderer;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;

import java.util.List;

/**
 * Decides the order of data generation for a given dataset.
 * Uses internal oriented graph representation to determine the order
 * of generation based on relationships between tables.
 */
public class DataGeneratorOrderer {

    /**
     * Prepares the internal graph representation of the dataset and decides the order of data generation.
     *
     * @param dataset Dataset to analyze and determine the generation order for.
     */
    public void decideOrder(DatasetEntity dataset) {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Returns the next batch of items to be generated based on the previously decided order.
     *
     * @return List of table/relationships IDs for next item batch to generate.
     */
    public List<String> nextItemBatch() {
        throw new RuntimeException("Not implemented yet");
    }
}
