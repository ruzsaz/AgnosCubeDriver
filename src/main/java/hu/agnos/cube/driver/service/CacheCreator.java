package hu.agnos.cube.driver.service;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.meta.resultDto.ResultElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheCreator {


    private Cube cube;
    private ProblemFactory problemFactory;

    public void createCache(Cube cube) {
        this.cube = cube;
        this.problemFactory = new ProblemFactory(cube);
        cube.setCache(new HashMap<>(10));
        List<Node> topNode = cube.getDimensions().stream().map(dimension -> dimension.getNodes()[0][0]).toList();
        addToCacheIfComplexityHihgerThan(topNode, 100000);
    }

    private void addToCacheIfComplexityHihgerThan(List<Node> baseVector, int complexity) {
        Problem problem = problemFactory.createProblem(baseVector);
        int affectedRows = problem.getNumberOfAffectedRows();
        System.out.println("Checking complexity of " + affectedRows + ": " + baseVector);
        if (affectedRows > complexity) {
            System.out.println("Adding to cache with complexity of " + affectedRows + ": " + baseVector);
            DataRetriever retriever = new DataRetriever();
            retriever.addProblem(problem);
            List<Future<ResultElement>> futures = retriever.computeAll();
            List<ResultElement> tempResult = extractResults(futures);
            cube.getCache().put(baseVector, tempResult.get(0).measureValues());
            getChildQueries(baseVector).forEach(childQuery -> addToCacheIfComplexityHihgerThan(childQuery, complexity));
        }
    }

    static List<ResultElement> extractResults(List<Future<ResultElement>> futures) {
        List<ResultElement> resultElements = new ArrayList<>(futures.size());
        for (Future<ResultElement> future : futures) {
            try {
                ResultElement resultElement = future.get();
                resultElements.add(resultElement);
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(CacheCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resultElements;
    }


    private List<List<Node>> getChildQueries(List<Node> baseVector) {
        int dimSize = baseVector.size();
        List<List<Node>> childrenList = new ArrayList<>(10);
        for (int dimToDrillIn = 0; dimToDrillIn < dimSize; dimToDrillIn++) {
            List<Node> childrenInDrillCoordinate = List.of(cube.getDimensions().get(dimToDrillIn).getChildrenOf(baseVector.get(dimToDrillIn)));
            for (Node childInDrillCoordinate : childrenInDrillCoordinate) {
                List<Node> child = new ArrayList<>(dimSize);
                for (int i = 0; i < dimSize; i++) {
                    if (i == dimToDrillIn) {
                        child.add(childInDrillCoordinate);
                    } else {
                        child.add(baseVector.get(i));
                    }
                }
                childrenList.add(child);
            }
        }
        return childrenList;
    }

}
