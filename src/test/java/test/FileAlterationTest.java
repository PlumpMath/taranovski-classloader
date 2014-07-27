/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alyx
 */
public class FileAlterationTest {

    private static File file1;
    private static int number = 0;

    public FileAlterationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        file1 = new File("testFile1.txt");
        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileAlterationTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() {
        number++;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void alterFile() {
        long time1 = file1.lastModified();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileAlterationTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file1, true))) {
            writer.write("some_new_string" + number + "\n");
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(FileAlterationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        long time2 = file1.lastModified();
        assertTrue(time2 > time1);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
