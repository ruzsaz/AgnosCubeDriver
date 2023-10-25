package hu.agnos.cube.driver;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.driver.zolikaokos.DataRetriever;
import hu.agnos.cube.driver.zolikaokos.Problem;
import hu.agnos.cube.driver.zolikaokos.ResultElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author parisek
 */
public class NativeStatement {

    private final Cube cube;

    public NativeStatement(Cube cube) {
        this.cube = cube;
    }

    public ResultSet[] executeQueries(String baseVector, String[] drillVectors) {
        int drillVectorsSize = drillVectors.length;

        ResultSet[] resultSet = new ResultSet[drillVectorsSize];        
        DataRetriever retriever = new DataRetriever();

        for (int i = 0; i < drillVectors.length; i++) {
            List<List<Node>> newBaseVectorArray = null;
            String drillVector = drillVectors[i];
            
            resultSet[i] = new ResultSet(cube.getName(), Arrays.asList(cube.getMeasures().getHeader()), drillVector);
            
            if (drillVector != null) {
                String[] baseVectorArray = baseVector.split(":", -1);
                String[] drillVectorArray = drillVector.split(":", -1);
                newBaseVectorArray = QueryGenerator.getCoordinatesOfDrill(cube.getDimensions(), baseVectorArray, drillVectorArray);
            }

            if (newBaseVectorArray != null) {
                int rowCnt = newBaseVectorArray.size();
                for (List<Node> nodes : newBaseVectorArray) {
                    retriever.addProblem(new Problem(cube, i, nodes));
                }
            }
        }
        
        List<Future<ResultElement>> futures = retriever.computeAll();
        
        
        List<List<ResultElement>> result = new ArrayList<>();
        for(int i= 0; i < drillVectorsSize; i++){
            List<ResultElement> temp = new ArrayList<>();
            result.add(temp);
        }


        for (Future<ResultElement> future : futures) {
            try {
                ResultElement r = future.get();
                int drillVectorId = r.getDrillVectorId();
                List<ResultElement> temp = result.get(drillVectorId);
                temp.add(r);
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(NativeStatement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for(int i = 0; i < drillVectorsSize; i++){
            resultSet[i].setResponse(result.get(i));
        }
        
        return resultSet;
    }

}
