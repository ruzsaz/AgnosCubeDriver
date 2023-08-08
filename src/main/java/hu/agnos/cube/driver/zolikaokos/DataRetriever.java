/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver.zolikaokos;

import hu.agnos.molap.Cube;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ruzsaz
 */
public class DataRetriever {

    private List<Problem> problems;
//    private double[][] facts;
    private Cube cube;
    private List<Callable<ResultElement>> tasks;
    private final int numberOfProcessors;

    public DataRetriever(Cube cube) {
//        this.facts = facts;
        this.cube = cube;
        this.problems = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.numberOfProcessors = Runtime.getRuntime().availableProcessors();
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        for (Problem p : problems) {
            this.addProblem(p);
        }
    }

    public void addProblem(Problem problem) {
        Callable<ResultElement> c = new Callable<ResultElement>() {
            @Override
            public ResultElement call() throws Exception {
//                Runtime.getRuntime().gc();
//                System.out.println("Free after runs: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " Mbyte");
                return problem.compute(cube);
                
            }
        };
        this.tasks.add(c);
        this.problems.add(problem);
    }

   
    public List<Future<ResultElement>> computeAll() {
        
//        System.out.println("Itt vagyok meghívva");
//        
//        System.out.println("Problems: " + this.problems.size());
//        for(Problem p : this.problems){
//            System.out.println(p.toString());
//        }
        
        //ExecutorService exec = Executors.newCachedThreadPool();
        // some other exectuors you could try to see the different behaviours
        ExecutorService exec = Executors.newFixedThreadPool(numberOfProcessors);
        //ExecutorService exec = Executors.newFixedThreadPool(4);
        //ExecutorService exec = Executors.newSingleThreadExecutor();
        List<Future<ResultElement>> results = null;
        try {

//            long begin = System.nanoTime();
            results = exec.invokeAll(tasks);

//            long duration = (System.nanoTime() - begin) / 1000000;
//            System.out.println(String.format("Elapsed time: %d ms", duration) + "  " + results.size());

        } catch (InterruptedException ex) {
            Logger.getLogger(DataRetriever.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            exec.shutdown();
//          System.out.println("Free after runs: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " Mbyte");
            
        }
        return results;
    }

}
