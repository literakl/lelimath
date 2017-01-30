package lelisoft.com.lelimath.data;

import java.io.Serializable;

/**
 * Argument and result definition for some operation.
 * Created by Leoš on 23.04.2016.
 */
public class OperatorDefinition implements Serializable {
    /** the Operator */
    private Operator operator;
    /** allowed values for operator's left operand */
    private Values firstOperand;
    /** allowed values for operator's right operand */
    private Values secondOperand;
    /** allowed values for difference between operators's operands */
    private Values operandDifference;
    /** allowed values for operator's result */
    private Values result;

    public OperatorDefinition() {
    }

    public OperatorDefinition(Operator operator) {
        this.operator = operator;
    }

    public OperatorDefinition(Operator operator, Values firstOperand, Values secondOperand, Values result) {
        this.operator = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.result = result;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Values getFirstOperand() {
        return firstOperand;
    }

    public void setFirstOperand(Values firstOperand) {
        this.firstOperand = firstOperand;
    }

    public Values getSecondOperand() {
        return secondOperand;
    }

    public void setSecondOperand(Values secondOperand) {
        this.secondOperand = secondOperand;
    }

    @SuppressWarnings("unused")
    public Values getOperandDifference() {
        return operandDifference;
    }

    @SuppressWarnings("unused")
    public void setOperandDifference(Values operandDifference) {
        this.operandDifference = operandDifference;
    }

    public Values getResult() {
        return result;
    }

    public void setResult(Values result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "OperatorDefinition{" +
                "operator=" + operator +
                ", firstOperand=" + firstOperand +
                ", secondOperand=" + secondOperand +
                ", result=" + result +
                '}';
    }
}
