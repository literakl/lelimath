package lelisoft.com.lelimath.data;

import android.os.Parcel;
import android.os.Parcelable;

import lelisoft.com.lelimath.logic.Solver;

/**
 * Data holder for one equation
 * Created by Leoš on 4. 2. 2015.
 */
public class Formula implements Parcelable {
    private Integer firstOperand, secondOperand, thirdOperand, result;
    private Operator operator, operator2;
    private FormulaPart unknown;
    private StringBuilder sb = new StringBuilder(5);

    public Formula(Integer firstOperand, Integer secondOperand, Integer result, Operator operator, FormulaPart unknown) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.result = result;
        this.operator = operator;
        this.unknown = unknown;
    }

    public Formula() {
    }

    /**
     * Add character that user typed
     * @param entry character
     */
    public void append(char entry) {
        sb.append(entry);
    }

    /**
     * Add character that user typed
     * @param entry character
     */
    public void append(CharSequence entry) {
        sb.append(entry);
    }

    /**
     * Remove the last character that was appended
     */
    public void undoAppend() {
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        }
    }

    /**
     * Clears all user input
     */
    public void clear() {
        sb.setLength(0);
    }

    /**
     * @return content that user typed
     */
    public String getUserInput() {
        return sb.toString();
    }

    public void setUserEntry(CharSequence entry) {
        sb.setLength(0);
        sb.append(entry);
    }

    /**
     * @return value of unknown formula part
     */
    private String getUnknownValue() {
        switch (unknown){
            case OPERATOR:
                return operator.toString();
            case RESULT:
                return result.toString();
            case FIRST_OPERAND:
                return firstOperand.toString();
            case SECOND_OPERAND:
                return secondOperand.toString();
        }
        return "";
    }

    /**
     * @return true if user solved the formula correctly
     */
    @SuppressWarnings("all")
    public boolean isEntryCorrect() {
        String userInput = sb.toString();
        if (secondOperand == 0) {
            if (unknown == FormulaPart.FIRST_OPERAND && result == 0) {
                return true; // x * 0 = 0
            }
            if (unknown == FormulaPart.OPERATOR) {
                if (Operator.PLUS.equals(userInput) || Operator.MINUS.equals(userInput)) {
                    return result.equals(firstOperand); // x + 0 = x  && y - 0 = y
                } else if (Operator.MULTIPLY.equals(userInput)) {
                    return result == 0; // x * 0 = 0
                } else {
                    return false; // x : 0 = UNDEF
                }
            }
        }

        if (secondOperand == 1) {
            if (unknown == FormulaPart.OPERATOR) {
                if (Operator.MULTIPLY.equals(userInput)|| Operator.DIVIDE.equals(userInput)) {
                    return result.equals(firstOperand); // x * 1 = x && x : 1 = x
                }
            }
        }

        return getUnknownValue().equals(userInput);
    }

    @SuppressWarnings("all")
    String solve() {
        switch (unknown){
            case OPERATOR: {
                if (result.equals(firstOperand + secondOperand)) {
                    return Operator.PLUS.toString();
                }
                if (result.equals(firstOperand / secondOperand)) {
                    return Operator.DIVIDE.toString();
                }
                if (result.equals(firstOperand * secondOperand)) {
                    return Operator.MULTIPLY.toString();
                }
                if (result.equals(firstOperand - secondOperand)) {
                    return Operator.MINUS.toString();
                }
            }
            case RESULT:
                return Solver.evaluate(firstOperand, operator, secondOperand).toString();
            case FIRST_OPERAND:
                return Solver.evaluate(result, operator.negate(), secondOperand).toString();
            case SECOND_OPERAND:
                return Solver.evaluate(result, operator.negate(), firstOperand).toString();
        }
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstOperand);
        if (unknown == FormulaPart.FIRST_OPERAND) sb.append("(?)");
        sb.append(" ").append(operator);
        if (unknown == FormulaPart.OPERATOR) sb.append("(?)");
        sb.append(" ");
        sb.append(secondOperand);
        if (unknown == FormulaPart.SECOND_OPERAND) sb.append("(?)");
        if (operator2 != null) {
            sb.append(" ").append(operator2);
            if (unknown == FormulaPart.OPERATOR2) sb.append("(?)");
            sb.append(" ");
            sb.append(thirdOperand);
            if (unknown == FormulaPart.THIRD_OPERAND) sb.append("(?)");
        }
        sb.append(" = ");
        sb.append(result);
        if (unknown == FormulaPart.RESULT) sb.append("(?)");
        return sb.toString();
    }

    public Integer getFirstOperand() {
        return firstOperand;
    }

    @SuppressWarnings("unused")
    public void setFirstOperand(Integer firstOperand) {
        this.firstOperand = firstOperand;
    }

    public Integer getSecondOperand() {
        return secondOperand;
    }

    @SuppressWarnings("unused")
    public void setSecondOperand(Integer secondOperand) {
        this.secondOperand = secondOperand;
    }

    @SuppressWarnings("unused")
    public Integer getThirdOperand() {
        return thirdOperand;
    }

    @SuppressWarnings("unused")
    public void setThirdOperand(Integer thirdOperand) {
        this.thirdOperand = thirdOperand;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @SuppressWarnings("unused")
    public Operator getOperator2() {
        return operator2;
    }

    @SuppressWarnings("unused")
    public void setOperator2(Operator operator2) {
        this.operator2 = operator2;
    }

    public FormulaPart getUnknown() {
        return unknown;
    }

    public void setUnknown(FormulaPart unknown) {
        this.unknown = unknown;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Formula formula = (Formula) o;
        if (!firstOperand.equals(formula.firstOperand)) return false;
        if (!secondOperand.equals(formula.secondOperand)) return false;
        if (!result.equals(formula.result)) return false;
        if (operator != formula.operator) return false;
        return unknown == formula.unknown;

    }

    @Override
    public int hashCode() {
        int result1 = firstOperand.hashCode();
        result1 = 31 * result1 + secondOperand.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + operator.hashCode();
        result1 = 31 * result1 + unknown.hashCode();
        return result1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.firstOperand);
        dest.writeValue(this.secondOperand);
        dest.writeValue(this.thirdOperand);
        dest.writeValue(this.result);
        dest.writeInt(this.operator == null ? -1 : this.operator.ordinal());
        dest.writeInt(this.operator2 == null ? -1 : this.operator2.ordinal());
        dest.writeInt(this.unknown == null ? -1 : this.unknown.ordinal());
        dest.writeSerializable(this.sb);
    }

    private Formula(Parcel in) {
        this.firstOperand = (Integer) in.readValue(Integer.class.getClassLoader());
        this.secondOperand = (Integer) in.readValue(Integer.class.getClassLoader());
        this.thirdOperand = (Integer) in.readValue(Integer.class.getClassLoader());
        this.result = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpOperator = in.readInt();
        this.operator = tmpOperator == -1 ? null : Operator.values()[tmpOperator];
        tmpOperator = in.readInt();
        this.operator2 = tmpOperator == -1 ? null : Operator.values()[tmpOperator];
        int tmpUnknown = in.readInt();
        this.unknown = tmpUnknown == -1 ? null : FormulaPart.values()[tmpUnknown];
        this.sb = (StringBuilder) in.readSerializable();
    }

    public static final Parcelable.Creator<Formula> CREATOR = new Parcelable.Creator<Formula>() {
        public Formula createFromParcel(Parcel source) {
            return new Formula(source);
        }

        public Formula[] newArray(int size) {
            return new Formula[size];
        }
    };
}
