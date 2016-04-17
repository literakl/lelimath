package lelisoft.com.lelimath.data;

/**
 * Created by Leoš on 4. 2. 2015.
 */
public enum Operator {

    PLUS("+"), MINUS("-"), MULTIPLY("\u22C5"), DIVIDE(":");

    String value;

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

    @Override
    public String toString() {
        return value;
    }

}
