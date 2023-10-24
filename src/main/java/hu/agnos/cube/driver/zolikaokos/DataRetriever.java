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

    private final List<Problem> problems;
    private final Cube cube;
    private final List<Callable<ResultElement>> tasks;
    private final int numberOfProcessors;

    public DataRetriever(Cube cube) {
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
        Callable<ResultElement> c = () -> problem.compute(cube);
        this.tasks.add(c);
        this.problems.add(problem);
    }

   
    public List<Future<ResultElement>> computeAll() {
        ExecutorService exec = Executors.newFixedThreadPool(numberOfProcessors);
        List<Future<ResultElement>> results = null;
        try {
            results = exec.invokeAll(tasks);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataRetriever.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            exec.shutdown();
        }
        return results;
    }

}
