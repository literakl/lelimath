package lelisoft.com.lelimath.data;

import java.io.Serializable;

/**
 * Expression with up to three arguments and result.
 * Created by Leoš on 23.04.2016.
 */
public class Expression implements Serializable {
    /** allowed values for operator's first operand */
    private Values firstOperand;
    /** first operator */
    private Operator operator1;
    /** allowed values for operator's second operand */
    private Values secondOperand;
    /** second operator */
    private Operator operator2;
    /** allowed values for operator's third operand */
    private Values thirdOperand;
    /** allowed values for operator's result */
    private Values result;

    @SuppressWarnings("unused")
    public Expression() {
    }

    @SuppressWarnings("unused")
    public Expression(Values firstOperand, Operator operator, Values secondOperand, Values result) {
        this.operator1 = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.result = result;
    }

    public Values getFirstOperand() {
        return firstOperand;
    }

    @SuppressWarnings("unused")
    public void setFirstOperand(Values firstOperand) {
        this.firstOperand = firstOperand;
    }

    @SuppressWarnings("unused")
    public Operator getOperator1() {
        return operator1;
    }

    @SuppressWarnings("unused")
    public void setOperator1(Operator operator) {
        this.operator1 = operator;
    }

    public Values getSecondOperand() {
        return secondOperand;
    }

    @SuppressWarnings("unused")
    public void setSecondOperand(Values secondOperand) {
        this.secondOperand = secondOperand;
    }

    @SuppressWarnings("unused")
    public Operator getOperator2() {
        return operator2;
    }

    @SuppressWarnings("unused")
    public void setOperator2(Operator operator) {
        this.operator2 = operator;
    }

    @SuppressWarnings("unused")
    public Values getThirdOperand() {
        return thirdOperand;
    }

    @SuppressWarnings("unused")
    public void setThirdOperand(Values thirdOperand) {
        this.thirdOperand = thirdOperand;
    }

    public Values getResult() {
        return result;
    }

    public void setResult(Values result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "first=" + firstOperand + operator1 +
                ", second=" + secondOperand + operator2 +
                ", third=" + thirdOperand +
                ", result=" + result +
                '}';
    }
}
