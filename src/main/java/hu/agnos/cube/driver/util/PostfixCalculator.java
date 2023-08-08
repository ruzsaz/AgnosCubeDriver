/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver.util;

import java.util.Stack;

/**
 *
 * @author parisek
 */
public class PostfixCalculator {

    public static final String ADD = "+";
    public static final String SUB = "-";
    public static final String MUL = "*";
    public static final String DIV = "/";
    public static final String SQU = "^";
    public static final String ABS = "|";
    public static final String IS_ZERO = "?";

    public double calculate(String[] input, double[] measures) {
        String[] formula = replaceIndexToValue(input, measures);
        return handleCalculation(formula);
    }

    public boolean isOperator(String s) {
        boolean result = false;
        switch (s) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case SQU:
            case ABS:
            case IS_ZERO:
                result = true;
                break;
        }
        return result;
    }

    private String[] replaceIndexToValue(String[] inputs, double[] measures) {
        String[] outputs = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].equals(ADD) || inputs[i].equals(SUB) || inputs[i].equals(MUL) || inputs[i].equals(DIV) || inputs[i].equals(SQU) || inputs[i].equals(ABS)) {
                outputs[i] = inputs[i];
            } else if (inputs[i].contains(".")) {
                Double d = Double.parseDouble(inputs[i]);
                outputs[i] = d.toString();

            } else {
                int idx = Integer.parseInt(inputs[i]);
                Double d = measures[idx];
                outputs[i] = d.toString();
            }
        }
        return outputs;
    }

    private double handleCalculation(String[] el) {
        double operand1, operand2, operand3;
        Stack<Double> stack = new Stack();
        for (int i = 0; i < el.length; i++) {
            if (el[i].equals(ADD) || el[i].equals(SUB) || el[i].equals(MUL) || el[i].equals(DIV)) {
                operand2 = stack.pop();
                operand1 = stack.pop();
                switch (el[i]) {
                    case ADD: {
                        double local = operand1 + operand2;
                        stack.push(local);
                        break;
                    }

                    case SUB: {
                        double local = operand1 - operand2;
                        stack.push(local);
                        break;
                    }

                    case MUL: {
                        double local = operand1 * operand2;
                        stack.push(local);
                        break;
                    }

                    case DIV: {
                        double local;
                        if (operand2 == 0) {
                            local = 0;
                        } else {
                            local = operand1 / operand2;
                        }
                        stack.push(local);
                        break;
                    }
                }
            } else if (el[i].equals(SQU) || el[i].equals(ABS)) {
                operand1 = stack.pop();
                switch (el[i]) {
                    case SQU: {
                        double local = operand1 * operand1;
                        stack.push(local);
                        break;
                    }
                    case ABS: {
                        double local;
                        if (operand1 < 0) {
                            local = operand1 * -1;
                        } else {
                            local = operand1;
                        }
                        stack.push(local);
                        break;
                    }
                }

            } else if (el[i].equals(IS_ZERO)) {
                operand3 = stack.pop();
                operand2 = stack.pop();
                operand1 = stack.pop();
                switch (el[i]) {
                    case IS_ZERO: {

                        double local = (operand1 == 0) ? operand2 : operand3;
                        stack.push(local);
                        break;
                    }
                }
            } else {
                stack.push(Double.parseDouble(el[i]));
            }
        }

        return stack.pop();
    }
}
