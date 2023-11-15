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

    public static double calculate(String[] input, double[] measures) {
        String[] formula = replaceIndexToValue(input, measures);
        return handleCalculation(formula);
    }

    public static boolean isOperator(String s) {
        return switch (s) {
            case ADD, SUB, MUL, DIV, SQU, ABS, IS_ZERO -> true;
            default -> false;
        };
    }

    private static String[] replaceIndexToValue(String[] inputs, double[] measures) {
        String[] outputs = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].equals(ADD) 
                    || inputs[i].equals(SUB) 
                    || inputs[i].equals(MUL) 
                    || inputs[i].equals(DIV) 
                    || inputs[i].equals(SQU) 
                    || inputs[i].equals(ABS)) {
                outputs[i] = inputs[i];
            } else if (inputs[i].contains(".")) {
                double d = Double.parseDouble(inputs[i]);
                outputs[i] = Double.toString(d);

            } else {
                int idx = Integer.parseInt(inputs[i]);
                double d = measures[idx];
                outputs[i] = Double.toString(d);
            }
        }
        return outputs;
    }

    private static double handleCalculation(String[] el) {
        double operand1, operand2, operand3;
        Stack<Double> stack = new Stack<>();
        for (String s : el) {
            switch (s) {
                case ADD:  {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = operand1 + operand2;
                    stack.push(local);
                    break;
                }
                case SUB: {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = operand1 - operand2;
                    stack.push(local);
                    break;
                }
                case MUL: {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = operand1 * operand2;
                    stack.push(local);
                    break;
                }
                case DIV:  {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local;
                    if (operand2 == 0) {
                        local = 0;
                    } else {
                        local = operand1 / operand2;
                    }
                    stack.push(local);
                    break;
                }
                case SQU: {
                    operand1 = stack.pop();
                    double local = operand1 * operand1;
                    stack.push(local);
                    break;
                }
                case ABS:  {
                    operand1 = stack.pop();
                    double local;
                    if (operand1 < 0) {
                        local = operand1 * -1;
                    } else {
                        local = operand1;
                    }
                    stack.push(local);
                    break;
                }
                case IS_ZERO:
                    operand3 = stack.pop();
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = (operand1 == 0) ? operand2 : operand3;
                    stack.push(local);
                    break;
                default:
                    stack.push(Double.parseDouble(s));
                    break;
            }
        }

        return stack.pop();
    }
}
