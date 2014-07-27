/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.training.myclassloader;

import java.lang.reflect.Method;

/**
 * CCLRun executes a Java program by loading it through a CompilingClassLoader.
 *
 * @author Alyx
 */
public class MyClassLoaderRun {

    /**
     *
     * @param args
     * @throws Exception
     */
    static public void main(String args[]) throws Exception {
        // Create a CompilingClassLoader
        MyClassLoader ccl;
        // Load the main class through our CCL
        Class clas;
        Object testObject;

        while (true) {
            ccl = new MyClassLoader(/*System.out, true*/);
            clas = ccl.loadClass("com.epam.training.myclassloader.TestModule");
            testObject = clas.newInstance();
            System.out.println(testObject);
            Thread.sleep(1000);
        }
//        // Use reflection to call its main() method, and to
//        // pass the arguments in.
//        // Get a class representing the type of the main method's argument
//        Class mainArgType[] = {(new String[0]).getClass()};
//        // Find the standard main method in the class
//        Method main = clas.getMethod("main", mainArgType);
//        // Create a list containing the arguments -- in this case,
//        // an array of strings
//        Object argsArray[] = {progArgs};
//        // Call the method
//        main.invoke(null, argsArray);
    }
}
