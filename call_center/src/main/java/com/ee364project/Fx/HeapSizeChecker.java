package com.ee364project.Fx;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;

/**
 * This class is used to keep track of the memory usage of the program.
 * 
 * @author Mehdi Bathalath
 */
public class HeapSizeChecker {

    /**
     * This method is used to check the heap memory size of the JVM.
     * 
     */
    public static void checkMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        NumberFormat numberFormat = NumberFormat.getInstance();
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("Init: " + numberFormat.format(heapMemoryUsage.getInit()) + " bytes");
        System.out.println("Used: " + numberFormat.format(heapMemoryUsage.getUsed()) + " bytes");
        System.out.println("Committed: " + numberFormat.format(heapMemoryUsage.getCommitted()) + " bytes");
        System.out.println("Max: " + numberFormat.format(heapMemoryUsage.getMax()) + " bytes");
    }
}