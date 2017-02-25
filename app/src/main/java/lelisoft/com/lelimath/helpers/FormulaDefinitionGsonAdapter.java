package lelisoft.com.lelimath.helpers;


import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import lelisoft.com.lelimath.data.Expression;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.SequenceOrder;
import lelisoft.com.lelimath.data.Values;

/**
 * Deserializes JSON holding FormulaDefinition data
 * Created by Leoš on 22.12.2016.
 */

public class FormulaDefinitionGsonAdapter implements JsonDeserializer<FormulaDefinition> {
    private static final Logger log = LoggerFactory.getLogger(FormulaDefinitionGsonAdapter.class);

    @Override
    public FormulaDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        log.debug("deserialize starts");
        FormulaDefinition definition = new FormulaDefinition();
        JsonObject jsonObject = json.getAsJsonObject();

        JsonPrimitive p = jsonObject.getAsJsonPrimitive("count");
        if (p == null) {
            definition.setCount(6); // default
        } else {
            definition.setCount(p.getAsInt());
        }

        p = jsonObject.getAsJsonPrimitive("order");
        if (p == null) {
            definition.setOrder(SequenceOrder.RANDOM); // default
        } else {
            definition.setOrder(SequenceOrder.valueOf(p.getAsString()));
        }

        p = jsonObject.getAsJsonPrimitive("sequence");
        if (p == null) {
            definition.setSequence(FormulaPart.FIRST_OPERAND); // default
        } else {
            definition.setSequence(FormulaPart.getValue(p.getAsString()));
        }

        JsonArray array = jsonObject.getAsJsonArray("unknowns");
        if (array != null && array.size() > 0) {
            for (JsonElement jsonElement : array) {
                FormulaPart part = FormulaPart.getValue(jsonElement.getAsString());
                definition.addUnknown(part);
            }
        }

        array = jsonObject.getAsJsonArray("games");
        if (array != null && array.size() > 0) {
            for (JsonElement jsonElement : array) {
                Game game = Game.getValue(jsonElement.getAsString());
                definition.addGame(game);
            }
        }

        array = jsonObject.getAsJsonArray("expressions");
        if ((array == null || array.size() == 0)) {
            throw new JsonParseException("Element 'operators' is missing!");
        }
        for (JsonElement jsonElement : array) {
            jsonObject = (JsonObject) jsonElement;

            Values firstArg = getValues(jsonObject, "first");
            Values secondArg = getValues(jsonObject, "second");
            Values thirdArg = getValues(jsonObject, "third");
            Values result = getValues(jsonObject, "result");
            p = jsonObject.getAsJsonPrimitive("operator1");
            Operator operator1 = Operator.getValue(p.getAsString());
            p = jsonObject.getAsJsonPrimitive("operator2");
            Operator operator2 = Operator.getValue(p.getAsString());

            Expression expression = new Expression(firstArg, operator1, secondArg, result);
            expression.setOperator2(operator2);
            expression.setThirdOperand(thirdArg);
            definition.addExpression(expression);
        }

        log.debug("deserialize finishes");
        return definition;
    }

    private Values getValues(JsonObject parent, String name) {
        JsonObject object = parent.getAsJsonObject(name);
        JsonPrimitive p = object.getAsJsonPrimitive("values");
        String sValues = p.getAsString();
        Values values = Values.parse(sValues, false);

        p = object.getAsJsonPrimitive("order");
        if (p == null) {
            values.setOrder(SequenceOrder.RANDOM); // default
        } else {
            values.setOrder(SequenceOrder.valueOf(p.getAsString()));
        }
        return values;
    }
}
