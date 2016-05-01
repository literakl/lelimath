package lelisoft.com.lelimath.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Records formula solving activity into database
 * Created by Leoš on 29.04.2016.
 */
@DatabaseTable(tableName = "formula_record")
public class FormulaRecord {
    @DatabaseField(generatedId = true)
    Long id;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(canBeNull = false, foreign = true)
    User user;

    @DatabaseField(persisted=false)
    Game game;

    @DatabaseField(canBeNull=false, columnName="game", width = 2)
    String gameStr;

    @DatabaseField(canBeNull=true, columnName="first")
    Integer firstOperand;

    @DatabaseField(canBeNull=true, columnName="second")
    Integer secondOperand;

    @DatabaseField(canBeNull=true, columnName="result")
    Integer result;

    @DatabaseField(persisted = false)
    Operator operator;

    @DatabaseField(canBeNull=true, columnName="operator", width = 1)
    String operatorStr;

    @DatabaseField(persisted = false)
    FormulaPart unknown;

    @DatabaseField(canBeNull=true, columnName="unknown", width = 2)
    String unknownStr;

    @DatabaseField(canBeNull=true, columnName="correct")
    boolean correct;

    @DatabaseField(canBeNull=true, columnName="spent")
    Long timeSpent;

    public FormulaRecord() {
    }

    public void setFormula(@NonNull Formula formula) {
        firstOperand = formula.getFirstOperand();
        secondOperand = formula.getSecondOperand();
        result = formula.getResult();
        if (formula.getUnknown() != null) {
            setUnknown(formula.getUnknown());
        }
        timeSpent = formula.getTimeSpent();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Game getGame() {
        if (game != null) {
            return game;
        } else if (gameStr != null) {
            game = Game.getValue(gameStr);
        }
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        gameStr = game.key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        this.operator = operator;
        operatorStr = operator.value;
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
        this.unknown = unknown;
        unknownStr = unknown.key;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * @return amount of milliseconds that user spent solving this formula
     */
    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    @Override
    public String toString() {
        return "FormulaRecord{" +
                "id=" + id +
                ", date=" + date +
                ", user=" + user +
                ", game=" + getGame() +
                ", firstOperand=" + firstOperand +
                ", secondOperand=" + secondOperand +
                ", result=" + result +
                ", operator=" + getOperator() +
                ", unknown=" + getUnknown() +
                ", correct=" + correct +
                ", timeSpent=" + timeSpent +
                '}';
    }
}
