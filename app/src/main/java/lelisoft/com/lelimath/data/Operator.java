package lelisoft.com.lelimath.data;

/**
 * Operators
 * Created by Leoš on 4. 2. 2015.
 */
public enum Operator {

    PLUS("+"), MINUS("-"), MULTIPLY("\u22C5"), DIVIDE(":");

    public final String value;

    Operator(String value) {
        this.value = value;
    }

    public Operator negate() {
        switch (this) {
            case PLUS:
                return MINUS;
            case MINUS:
                return PLUS;
            case MULTIPLY:
                return DIVIDE;
            default: //case DIVIDE:
                return MULTIPLY;
        }
    }

    public boolean equals(String another) {
        return value.equals(another);
    }

    public static Operator getValue(String s) {
        switch (s) {
            case "+":
                return PLUS;
            case "-":
                return MINUS;
            case "*":
            case "\u22C5":
                return MULTIPLY;
            case "/":
            case ":":
                return DIVIDE;
            default:
                throw new IllegalArgumentException("Unknown Operator '" + s + "'!");
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
