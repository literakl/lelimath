package lelisoft.com.lelimath.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Records formula solving activity into database
 * Created by Leoš on 29.04.2016.
 */
@DatabaseTable(tableName = "play_record")
public class PlayRecord {
    @DatabaseField(generatedId = true, columnName = Columns.ID)
    Integer id;

    @DatabaseField(canBeNull = false)
    private Date date;

    @DatabaseField(foreign = true, columnName=Columns.PLAY_ID)
    private Play play;

    @DatabaseField(columnName = Columns.FIRST_OPERAND)
    private Integer firstOperand;

    @DatabaseField(columnName = Columns.SECOND_OPERAND)
    private Integer secondOperand;

    @DatabaseField(columnName = Columns.THIRD_OPERAND)
    private Integer thirdOperand;

    @DatabaseField(columnName = Columns.RESULT)
    private Integer result;

    @DatabaseField(persisted = false)
    private Operator operator;

    @DatabaseField(columnName = Columns.OPERATOR, width = 1)
    private String operatorStr;

    @DatabaseField(persisted = false)
    private Operator operator2;

    @DatabaseField(columnName = Columns.OPERATOR2, width = 1)
    private String operator2Str;

    @DatabaseField(persisted = false)
    private FormulaPart unknown;

    @DatabaseField(columnName = Columns.UNKNOWN, width = 2)
    private String unknownStr;

    @DatabaseField(columnName = Columns.CORRECT)
    private boolean correct;

    @DatabaseField(columnName = Columns.WRONG_VALUE)
    private String wrongValue;

    @DatabaseField(columnName = Columns.SPENT)
    private Long timeSpent;

    @DatabaseField(canBeNull = false, defaultValue = "0", columnName = Columns.POINTS)
    private Integer points;

    public PlayRecord() {
    }

    public PlayRecord(Play play, Date date, boolean correct, Long timeSpent) {
        setPlay(play);
        setDate(date);
        setCorrect(correct);
        setTimeSpent(timeSpent);
    }

    public void setFormula(@NonNull Formula formula) {
        firstOperand = formula.getFirstOperand();
        if (formula.getOperator() != null) {
            setOperator(formula.getOperator());
        }
        secondOperand = formula.getSecondOperand();
        result = formula.getResult();
        if (formula.getUnknown() != null) {
            setUnknown(formula.getUnknown());
        }
    }

    public PlayRecord setFormula(Integer firstOperand, Operator operator, Integer secondOperand, Integer result, FormulaPart unknown) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        setOperator(operator);
        this.result = result;
        setUnknown(unknown);
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
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
        if (operator != null) {
            return operator;
        } else if (operatorStr != null) {
            operator = Operator.getValue(operatorStr);
        }
        return operator;
    }

    public void setOperator(Operator operator) {
        if (operator != null) {
            this.operator = operator;
            operatorStr = operator.value;
        } else {
            this.operator = null;
            operatorStr = null;
        }
    }

    @SuppressWarnings("unused")
    public Operator getOperator2() {
        if (operator2 != null) {
            return operator2;
        } else if (operator2Str != null) {
            operator2 = Operator.getValue(operator2Str);
        }
        return operator2;
    }

    @SuppressWarnings("unused")
    public void setOperator2(Operator operator2) {
        if (operator2 != null) {
            this.operator2 = operator2;
            operator2Str = operator2.value;
        } else {
            this.operator2 = null;
            operator2Str = null;
        }
    }

    public FormulaPart getUnknown() {
        if (unknown != null) {
            return unknown;
        } else if (unknownStr != null) {
            unknown = FormulaPart.getValue(unknownStr);
        }
        return unknown;
    }

    public void setUnknown(FormulaPart unknown) {
        if (unknown != null ) {
            this.unknown = unknown;
            unknownStr = unknown.key;
        } else {
            this.unknown = null;
            unknownStr = null;
        }
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * @return value that user has entered and it is incorrect
     */
    public String getWrongValue() {
        return wrongValue;
    }

    public void setWrongValue(String wrongValue) {
        this.wrongValue = wrongValue;
    }

    /**
     * @return amount of milliseconds that user spent solving this formula
     */
    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    /**
     * Correctly solved formula will receive points. Value depends on formula complexity.
     * @return points for this formula
     */
    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    /**
     * This method returns formula without localization.
     * @return formula with spaces
     */
    public String getFormulaString() {
        return firstOperand + " " + getOperator() + " " + secondOperand + " = " + result;
    }

    @Override
    public String toString() {
        return "PlayRecord{" +
                "id=" + id +
                ", date=" + date +
                ", play=" + getPlay() +
                ", firstOperand=" + firstOperand +
                ", secondOperand=" + secondOperand +
                ", thirdOperand=" + thirdOperand +
                ", result=" + result +
                ", operator=" + getOperator() +
                ", operator2=" + getOperator2() +
                ", unknown=" + getUnknown() +
                ", wrongValue" + getWrongValue() +
                ", correct=" + correct +
                ", timeSpent=" + timeSpent +
                '}';
    }
}
