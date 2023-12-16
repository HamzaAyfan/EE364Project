package com.ee364project;

import java.io.IOException;
import com.ee364project.file_manage.Zip;


/**
 * This class contains a single function that is used to delete the contents of a directory.
 * 
 * @author Faisal Al-Gadi
 */
public class Write {
    public static void main(String[] args) throws IOException {
        Zip.deleteExtracted("call_center\\output");
    }    
}
