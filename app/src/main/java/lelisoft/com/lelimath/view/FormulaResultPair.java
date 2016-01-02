package lelisoft.com.lelimath.view;

import lelisoft.com.lelimath.data.Formula;

/**
 * Holder for formula or result
 * Created by Leoš on 02.01.2016.
 */
public class FormulaResultPair {
    public Formula formula;
    public Integer result;

    public FormulaResultPair(Formula formula) {
        this.formula = formula;
    }

    public FormulaResultPair(Integer result) {
        this.result = result;
    }
}
