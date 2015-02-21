package lelisoft.com.lelimath;

import java.math.BigDecimal;

/**
 * Created by Leoš on 4. 2. 2015.
 */
public class FormulaVO {
    BigDecimal firstOperand, secondOperand, result;
    Operator operator;
    Unknown unknown;
    StringBuilder sb = new StringBuilder(5);

    public FormulaVO(BigDecimal firstOperand, BigDecimal secondOperand, BigDecimal result, Operator operator, Unknown unknown) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.result = result;
        this.operator = operator;
        this.unknown = unknown;
    }

    public FormulaVO() {
    }

    public void append(char entry) {
        sb.append(entry);
    }

    public void removeLastChar() {
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        }
    }

    public String evaluate() {
        if (unknown == Unknown.OPERATOR) {
            if (result.equals(firstOperand.add(secondOperand))) {
                return Operator.PLUS.toString();
            };
        } else {

        }
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
