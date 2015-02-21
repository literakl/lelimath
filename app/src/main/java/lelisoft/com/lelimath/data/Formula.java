package lelisoft.com.lelimath.data;

import java.math.BigDecimal;

/**
 * Created by Leoš on 4. 2. 2015.
 */
public class Formula {
    BigDecimal firstOperand, secondOperand, result;
    Operator operator;
    Unknown unknown;
    StringBuilder sb = new StringBuilder(5);
    boolean decimalPointSet;

    public Formula(BigDecimal firstOperand, BigDecimal secondOperand, BigDecimal result, Operator operator, Unknown unknown) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.result = result;
        this.operator = operator;
        this.unknown = unknown;
    }

    public Formula() {
    }

    public void append(char entry) {
        if (entry == ',') { // TODO localization
            if (decimalPointSet) {
                return;
            }
            decimalPointSet = true;
        }
        sb.append(entry);
    }

    public void removeLastChar() {
        int length = sb.length();
        if (length > 0) {
            if (sb.charAt(length - 1) == ',') { // TODO localization
                decimalPointSet = false;
            }
            sb.setLength(length - 1);
        }
    }

    public String getEntry() {
        return sb.toString();
    }

    public String solve() {
        switch (unknown){
            case OPERATOR: {
                if (result.equals(firstOperand.add(secondOperand))) {
                    return Operator.PLUS.toString();
                }
                if (result.equals(firstOperand.divide(secondOperand))) {
                    return Operator.DIVIDE.toString();
                }
                if (result.equals(firstOperand.multiply(secondOperand))) {
                    return Operator.MULTIPLY.toString();
                }
                if (result.equals(firstOperand.subtract(secondOperand))) {
                    return Operator.MINUS.toString();
                }
            }
            case RESULT:
                return evaluate(firstOperand, operator, secondOperand).toString();
            case FIRST_OPERAND:
                return evaluate(result, operator.negate(), secondOperand).toString();
            case SECOND_OPERAND:
                return evaluate(result, operator.negate(), firstOperand).toString();
        }
        return "";
    }

    private BigDecimal evaluate(BigDecimal first, Operator operator, BigDecimal second) {
        switch (operator) {
            case PLUS:
                return first.add(second);
            case MINUS:
                return first.subtract(second);
            case MULTIPLY:
                return first.multiply(second);
            case DIVIDE:
                return first.divide(second);
        }
        return null;
    }

    public BigDecimal getFirstOperand() {
        return firstOperand;
    }

    public void setFirstOperand(BigDecimal firstOperand) {
        this.firstOperand = firstOperand;
    }

    public BigDecimal getSecondOperand() {
        return secondOperand;
    }

    public void setSecondOperand(BigDecimal secondOperand) {
        this.secondOperand = secondOperand;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Unknown getUnknown() {
        return unknown;
    }

    public void setUnknown(Unknown unknown) {
        this.unknown = unknown;
    }
}
