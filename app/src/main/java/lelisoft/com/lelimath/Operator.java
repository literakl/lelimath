package lelisoft.com.lelimath;

/**
 * Created by Leoš on 4. 2. 2015.
 */
public enum Operator {
    String value;

    PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/");

    @Override
    public String toString() {
        return value;
    }

}
