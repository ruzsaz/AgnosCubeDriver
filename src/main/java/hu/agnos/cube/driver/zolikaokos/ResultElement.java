package hu.agnos.cube.driver.zolikaokos;

import hu.agnos.molap.dimension.DimValue;
import lombok.Getter;

/**
 * Ez az osztály az eredményhalmaz egy sorát reprzentálja
 *
 * @author ruzsaz
 */
@Getter
public class ResultElement {

    private final DimValue[] header;
    private final int drillVectorId;
    private final double[] measureValues;

    public ResultElement(DimValue[] header, double[] measureValues, int drillVectorId) {
        this.header = header;
        this.measureValues = measureValues;
        this.drillVectorId = drillVectorId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\tElement:\n\t");
        sb.append(printHeader()).append("\n\t");
        sb.append(printMeasures());
        return sb.toString();
    }

    public String printHeader() {
        StringBuilder sb = new StringBuilder("\tHeader: ");
        for (DimValue s : header) {
            sb.append(s.toString()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public String printMeasures() {
        StringBuilder sb = new StringBuilder("\tMeasures: ");
        for (double s : measureValues) {
            sb.append(s).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public ResultElement deepCopy() {
        DimValue[] tempHeader = new DimValue[header.length];
        System.arraycopy(header, 0, tempHeader, 0, header.length);

        double[] tempMeasureValues = new double[measureValues.length];
        System.arraycopy(measureValues, 0, tempMeasureValues, 0, measureValues.length);

        return new ResultElement(tempHeader, tempMeasureValues, drillVectorId);
    }

}
