package lelisoft.com.lelimath.data;

import android.os.Parcel;
import android.os.Parcelable;

import lelisoft.com.lelimath.logic.Solver;

/**
 * Data holder for one equation
 * Created by Leoš on 4. 2. 2015.
 */
public class Formula implements Parcelable {
    Integer firstOperand, secondOperand, result;
    Operator operator;
    FormulaPart unknown;
    StringBuilder sb = new StringBuilder(5);

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
     * @return content that user typed
     */
    public String getUserEntry() {
        return sb.toString();
    }

    public void setUserEntry(CharSequence entry) {
        sb.setLength(0);
        sb.append(entry);
    }

    /**
     * @return value of unknown formula part
     */
    public String getUnknownValue() {
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
    public boolean isEntryCorrect() {
        return getUnknownValue().equals(getUserEntry());
    }

    public String solve() {
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

    public Integer getFirstOperand() {
        return firstOperand;
    }

    public void setFirstOperand(Integer firstOperand) {
        this.firstOperand = firstOperand;
    }

    public Integer getSecondOperand() {
        return secondOperand;
    }

    public void setSecondOperand(Integer secondOperand) {
        this.secondOperand = secondOperand;
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

    public FormulaPart getUnknown() {
        return unknown;
    }

    public void setUnknown(FormulaPart unknown) {
        this.unknown = unknown;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.firstOperand);
        dest.writeValue(this.secondOperand);
        dest.writeValue(this.result);
        dest.writeInt(this.operator == null ? -1 : this.operator.ordinal());
        dest.writeInt(this.unknown == null ? -1 : this.unknown.ordinal());
        dest.writeSerializable(this.sb);
    }

    private Formula(Parcel in) {
        this.firstOperand = (Integer) in.readValue(Integer.class.getClassLoader());
        this.secondOperand = (Integer) in.readValue(Integer.class.getClassLoader());
        this.result = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpOperator = in.readInt();
        this.operator = tmpOperator == -1 ? null : Operator.values()[tmpOperator];
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
