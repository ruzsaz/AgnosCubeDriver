package hu.agnos.cube.driver.util;

import java.util.Stack;

/**
 * @author parisek
 */
public class PostfixCalculator {

    private static final String ADD = "+";
    private static final String SUB = "-";
    private static final String MUL = "*";
    private static final String DIV = "/";
    private static final String SQU = "^";
    private static final String ABS = "|";
    private static final String IS_ZERO = "?";

    public static double calculate(String[] input, double[] measures) {
        String[] formula = PostfixCalculator.replaceIndexToValue(input, measures);
        return PostfixCalculator.handleCalculation(formula);
    }

    private static String[] replaceIndexToValue(String[] inputs, double[] measures) {
        int inputsLength = inputs.length;
        String[] outputs = new String[inputsLength];
        for (int i = 0; i < inputsLength; i++) {
            if (inputs[i].equals(PostfixCalculator.ADD)
                    || inputs[i].equals(PostfixCalculator.SUB)
                    || inputs[i].equals(PostfixCalculator.MUL)
                    || inputs[i].equals(PostfixCalculator.DIV)
                    || inputs[i].equals(PostfixCalculator.SQU)
                    || inputs[i].equals(PostfixCalculator.ABS)) {
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
                case PostfixCalculator.ADD: {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = operand1 + operand2;
                    stack.push(local);
                    break;
                }
                case PostfixCalculator.SUB: {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = operand1 - operand2;
                    stack.push(local);
                    break;
                }
                case PostfixCalculator.MUL: {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                    double local = operand1 * operand2;
                    stack.push(local);
                    break;
                }
                case PostfixCalculator.DIV: {
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
                case PostfixCalculator.SQU: {
                    operand1 = stack.pop();
                    double local = operand1 * operand1;
                    stack.push(local);
                    break;
                }
                case PostfixCalculator.ABS: {
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
                case PostfixCalculator.IS_ZERO:
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

    public static boolean isOperator(String s) {
        return switch (s) {
            case ADD, SUB, MUL, DIV, SQU, ABS, IS_ZERO -> true;
            default -> false;
        };
    }
}
