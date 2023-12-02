package com.ee364project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ee364project.file_manage.Csv;
import com.ee364project.file_manage.Zip;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Write {
    public static void main(String[] args) throws IOException {
        //Utilities.getFakeData(5, Vars.DataClasses.Department);
        //Utilities.getFakeData(20, Vars.DataClasses.Problem);
//
        //HasData[] customers = Utilities.getFakeData(20,Vars.DataClasses.Customer);
        //HasData[] agents = Utilities.getFakeData(5,Vars.DataClasses.Agent);
//
        //Csv.write(customers, "call_center\\output\\Customer.csv");
        //Csv.write(agents, "call_center\\output\\Agent.csv");
        //Csv.write(Problem.getAllProblems(), "call_center\\output\\Problem.csv");

        // Zip.compressToZip("output3.zip", "call_center\\output");
        Zip.deleteExtracted("call_center\\output");
        //Zip.deleteExtracted("extracted");

    }    
}
