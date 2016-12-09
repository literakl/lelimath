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
    Date date;

    @DatabaseField(foreign = true, columnName=Columns.PLAY_ID)
    Play play;

    @DatabaseField(canBeNull=true, columnName=Columns.FIRST_OPERAND)
    Integer firstOperand;

    @DatabaseField(canBeNull=true, columnName=Columns.SECOND_OPERAND)
    Integer secondOperand;

    @DatabaseField(canBeNull=true, columnName=Columns.RESULT)
    Integer result;

    @DatabaseField(persisted = false)
    Operator operator;

    @DatabaseField(canBeNull=true, columnName= Columns.OPERATOR, width = 1)
    String operatorStr;

    @DatabaseField(persisted = false)
    FormulaPart unknown;

    @DatabaseField(canBeNull=true, columnName=Columns.UNKNOWN, width = 2)
    String unknownStr;

    @DatabaseField(canBeNull=true, columnName= Columns.CORRECT)
    boolean correct;

    @DatabaseField(canBeNull=true, columnName=Columns.WRONG_VALUE)
    String wrongValue;

    @DatabaseField(canBeNull=true, columnName=Columns.SPENT)
    Long timeSpent;

    @DatabaseField(canBeNull=false, defaultValue = "0", columnName= Columns.POINTS)
    Integer points;

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
                ", result=" + result +
                ", operator=" + getOperator() +
                ", unknown=" + getUnknown() +
                ", wrongValue" + getWrongValue() +
                ", correct=" + correct +
                ", timeSpent=" + timeSpent +
                '}';
    }
}
