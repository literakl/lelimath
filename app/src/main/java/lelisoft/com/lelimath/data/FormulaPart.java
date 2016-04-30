package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Enum identifying individual parts of formula.
 * Created by Leoš on 4. 2. 2015.
 */
//@DatabaseTable(tableName = "part")
public enum FormulaPart {
    FIRST_OPERAND, SECOND_OPERAND, RESULT, OPERATOR
/*
    FIRST_OPERAND("FO"), SECOND_OPERAND("SO"), RESULT("RS"), OPERATOR("OP"), EXPRESSION("EX");
    @DatabaseField(id = true)
    String key;

    FormulaPart(String key) {
        this.key = key;
    }
*/
}
