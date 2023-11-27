package com.ee364project.file_manage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.ee364project.HasData;
import com.ee364project.helpers.Vars;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

public final class Csv {

    public static void write(HasData[] objects, String path) {
        if (objects.length == 0) {
            System.out.println("no data");
            return;
        }
        File file = new File(path);
        new File(file.getParent()).mkdirs();
        try {
            CSVPrinter printer = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
            printer.printRecord((Object[]) objects[0].getHeaders());
            for (HasData datum : objects) {
                for (String[] arr : datum.getData()) {
                    printer.printRecord((Object[]) arr);
                }
            }
            printer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HasData[] read(String path, String clsName) {
        ArrayList<HasData> objects = new ArrayList<>();
        try {
            HasData object;
            File file = new File(path);
            CSVParser reader = new CSVParser(new FileReader(file), CSVFormat.DEFAULT);
            Class<?> DataClass = Class.forName(Vars.projectPrefix + clsName);
            ArrayList<ArrayList<String>> mat = new ArrayList<>();
            ArrayList<String> row = new ArrayList<>();
            for (CSVRecord record : reader.getRecords()) {
                row = new ArrayList<>();
                for (String word : record.toList()) {
                    row.add(word);
                }
                mat.add(row);
            }
            String[] arg;
            for (ArrayList<String> rowInMat : mat.subList(1, mat.size())) {
                arg = rowInMat.toArray(new String[rowInMat.size()]);
                object = (HasData) DataClass.getDeclaredConstructor().newInstance();
                System.out.println(arg);
                object.parseData(arg);
                objects.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects.toArray(new HasData[objects.size()]);
    }

    public static HasData[] read(String path) {
        File file = new File(path);
        return read(path, file.getName().split("\\.")[0]);
    }
}
