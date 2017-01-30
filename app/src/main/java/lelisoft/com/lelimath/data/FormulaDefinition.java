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
    /** Number of questions */
    private int count;
    /** Allowed formula's operators and their definition. If unset a demo PLUS 0-9 will be used */
    private List<OperatorDefinition> operatorDefinitions;
    /** allowed formula's unknowns. If unset the RESULT will be used */
    private List<FormulaPart> unknowns;
    /** order for values from OperatorDefinition defined in *sequence* */
    private SequenceOrder order;
    /** formula part which will be used as sequence. Null when order is Random */
    private FormulaPart sequence;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
            List<FormulaPart> list = new ArrayList<>(3);
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

    public SequenceOrder getOrder() {
        return order;
    }

    public void setOrder(SequenceOrder order) {
        this.order = order;
    }

    public FormulaPart getSequence() {
        return sequence;
    }

    public void setSequence(FormulaPart sequence) {
        this.sequence = sequence;
    }

    public String toString() {
        return "FormulaDefinition{" +
                "unknowns=" + unknowns +
                ", operators=" + operatorDefinitions +
                "}";
    }
}
