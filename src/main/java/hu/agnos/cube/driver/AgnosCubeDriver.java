package hu.agnos.cube.driver;

import hu.agnos.cube.Cube;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author parisek
 */
public class AgnosCubeDriver {

    /**
     * Direct running this project is ONLY FOR TESTING.
     * So this will never be used in production.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String path = args[0];
        Cube cube = loader(path);
        CubeHandler ch = new CubeHandler(cube.getDimensionHeader(), cube.getMeasureHeader());
        //ch.printDims(cube);
        //ch.printCells(cube);
        
    }

    private static Cube loader(String path) {
        Cube cube = null;
        try ( FileInputStream fileIn = new FileInputStream(path); ObjectInputStream in = new ObjectInputStream(fileIn)){
            cube = (Cube) in.readObject();
            cube.init();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(AgnosCubeDriver.class.getName()).log(Level.SEVERE, "MOLAP Cube loading failed.", ex);
        }
        return cube;
    }

}
