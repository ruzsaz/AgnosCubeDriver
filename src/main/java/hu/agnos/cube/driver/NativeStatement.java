/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver;

import hu.agnos.molap.Cube;
import hu.agnos.cube.driver.zolikaokos.DataRetriever;
import hu.agnos.cube.driver.zolikaokos.Problem;
import hu.agnos.cube.driver.zolikaokos.ResultElement;
import java.util.ArrayList;
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

    private Cube cube;

    public NativeStatement(Cube cube) {
        this.cube = cube;
    }

    public ResultSet[] executeQueries(String baseVector, String[] drillVectors) {
        int drillVectorsSize = drillVectors.length;
        
        
        
        ResultSet[] resultSet = new ResultSet[drillVectorsSize];        
        DataRetriever retriever = new DataRetriever(cube);

        for (int i = 0; i < drillVectors.length; i++) {
            String[] newBaseVectorArray = null;
            String drillVector = drillVectors[i];
            
            resultSet[i] = new ResultSet(drillVector);
            
            if (drillVector != null) {
                String[] baseVectorArray = baseVector.split(":", -1);
                String[] drillVectorArray = drillVector.split(":", -1);
                NativeQueryGenerator queryGenerator = new NativeQueryGenerator(cube.getDimensions(), cube.getHierarchyIndex());
                newBaseVectorArray = queryGenerator.getBaseVectorsFromDrillVector(baseVectorArray, drillVectorArray);
            }

            if (newBaseVectorArray != null) {
                int rowCnt = newBaseVectorArray.length;
                for (int j = 0; j < rowCnt; j++) {
                    retriever.addProblem(new Problem(i, newBaseVectorArray[j]));
                }
            }
        }
        
        List<Future<ResultElement>> futures = retriever.computeAll();
        
        
        List<List<ResultElement>>  eredmeny = new ArrayList<>();
        for(int i= 0; i < drillVectorsSize; i++){
            List<ResultElement> temp = new ArrayList<>();
            eredmeny.add(temp);
        }
        
         
        
        
        for(int i = 0; i < futures.size(); i++){
             try {
                 
                    ResultElement r = futures.get(i).get();
//                    for(String s : r.getHeader()){
//                        System.out.print(s + ", ");
//                    }
//                    System.out.println("");
                    int drillVectorId = r.getDrillVectorId();
                    List<ResultElement> temp = eredmeny.get(drillVectorId);
                    temp.add(r);                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(NativeStatement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(NativeStatement.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
      
        for(int i = 0; i < drillVectorsSize; i++){
            resultSet[i].setResponse(eredmeny.get(i));         
        }
        
        return resultSet;
    }

    public ResultSet executeQuery(String baseVector, String drillVector) {
        ResultSet resultSet = null;
        String[] newBaseVectorArray = null;

        if (drillVector != null) {
            String[] baseVectorArray = baseVector.split(":", -1);
            String[] drillVectorArray = drillVector.split(":", -1);
            NativeQueryGenerator queryGenerator = new NativeQueryGenerator(cube.getDimensions(), cube.getHierarchyIndex());

            newBaseVectorArray = queryGenerator.getBaseVectorsFromDrillVector(baseVectorArray, drillVectorArray);

        }

        if (newBaseVectorArray != null) {
            resultSet = new ResultSet(drillVector);
            ResultElement[] response = null;
            int rowCnt = newBaseVectorArray.length;

            DataRetriever retriever = new DataRetriever(cube);
            for (int i = 0; i < rowCnt; i++) {
                retriever.addProblem(new Problem(0, newBaseVectorArray[i]));
            }

            List<Future<ResultElement>> futures = retriever.computeAll();

            response = new ResultElement[rowCnt];

            for (int i = 0; i < rowCnt; i++) {
                try {
                    response[i] = futures.get(i).get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NativeStatement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(NativeStatement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

//            resultSet.setResponse(response);

        }
        return resultSet;
    }

}
