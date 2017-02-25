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
    /** Allowed expressions and their definition. If unset a demo PLUS 0-9 will be used */
    private List<Expression> expressions;
    /** allowed formula's unknowns. If unset the RESULT will be used */
    private List<FormulaPart> unknowns;
    /** list of allowed games */
    private List<Game> games;
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

    @SuppressWarnings("unused")
    public List<Expression> getExpressions() {
        return expressions;
    }

    @SuppressWarnings("unused")
    public FormulaDefinition addExpression(Expression expression) {
        if (expressions == null) {
            expressions = Collections.singletonList(expression);
            return this;
        }
        if (expressions.size() == 1) {
            List<Expression> list = new ArrayList<>(3);
            list.add(expressions.get(0));
            list.add(expression);
            expressions = list;
            return this;
        }
        expressions.add(expression);
        return this;
    }

    @SuppressWarnings("unused")
    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
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

    public List<Game> getGames() {
        return games;
    }

    public void addGame(Game value) {
        if (games == null) {
            games = new ArrayList<>(2);
        }
        games.add(value);
    }

    @SuppressWarnings("unused")
    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "FormulaDefinition{" +
                "games=" + games +
                ", unknowns=" + unknowns +
                ", count=" + count +
                ", expressions=" + expressions +
                ", order=" + order +
                ", sequence=" + sequence +
                '}';
    }
}
