package lelisoft.com.lelimath.data;

/**
 * Argument and result definition for some operation.
 * Created by Leoš on 23.04.2016.
 */
public class OperatorDefinition {
    /** the Operator */
    Operator operator;
    /** allowed values for operator's left operand */
    Values firstOperand;
    /** allowed values for operator's right operand */
    Values secondOperand;
    /** allowed values for difference between operators's operands */
    Values operandDifference;
    /** allowed values for operator's result */
    Values result;

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

    public Values getOperandDifference() {
        return operandDifference;
    }

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
