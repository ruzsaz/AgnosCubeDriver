/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver;

import hu.agnos.cube.driver.zolikaokos.ResultElement;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author parisek
 */

@Getter
@Setter
public class ResultSet implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = -8940196742313994740L;
    private final String name;
    private List<String> originalName;
    private List<ResultElement> response;
    private String cubeName;
    private List<String> measures;

    public ResultSet(String name) {
        this.name = name;
        this.originalName = new ArrayList<>();
        this.response = new ArrayList<>();
        this.cubeName = "";
        this.measures = new ArrayList<>();
    }

    public ResultSet(String cubeName, List<String> measures, String name) {
        this.name = name;
        this.originalName = new ArrayList<>();
        this.response = new ArrayList<>();
        this.cubeName = cubeName;
        this.measures = measures;
    }


//    public void addResponse(ResultElement result) {
//        this.response.add(result);
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResultSet{"
                + "originalName=" + String.join(",", originalName)
                + ", name=" + name
                + ", cubeName=" + cubeName
                + ", measures=" + String.join(",", measures)
                + ", response:");

        for (ResultElement e : this.response) {
            sb.append("\n\t").append(e.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

//    public ResultSet deepCopy() {
//        ResultSet result = new ResultSet(new String(name));
//        for (ResultElement r : this.response) {
//            result.addResponse(r);
//        }
//        return result;
//    }

}
