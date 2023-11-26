package com.ee364project;

public interface HasData extends Cloneable {
    String getDataTypeName();

    String[] getHeaders();

    String[][] getData();

    HasData parseData(String[] dataFields);

    HasData shuffle();
}
