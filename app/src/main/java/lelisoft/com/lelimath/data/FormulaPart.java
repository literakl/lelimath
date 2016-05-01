package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * Enum identifying individual parts of formula.
 * Created by Leoš on 4. 2. 2015.
 */
public enum FormulaPart {
    FIRST_OPERAND("FO"), SECOND_OPERAND("SO"), RESULT("RS"), OPERATOR("OP"), EXPRESSION("EX");
    @DatabaseField(id = true)
    String key;

    FormulaPart(String key) {
        this.key = key;
    }

    public static FormulaPart getValue(String s) {
        switch (s) {
            case "FO":
                return FIRST_OPERAND;
            case "SO":
                return SECOND_OPERAND;
            case "RS":
                return RESULT;
            case "OP":
                return OPERATOR;
            case "EX":
                return EXPRESSION;
            default:
                throw new IllegalArgumentException("Unknown FormulaPart '" + s + "'!");
        }
    }
}
