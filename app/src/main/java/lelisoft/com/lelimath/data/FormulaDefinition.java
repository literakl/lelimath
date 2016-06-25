package lelisoft.com.lelimath.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A definition to generate one test.
 * Created by leos.literak on 26.2.2015.
 */
public class FormulaDefinition implements Serializable {
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
    /** Allowed formula's operators and their definition. If unset demo PLUS 0-9 will be used */
    List<OperatorDefinition> operatorDefinitions;
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

    public void setOperatorDefinitions(List<OperatorDefinition> operatorDefinitions) {
        this.operatorDefinitions = operatorDefinitions;
    }

    public FormulaDefinition addOperator(OperatorDefinition operator) {
        if (operatorDefinitions == null) {
            operatorDefinitions = Collections.singletonList(operator);
            return this;
        }
        if (operatorDefinitions.size() == 1) {
            List<OperatorDefinition> list = new ArrayList<>(3);
            list.add(operatorDefinitions.get(0));
            list.add(operator);
            operatorDefinitions = list;
            return this;
        }
        operatorDefinitions.add(operator);
        return this;
    }

    public List<OperatorDefinition> getOperatorDefinitions() {
        return operatorDefinitions;
    }

    public List<FormulaPart> getUnknowns() {
        return unknowns;
    }

    public FormulaDefinition addUnknown(FormulaPart unknown) {
        if (unknowns == null) {
            unknowns = Collections.singletonList(unknown);
            return this;
        }
        if (unknowns.size() == 1) {
            List<FormulaPart> list = new ArrayList<FormulaPart>(3);
            list.add(unknowns.get(0));
            list.add(unknown);
            unknowns = list;
            return this;
        }
        unknowns.add(unknown);
        return this;
    }

    public void setUnknowns(List<FormulaPart> unknowns) {
        this.unknowns = unknowns;
    }

    /**
     * Calculates number of characters neccessary for most long equation.
     * @return maximum length
     */
    @SuppressWarnings("ConstantConditions")
    public int getFormulaMaximumLength() {
        int length = 6, i, j, k;
        int maxFirst = 2, maxSecond = 2, maxResult = 2;
        for (OperatorDefinition definition : operatorDefinitions) {
            i = definition.firstOperand.getMaximumLength();
            j = definition.secondOperand.getMaximumLength();
            k = definition.result.getMaximumLength();

            if (i == 0) {
                i = Math.max(j, k);
            }
            if (j == 0) {
                j = Math.max(i, k);
            }
            if (k == 0) {
                switch (definition.getOperator()) {
                    case MULTIPLY: k = i + j; break;
                    case PLUS: k = Math.max(i, j) + 1; break;
                    default: k = Math.max(i, j);
                }
            }

            if (i > maxFirst) {
                maxFirst = i;
            }
            if (j > maxSecond) {
                maxSecond = j;
            }
            if (k > maxResult) {
                maxResult = k;
            }
        }
        length += maxFirst + maxSecond + maxResult;
        return length;
    }

    public String toString() {
        return "FormulaDefinition{" +
                "unknowns=" + unknowns +
                ", operators=" + operatorDefinitions +
                "}";
    }
}
