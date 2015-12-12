package lelisoft.com.lelimath.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A definition to generate one test.
 * Created by leos.literak on 26.2.2015.
 */
public class FormulaDefinition {
    /** Primary key that shall never change */
    String id;
    /** Name of this test, it shall be short */
    String title;
    /** Description of this test */
    String description;
    /** Number of questions */
    int count;
    /** Count of correct answers to succeed */
    int minimumCorrectAnswers;
    /** Maximum allowed time, in seconds */
    int testTimeLimit;
    /** Maximum time to receive speed badge, in seconds */
    int badgeTimeLimit;
    /** allowed values for formula's left operand */
    Values leftOperand;
    /** allowed values for formula's right operand */
    Values rightOperand;
    /** allowed values for difference between formula's operands */
    Values operandDifference;
    /** allowed values for formula's result */
    Values result;
    /** Allowed formula's operators. If unset PLUS will be used */
    List<Operator> operators;
    /** allowed formula's unknowns. If unset RESULT will be used */
    List<FormulaPart> unknowns;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMinimumCorrectAnswers() {
        return minimumCorrectAnswers;
    }

    public void setMinimumCorrectAnswers(int minimumCorrectAnswers) {
        this.minimumCorrectAnswers = minimumCorrectAnswers;
    }

    public int getTestTimeLimit() {
        return testTimeLimit;
    }

    public void setTestTimeLimit(int testTimeLimit) {
        this.testTimeLimit = testTimeLimit;
    }

    public int getBadgeTimeLimit() {
        return badgeTimeLimit;
    }

    public void setBadgeTimeLimit(int badgeTimeLimit) {
        this.badgeTimeLimit = badgeTimeLimit;
    }

    public Values getFirstOperand() {
        return leftOperand;
    }

    public void setLeftOperand(Values leftOperand) {
        this.leftOperand = leftOperand;
    }

    public Values getSecondOperand() {
        return rightOperand;
    }

    public void setRightOperand(Values rightOperand) {
        this.rightOperand = rightOperand;
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

    public void addOperator(Operator operator) {
        if (operators == null) {
            operators = Collections.singletonList(operator);
            return;
        }
        if (operators.size() == 1) {
            List<Operator> list = new ArrayList<Operator>(3);
            list.add(operators.get(0));
            list.add(operator);
            operators = list;
            return;
        }
        operators.add(operator);
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public List<FormulaPart> getUnknowns() {
        return unknowns;
    }

    public void addUnknown(FormulaPart unknown) {
        if (unknowns == null) {
            unknowns = Collections.singletonList(unknown);
            return;
        }
        if (unknowns.size() == 1) {
            List<FormulaPart> list = new ArrayList<FormulaPart>(3);
            list.add(unknowns.get(0));
            list.add(unknown);
            unknowns = list;
            return;
        }
        unknowns.add(unknown);
    }

    public void setUnknowns(List<FormulaPart> unknowns) {
        this.unknowns = unknowns;
    }

    /**
     * Calculates number of characters neccessary for most long equation.
     * @return maximum length
     */
    public int getFormulaMaximumLength() {
        int length = 6;
        length += getValuesMaximumLength(leftOperand);
        length += getValuesMaximumLength(rightOperand);
        length += getValuesMaximumLength(result);
        return length;
    }

    public int getValuesMaximumLength(Values values) {
        if (values == null) {
            return 0;
        }

        if (values.listing != null && values.listing.size() > 0) {
            int max = 0, size;
            for (Integer number : values.listing) {
                size = getNumberLength(number);
                if (size > max) {
                    max = size;
                }
            }
            return max;
        } else {
            int a = getNumberLength(values.minValue);
            int b = getNumberLength(values.maxValue);
            return Math.max(a, b);
        }
    }

    private int getNumberLength(int number) {
        if (number == 0) {
            return 1;
        }
        int size = 0;
        if (number < 0) {
            size += 1;
            number *= -1;
        }
        size += (int)(Math.log10(number) + 1);
        return size;
    }

    public String toString() {
        return "FormulaDefinition{" +
                "unknowns=" + unknowns +
                ", operators=" + operators +
                ", result=" + result +
                ", operandDifference=" + operandDifference +
                ", rightOperand=" + rightOperand +
                ", leftOperand=" + leftOperand +
                '}';
    }
}
