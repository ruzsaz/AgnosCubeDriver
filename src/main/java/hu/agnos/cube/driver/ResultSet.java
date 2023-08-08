/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver;

import hu.agnos.cube.driver.zolikaokos.ResultElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author parisek
 */
public class ResultSet implements java.io.Serializable {

    private static final long serialVersionUID = -8940196742313994740L;
    private final String name;
    private List<ResultElement> response;

    public ResultSet(String name) {
        this.name = name;
        this.response = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<ResultElement> getResponse() {
        return response;
    }

    public void addResponse(ResultElement result) {
        this.response.add(result);
    }

    public void setResponse(List<ResultElement> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResultSet{" + "name=" + name + ", response:");
        for (ResultElement e : this.response) {
            sb.append("\n\t").append(e.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

    public ResultSet deepCopy() {
        ResultSet result = new ResultSet(new String(name));
        for (ResultElement r : this.response) {
            result.addResponse(r);
        }
        return result;
    }

}
