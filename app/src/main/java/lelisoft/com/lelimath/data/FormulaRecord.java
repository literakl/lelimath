package lelisoft.com.lelimath.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DataType;
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

    @DatabaseField
    Integer firstOperand;

    @DatabaseField
    Integer secondOperand;

    @DatabaseField
    Integer result;

    @DatabaseField(persisted = false)
//    @DatabaseField(canBeNull = false, foreign = true)
    Operator operator;

//    @DatabaseField(persisted = false)
//    @DatabaseField(canBeNull = false, foreign = true)
    @DatabaseField(dataType= DataType.ENUM_INTEGER)
    FormulaPart unknown;

    @DatabaseField
    boolean correct;

    @DatabaseField
    Long timeSpent;

    public FormulaRecord() {
    }

    public void setFormula(@NonNull Formula formula) {
        firstOperand = formula.getFirstOperand();
        secondOperand = formula.getSecondOperand();
        result = formula.getResult();
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
            game = Game.valueOf(gameStr);
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
                ", game=" + game +
                ", firstOperand=" + firstOperand +
                ", secondOperand=" + secondOperand +
                ", result=" + result +
                ", operator=" + operator +
                ", unknown=" + unknown +
                ", correct=" + correct +
                ", timeSpent=" + timeSpent +
                '}';
    }
}
