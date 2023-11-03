package hu.agnos.cube.driver.zolikaokos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import hu.agnos.cube.meta.resultDto.ResultElement;
import lombok.Getter;

/**
 *
 * @author ruzsaz
 */
public class DataRetriever {

    @Getter
    private final List<Problem> problems;
    private final List<Callable<ResultElement>> tasks;
    private final int numberOfProcessors;

    public DataRetriever() {
        this.problems = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.numberOfProcessors = Runtime.getRuntime().availableProcessors();
    }

    public void setProblems(List<Problem> problems) {
        for (Problem p : problems) {
            this.addProblem(p);
        }
    }

    public void addProblem(Problem problem) {
        this.tasks.add(problem::compute);
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
