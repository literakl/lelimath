package lelisoft.com.lelimath.data;

import java.util.StringTokenizer;

import lelisoft.com.lelimath.helpers.Misc;

/**
 * Expression where all Values hold single number.
 * Created by Leoš on 05.03.2017.
 */

public class FixedExpression extends Expression {

    @SuppressWarnings("WeakerAccess")
    public FixedExpression(int firstOperand, Operator operator, int secondOperand) {
        this.firstOperand = Values.fromList(firstOperand);
        this.operator1 = operator;
        this.secondOperand = Values.fromList(secondOperand);
        this.result = Values.INFINITE;
    }

    @SuppressWarnings("WeakerAccess")
    public FixedExpression(int firstOperand, Operator operator1, int secondOperand, Operator operator2, int thirdOperand) {
        this.firstOperand = Values.fromList(firstOperand);
        this.operator1 = operator1;
        this.secondOperand = Values.fromList(secondOperand);
        this.operator2 = operator2;
        this.thirdOperand = Values.fromList(thirdOperand);
        this.result = Values.INFINITE;
    }

    public static FixedExpression parse(CharSequence sequence) {
        StringTokenizer stk = new StringTokenizer(sequence.toString(), "+-*/", true);
        int tokens = stk.countTokens();
        if (tokens != 3 && tokens != 5) {
            throw new IllegalArgumentException("Failed to parse '" + sequence + "' as expression");
        }

        int first = Misc.parseNumber(stk.nextToken());
        Operator operator1 = Operator.getValue(stk.nextToken());
        int second = Misc.parseNumber(stk.nextToken());

        if (tokens == 3) {
            return new FixedExpression(first, operator1, second);
        }

        Operator operator2 = Operator.getValue(stk.nextToken());
        int third = Misc.parseNumber(stk.nextToken());
        return new FixedExpression(first, operator1, second, operator2, third);
    }
}
