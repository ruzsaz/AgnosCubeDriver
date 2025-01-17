package hu.agnos.cube.driver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 *
 * @author ruzsaz
 */
public class DataRetriever {

    private final List<Callable<ResultElement>> tasks;
    private final int numberOfProcessors;
    private final ExecutorService exec;

    public DataRetriever() {
        this.tasks = new ArrayList<>();
        this.numberOfProcessors = Runtime.getRuntime().availableProcessors();
        this.exec = Executors.newFixedThreadPool(numberOfProcessors);
    }

    public void addProblem(Problem problem) {
        tasks.add(problem::compute);
    }

    public List<Future<ResultElement>> computeAll() {
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
