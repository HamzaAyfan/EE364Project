package com.ee364project.Fx;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;

/**
 * This class is used to keep track of the memory usage of the program.
 * 
 * @author Mahdi Bathalath
 */
public class HeapSizeChecker {

    /**
     * This method is used to check the heap memory size of the JVM.
     * 
     */
    public static void checkMemory() {
        // Obtain the MemoryMXBean to retrieve memory usage information
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

         // Create a NumberFormat instance for formatting memory sizes
        NumberFormat numberFormat = NumberFormat.getInstance();

        // Clear the console screen
        System.out.print("\033[H\033[2J");
        System.out.flush();

         // Display heap memory usage 
        System.out.println("Init: " + numberFormat.format(heapMemoryUsage.getInit()) + " bytes");
        System.out.println("Used: " + numberFormat.format(heapMemoryUsage.getUsed()) + " bytes");
        System.out.println("Committed: " + numberFormat.format(heapMemoryUsage.getCommitted()) + " bytes");
        System.out.println("Max: " + numberFormat.format(heapMemoryUsage.getMax()) + " bytes");
    }
}
