package com.ee364project.file_manage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.ee364project.HasData;
import com.ee364project.Problem;
import com.ee364project.Fx.MainSceneController;
import com.ee364project.helpers.Vars;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

/**
 * The {@code Csv} class provides utility methods for reading and writing CSV
 * files
 * using objects that implement the {@code HasData} interface. It includes
 * methods
 * to write an array of objects to a CSV file and read data from a CSV file into
 * an
 * array of objects. The class supports automatic determination of the class
 * name based
 * on the file name (excluding the extension) for convenient file operations.
 */
public final class Csv {
    /**
     * Writes an array of objects that implement the {@code HasData} interface to a
     * CSV file.
     * The CSV file is created at the specified path. If the array is empty, no file
     * is created.
     *
     * @param objects the array of objects implementing the {@code HasData}
     *                interface to write to the CSV file.
     * @param path    the path where the CSV file will be created.
     */
    public static void write(HasData[] objects, String path) {
        if (objects.length == 0) {
            return;// no data
        }
        File file = new File(path);
        File fileParent = new File(file.getParent());

        // make parent dirs if not made
        fileParent.mkdirs();
        try {
            FileWriter fileWriter = new FileWriter(path);
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            // get the csv headers (first row)
            Object[] headers = (Object[]) objects[0].getHeaders();
            printer.printRecord(headers);
            for (HasData datum : objects) {
                for (String[] arr : datum.getData()) {
                    Object[] arrObj = (Object[]) arr;
                    printer.printRecord(arrObj);
                }
            }
            printer.close();
        } catch (IOException e) {
            MainSceneController.showErrorAlert("Failed attempt to save changes",
                    "Error encountered while saving the file");
        }
    }

    /**
     * Reads data from a file specified by the given path and creates an array of
     * objects of the specified class.
     *
     * @param path    The path to the file from which data will be read.
     * @param clsName The fully qualified class name of the objects to be created.
     * @return An array of objects representing the data read from the file.
     */
    public static HasData[] read(String path, String clsName) {
        ArrayList<HasData> objects = new ArrayList<>();
        try {
            HasData object;
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            try (CSVParser reader = new CSVParser(fileReader, CSVFormat.DEFAULT)) {

                // get the actual java class from the class name string
                Class<?> DataClass = Class.forName(Vars.projectPrefix + clsName);

                // for making the csv file
                ArrayList<ArrayList<String>> mat = new ArrayList<>();
                ArrayList<String> row = new ArrayList<>();

                // populate the csv file
                for (CSVRecord record : reader.getRecords()) {
                    row = new ArrayList<>();
                    for (String word : record.toList()) {
                        row.add(word);
                    }
                    mat.add(row);
                }
                String[] arg;

                // this code is an exception for the problem class because it requires nested tables.
                for (ArrayList<String> rowInMat : mat.subList(1, mat.size())) {
                    int horzLength = rowInMat.size();
                    String[] line = new String[horzLength];
                    arg = rowInMat.toArray(line);
                    if (clsName.equals("Problem")) {
                        object = Problem.checkRepeatedProblem(arg[0]);
                    } else {
                        object = (HasData) DataClass.getDeclaredConstructor().newInstance();
                    }
                    object.parseData(arg);
                    objects.add(object);
                }
            }
        } catch (Exception e) {
            MainSceneController.showErrorAlert("Invalid File", "File contents are unparsable");
        }

        // make it an array from a arraylist
        int dataSize = objects.size();
        HasData[] data = new HasData[dataSize];
        return objects.toArray(data);
    }

    /**
     * Reads data from a CSV file at the specified path and constructs an array of
     * objects that implement
     * the {@code HasData} interface. The class name is automatically determined
     * based on the file name
     * (excluding the extension). This method is a convenience overload for cases
     * where the class name
     * is the same as the file name (excluding the extension).
     *
     * @param path the path to the CSV file to be read.
     * @return an array of objects implementing the {@code HasData} interface parsed
     *         from the CSV file.
     */
    public static HasData[] read(String path) {
        File file = new File(path);
        String fileName = file.getName().split("\\.")[0];
        return read(path, fileName);
    }

}
