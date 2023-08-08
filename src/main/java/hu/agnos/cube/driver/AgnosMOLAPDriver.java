/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver;

import hu.agnos.molap.Cube;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author parisek
 */
public class AgnosMOLAPDriver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String path = args[0];
        Cube cube = loader(path);
        CubeHandler ch = new CubeHandler(cube.getHierarchyHeader(), cube.getMeasureHeader());
        ch.printDims(cube);
        ch.printCells(cube);
        
    }

    private static Cube loader(String path) {

        Cube cube = null;
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            cube = (hu.agnos.molap.Cube) in.readObject();

            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(AgnosMOLAPDriver.class.getName()).log(Level.SEVERE, "MOLAP Cube loading failed.", ex);
        }
        return cube;
    }

}
