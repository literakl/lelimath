package lelisoft.com.lelimath.data;

import com.google.gson.annotations.SerializedName;

/**
 * Enum identifying individual parts of formula.
 * Created by Leoš on 4. 2. 2015.
 */
public enum FormulaPart {
    @SerializedName("FO") FIRST_OPERAND("FO"),
    @SerializedName("SO") SECOND_OPERAND("SO"),
    @SerializedName("RS") RESULT("RS"),
    @SerializedName("OP") OPERATOR("OP"),
    @SerializedName("EX") EXPRESSION("EX");

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
