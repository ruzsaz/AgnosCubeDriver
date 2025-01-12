package hu.agnos.cube.driver.service;

import java.util.List;

import hu.agnos.cube.Cube;
import hu.agnos.cube.ClassicalCube;
import hu.agnos.cube.CountDistinctCube;
import hu.agnos.cube.CubeType;
import hu.agnos.cube.dimension.Node;

/**
 * Factory to create Problems to answer a single measure-array retrieval from the cube. The created problem can be
 * either a SumProblem, where data values are determined from the leaf-level nodes by summary, or a
 * CountDistinctProblem, where the single indicator is the count distinct result of the last dimension values.
 */
public final class ProblemFactory {

    private final Cube cube;
    
    /**
     * Creates a factory that can create Problems.
     *
     * @param cube Base cube
     */
    public ProblemFactory(Cube cube) {
        this.cube = cube;
    }

    /**
     * Creates a problem to answer a single measure-array retrieval from the cube. The created problem can be either a
     * SumProblem, where data values are determined from the leaf-level nodes by summary, or a CountDistinctProblem,
     * where the single indicator is the count distinct result of the last dimension values.
     *
     * @param baseVector Coordinate values to retrieve data from
     * @return The solvable problem, either a SumProblem or a CountDistinctProblem
     */
    public Problem createProblem(List<Node> baseVector) {
        if (cube.getType().equals(CubeType.COUNT_DISTINCT.getType())) {
            return new CountDistinctProblem((CountDistinctCube)cube, baseVector);
        } else {
            return new SumProblem((ClassicalCube)cube, baseVector);
        }
    }

}
