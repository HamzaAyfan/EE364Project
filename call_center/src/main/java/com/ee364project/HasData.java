package com.ee364project;
/**
 * The {@code HasData} interface defines a contract for classes that represent data.
 * Implementing classes should provide methods for obtaining information about the data,
 * such as its type name, headers, and actual content. The interface also includes
 * methods for parsing and shuffling data [parsing for reading from CSV and shuffle for
 * generating new data]
 */
public interface HasData {
    /**
     * Returns the name of the data type.
     *
     * @return The name of the data type.
     */
    String getDataTypeName();
/**
     * Returns an array of headers that describe the structure of the data.
     *
     * @return An array of headers.
     */
    String[] getHeaders();
/**
     * Returns a two-dimensional array of data.
     *
     * @return A two-dimensional array representing the data.
     */
    String[][] getData();
/**
     * Parses an array of data fields and returns a new instance of {@code HasData}
     * with the parsed data.
     *
     * @param dataFields An array of data fields to be parsed.
     * @return A new instance of {@code HasData} with the parsed data.
     */
    HasData parseData(String[] dataFields);
/**
     * Returns a new instance of {@code HasData} with the data shuffled.
     *
     * @return A new instance of {@code HasData} with the data shuffled.
     */
    HasData shuffle();
}
