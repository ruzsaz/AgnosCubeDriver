package hu.agnos.cube.driver.service;

import java.util.List;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Node;

/**
 * Factory to create Problems to answer a single measure-array retrieval from the cube. The created problem can be
 * either a SumProblem, where data values are determined from the leaf-level nodes by summary, or a
 * CountDistinctProblem, where the single indicator is the count distinct result of the last dimension values.
 */
public final class ProblemFactory {

    private final Cube cube;
    private final boolean isCountDistinctType;

    /**
     * Creates a factory that can create Problems.
     *
     * @param cube Base cube
     */
    public ProblemFactory(Cube cube) {
        this.cube = cube;
        this.isCountDistinctType = cube.getMeasures().get(0).isVirtual();
    }

    /**
     * Creates a problem to answer a single measure-array retrieval from the cube. The created problem can be either a
     * SumProblem, where data values are determined from the leaf-level nodes by summary, or a CountDistinctProblem,
     * where the single indicator is the count distinct result of the last dimension values.
     *
     * @param drillVectorId Unique id of the drill request, used to match the answer to the right question
     * @param baseVector Coordinate values to retrieve data from
     * @return The solvable problem, either a SumProblem or a CountDistinctProblem
     */
    public Problem createProblem(int drillVectorId, List<Node> baseVector, int version) {
        if (isCountDistinctType && version == 2) {
            return new CountDistinctProblem2(cube, drillVectorId, baseVector);
        }
        if (isCountDistinctType && version == 3) {
            return new CountDistinctProblem2(cube, drillVectorId, baseVector);
        }
        if (isCountDistinctType && version == 1) {
            return new CountDistinctProblem(cube, drillVectorId, baseVector);
        } else {
            return new SumProblem(cube, drillVectorId, baseVector);
        }
    }

}
