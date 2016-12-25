package lelisoft.com.lelimath.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.TestScript;
import lelisoft.com.lelimath.helpers.FormulaDefinitionGsonAdapter;

/**
 * Parser for JSON containing test script
 * Created by Leoš on 21.12.2016.
 */

public class ScriptParser {
    private static final Logger log = LoggerFactory.getLogger(ScriptParser.class);

    public static TestScript parse(InputStream is) {
        log.debug("Starting to parse");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FormulaDefinition.class, new FormulaDefinitionGsonAdapter());
        Gson gson = gsonBuilder.create();
        InputStreamReader reader = new InputStreamReader(is);
        TestScript script = gson.fromJson(reader, TestScript.class);
        log.debug("finished");
        return script;
    }
}
