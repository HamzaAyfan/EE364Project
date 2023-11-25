package com.ee364project;

import com.ee364project.file_manage.Csv;

public class Main {
    public static void main(String[] args) {
        HasData[] data = Csv.read("File path");
        for (HasData datum : data) {
            System.out.println(datum);
        }
    }

}


