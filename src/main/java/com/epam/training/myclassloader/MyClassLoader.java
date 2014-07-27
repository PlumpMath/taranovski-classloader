package com.epam.training.myclassloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A CompilingClassLoader compiles your Java source on-the-fly. It checks for
 * nonexistent .class files, or .class files that are older than their
 * corresponding source code.
 *
 * @author Alyx
 */
public class MyClassLoader extends ClassLoader {

    Writer printWriter;
    boolean debug = false;

    /**
     *
     * @param os output stream to write to
     * @param debug if debug mode
     */
    public MyClassLoader(OutputStream os, boolean debug) {
        printWriter = new PrintWriter(new OutputStreamWriter(os));
        this.debug = debug;
    }

    /**
     * default constructor assigns standart system out to write to
     */
    public MyClassLoader() {
        printWriter = new PrintWriter(new OutputStreamWriter(System.out));
    }

    /**
     * Given a filename, read the entirety of that file from disk and return it
     * as a byte array.
     */
    private byte[] getBytes(String filename) throws IOException {
        // Find out the length of the file
        File file = new File(filename);
        long len = file.length();
        // Create an array that's just the right size for the file's contents
        byte raw[] = new byte[(int) len];
        // Read all of it into the array; if we don't get all, then it's an error.
        try (FileInputStream fin = new FileInputStream(file)) {
            // Read all of it into the array; if we don't get all, then it's an error.
            int r = fin.read(raw);
            if (r != len) {
                throw new IOException("Can't read all, " + r + " != " + len);
            }
        }
        // And finally return the file contents as an array
        return raw;
    }

    /**
     * Spawn a process to compile the java source code file specified in the
     * 'javaFile' parameter. Return a true if the compilation worked, false
     * otherwise.
     */
    private boolean compile(String javaFile) {
        if (debug) {
            try {
                // Let the user know what's going on
                printWriter.write("CCL: Compiling " + javaFile + "...\n");
                printWriter.flush();

            } catch (IOException ex) {
                Logger.getLogger(MyClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Start up the compiler
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("javac -d target\\classes " + javaFile);

            //javac -d target\classes src\main\java\com\epam\training\myclassloader\TestModule.java
        } catch (IOException ex) {
            Logger.getLogger(MyClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Wait for it to finish running
        try {
            p.waitFor();
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
        // Check the return code, in case of a compilation error
        int ret = p.exitValue();
        // Tell whether the compilation worked
        return ret == 0;
    }

    /**
     * The heart of the ClassLoader -- automatically compile source as necessary
     * when looking for class files
     *
     * @return
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Our goal is to get a Class object
        Class clas = null;
        // First, see if we've already dealt with this one
        clas = findLoadedClass(name);
        if (debug) {
            try {
                printWriter.write("findLoadedClass: " + clas + "\n");
                printWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(MyClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Create a pathname from the class name E.g. java.lang.Object => java/lang/Object
        String fileStub = name.replace('.', '/');
        // Build objects pointing to the source code (.java) and object code (.class)
        String javaFilename = "src/main/java/" + fileStub + ".java";
        String classFilename = "target/classes/" + fileStub + ".class";
        File javaFile = new File(javaFilename);
        File classFile = new File(classFilename);

        if (debug) {
            try {
                printWriter.write(javaFile.toString() + "\n");
                printWriter.write(javaFile.exists() + "\n");
                printWriter.write(classFile.toString() + "\n");
                printWriter.write(classFile.exists() + "\n");

                printWriter.write("j " + javaFile.lastModified() + " c "
                        + classFile.lastModified() + "\n");
                printWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(MyClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /* 
         First, see if we want to try compiling. We do if (a) there
         is source code, and either (b0) there is no object code,
         or (b1) there is object code, but it's older than the source
         */
        if (javaFile.exists() & (!classFile.exists()
                || javaFile.lastModified() > classFile.lastModified())) {
            /* 
             Try to compile it. If this doesn't work, then
             we must declare failure. (It's not good enough to use
             and already-existing, but out-of-date, classfile)
             */
            if (!compile(javaFilename) || !classFile.exists()) {
                throw new ClassNotFoundException("Compile failed: " + javaFilename);
            }
        }
        /* 
         Let's try to load up the raw bytes, assuming they were
         properly compiled, or didn't need to be compiled
         */
        try {
            // read the bytes
            byte raw[] = getBytes(classFilename);
            // try to turn them into a class
            clas = defineClass(name, raw, 0, raw.length);
        } catch (IOException ie) {
            /* 
             This is not a failure! If we reach here, it might
             mean that we are dealing with a class in a library,
             such as java.lang.Object
             */
        }
        if (debug) {
            try {
                printWriter.write("defineClass: " + clas + "\n");
                printWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(MyClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Maybe the class is in a library -- try loading the normal way
        if (clas == null) {
            clas = findSystemClass(name);
        }
        if (debug) {
            try {
                printWriter.write("findSystemClass: " + clas + "\n");
                printWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(MyClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Resolve the class, if any, but only if the "resolve" flag is set to true
        if (resolve && clas != null) {
            resolveClass(clas);
        }
        // If we still don't have a class, it's an error
        if (clas == null) {
            throw new ClassNotFoundException(name);
        }
        // Otherwise, return the class
        return clas;
    }
}
