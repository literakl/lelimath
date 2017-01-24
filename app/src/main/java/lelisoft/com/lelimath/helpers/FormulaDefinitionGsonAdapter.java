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

import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.OperatorDefinition;
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
            throw new JsonParseException("Element 'count' is missing!");
        }
        definition.setCount(p.getAsInt());

        JsonArray array = jsonObject.getAsJsonArray("unknowns");
        if (array != null && array.size() > 0) {
            for (JsonElement jsonElement : array) {
                FormulaPart part = FormulaPart.getValue(jsonElement.getAsString());
                definition.addUnknown(part);
            }
        }

        array = jsonObject.getAsJsonArray("operators");
        if (array == null || array.size() == 0) {
            throw new JsonParseException("Element 'operators' is missing!");
        }

        for (JsonElement jsonElement : array) {
            jsonObject = (JsonObject) jsonElement;

            p = jsonObject.getAsJsonPrimitive("first");
            String sValues = p.getAsString();
            Values firstArgValues = Values.parse(sValues);

            p = jsonObject.getAsJsonPrimitive("second");
            sValues = p.getAsString();
            Values secondArgValues = Values.parse(sValues);

            p = jsonObject.getAsJsonPrimitive("result");
            sValues = p.getAsString();
            Values resultValues = Values.parse(sValues);

            JsonArray opArray = jsonObject.getAsJsonArray("operator");
            for (JsonElement element : opArray) {
                Operator operator = Operator.getValue(element.getAsString());
                OperatorDefinition operatorDefinition = new OperatorDefinition(operator);
                definition.addOperator(operatorDefinition);
                operatorDefinition.setFirstOperand(firstArgValues);
                operatorDefinition.setSecondOperand(secondArgValues);
                operatorDefinition.setResult(resultValues);
            }
        }

        log.debug("deserialize finishes");
        return definition;
    }
}
