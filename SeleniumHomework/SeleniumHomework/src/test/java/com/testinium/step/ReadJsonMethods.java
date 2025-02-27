package com.testinium.step;

import com.google.common.base.Splitter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.testinium.base.BaseTest;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ReadJsonMethods {

    private static final Logger logger = LoggerFactory.getLogger(ReadJsonMethods.class);
    Methods methodsUtil;

    public ReadJsonMethods() {

        methodsUtil = new Methods();
    }

    public void readJsonPath(JsonElement element, List<String> jsonPath, String type, String mapKey) {

        String splitValue = "";
        for (String value : jsonPath) {
            splitValue = !splitValue.equals("") ? splitValue + "|?" + value : value;
            if (element.isJsonArray()) {
                element = element.getAsJsonArray().get(Integer.parseInt(value));
            } else if (element.isJsonObject()) {
                element = element.getAsJsonObject().get(value);
            } else {
                BaseTest.TestMap.put(mapKey, "JsonElementNull");
                //  logger.info(splitValue + " elementi bulunamadı");
                //  fail("Hatalı json path: " + jsonPath.toString());
                return;
            }
            if (element == null) {
                BaseTest.TestMap.put(mapKey, "JsonElementNull");
                //   logger.info(splitValue + " elementi bulunamadı");
                return;
            }
        }
        if (element.isJsonNull() && !type.equals("JsonNull")) {
            BaseTest.TestMap.put(mapKey, "JsonNull");
            logger.info(splitValue + " elementi null değere sahip");
            return;
        }
        getValueFromJsonAndSaveMap(element, type, mapKey);
    }

    public List<String> getJsonPathAsList(String jsonPath, String splitRegex) {

        if (jsonPath.equals(""))
            return new ArrayList<>();
        return Splitter.on(splitRegex).splitToList(jsonPath);
    }

    public int getJsonListSize(JsonArray jsonArray) {

        return jsonArray.size();
    }

    public void setJsonPathMultipleValue(String jsonPath, String value, String type, String id, String mapKey, String valueControlType, boolean OrActive) {

        jsonPath = methodsUtil.setValueWithMapKey(jsonPath);
        Object objectValue;
        if (value.endsWith("KeyValue")) {
            objectValue = BaseTest.TestMap.get(value);
        } else {
            objectValue = value;
        }
        JsonPathValue jsonPathValue = new JsonPathValue();
        jsonPathValue.setKey(jsonPath);
        jsonPathValue.setValue(objectValue);
        jsonPathValue.setType(type);
        jsonPathValue.setId(id);
        jsonPathValue.setValueControlType(methodsUtil.getTextByMap(valueControlType));
        jsonPathValue.setOrActive(OrActive);
        if (BaseTest.TestMap.containsKey(mapKey)) {
            ((List<JsonPathValue>) BaseTest.TestMap.get(mapKey)).add(jsonPathValue);
        } else {
            List<JsonPathValue> list = new ArrayList<>();
            list.add(jsonPathValue);
            BaseTest.TestMap.put(mapKey, list);
        }
    }

    public void getMultipleValue(String jsonPathKey, JsonElement jsonElement, String splitRegex, String mapKeySuffix) {

        List<JsonPathValue> jsonPathValues = (List<JsonPathValue>) BaseTest.TestMap.get(jsonPathKey);
        for (JsonPathValue jsonPathValue : jsonPathValues) {
            String mapKey = jsonPathValue.getId() + mapKeySuffix;
            List<String> values = getJsonPathAsList(jsonPathValue.getKey(), splitRegex);
            readJsonPath(jsonElement, values, jsonPathValue.getType(), mapKey);
            logger.info(mapKey + " : " + BaseTest.TestMap.get(mapKey).toString());
        }
    }

    public void getMultipleListValue(String controlJsonKey, String jsonKey, JsonElement jsonElement, String splitRegex, String mapKeySuffix) {

        List<String> listJsonElementNumber = new ArrayList<>();
        boolean condition = true;
        if (!jsonElement.isJsonArray())
            fail("Hata Json array olmalı");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<JsonPathValue> jsonPathValues = (List<JsonPathValue>) BaseTest.TestMap.get(jsonKey);
        jsonPathValues.forEach(jsonPathValue -> BaseTest.TestMap.remove(jsonPathValue.getId() + "List" + mapKeySuffix));
        BaseTest.TestMap.remove("numberListJsonArray" + mapKeySuffix);
        boolean isControlJsonKey = !controlJsonKey.equals("");
        List<JsonPathValue> jsonPathControlValues = null;
        if (isControlJsonKey) {
            if (!BaseTest.TestMap.containsKey(controlJsonKey)) {
                isControlJsonKey = false;
            } else
                jsonPathControlValues = (List<JsonPathValue>) BaseTest.TestMap.get(controlJsonKey);
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement jsonElementInArray = jsonArray.get(i);
            condition = true;
            if (isControlJsonKey) {
                for (JsonPathValue jsonPathValue : jsonPathControlValues) {
                    String mapKey = jsonPathValue.getId() + mapKeySuffix;
                    List<String> values = getJsonPathAsList(jsonPathValue.getKey(), splitRegex);
                    readJsonPath(jsonElementInArray, values, jsonPathValue.getType(), mapKey);
                    String value = BaseTest.TestMap.get(mapKey).toString();
                    if (!jsonPathValue.getOrActive()) {
                        condition = methodsUtil.conditionValueControl(jsonPathValue.getValue().toString()
                                , value, jsonPathValue.getValueControlType());
                    } else {
                        List<String> controlValues = (List<String>) jsonPathValue.getValue();
                        for (String controlValue : controlValues) {
                            if (jsonPathValue.getValueControlType().startsWith("not")) {
                                condition = methodsUtil.conditionValueControl(controlValue
                                        , value, jsonPathValue.getValueControlType());
                                if (!condition) {
                                    break;
                                }
                            } else {
                                condition = methodsUtil.conditionValueControl(controlValue
                                        , value, jsonPathValue.getValueControlType());
                                if (condition) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!condition) {
                        break;
                    }
                }
            }
            if (condition) {
                listJsonElementNumber.add(String.valueOf(i));
                for (JsonPathValue jsonPathValue : jsonPathValues) {
                    String jsonPath = jsonPathValue.getKey();
                    if (jsonPath.contains("{?}")) {
                        getMultipleJsonArrayLoop(jsonElementInArray, jsonPathValue, mapKeySuffix, splitRegex);
                    } else {
                        saveMultipleListValue(jsonElementInArray, jsonPathValue, mapKeySuffix, splitRegex);
                    }
                }
            }
        }
        BaseTest.TestMap.put("numberListJsonArray" + mapKeySuffix, listJsonElementNumber);
    }

    public void saveMultipleListValue(JsonElement jsonElement, JsonPathValue jsonPathValue, String mapKeySuffix, String splitRegex) {

        String mapKey = jsonPathValue.getId() + mapKeySuffix;
        List<String> values = getJsonPathAsList(jsonPathValue.getKey(), splitRegex);
        readJsonPath(jsonElement, values, jsonPathValue.getType(), mapKey);
        String name = jsonPathValue.getId() + "List" + mapKeySuffix;
        String jsonValue = BaseTest.TestMap.get(mapKey).toString();
        if (BaseTest.TestMap.containsKey(name)) {
            ((List<String>) BaseTest.TestMap.get(name)).add(jsonValue);
        } else {
            List<String> list = new ArrayList<>();
            list.add(jsonValue);
            BaseTest.TestMap.put(name, list);
        }
    }

    public void getMultipleJsonArrayLoop(JsonElement jsonElement, JsonPathValue jsonPathValue, String mapKeySuffix, String splitRegex) {

        String mapKey = jsonPathValue.getId() + mapKeySuffix;
        String name = jsonPathValue.getId() + "List" + mapKeySuffix;
        List<String> jsonPathAsList = getJsonPathAsList(jsonPathValue.getKey(), splitRegex + "{?}" + splitRegex);
        boolean finishHim = false;
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        List<JsonElement> jsonElements = new ArrayList<JsonElement>();
        jsonElements.add(jsonElement);
        map.put("work", jsonElements);
        List<String> work3 = new ArrayList<>();
        List<String> work2 = new ArrayList<>();
        for (int i = 0; i < jsonPathAsList.size(); i++) {
            String a = jsonPathAsList.get(i);
            JsonArray jsonArray = null;
            List<JsonElement> work = (List<JsonElement>) map.get("work");
            for (int k = 0; k < work.size(); k++) {
                JsonElement jsonElement1 = work.get(k);
                if (i == jsonPathAsList.size() - 1) {
                    readJsonPath(jsonElement1, getJsonPathAsList(a, splitRegex), jsonPathValue.getType(), mapKey);
                    //  String u = work3.get(k) + splitRegex + a;
                    //   System.out.println(u);
                    String jsonValue = BaseTest.TestMap.get(mapKey).toString();
                    if (BaseTest.TestMap.containsKey(name)) {
                        ((List<String>) BaseTest.TestMap.get(name)).add(jsonValue);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(jsonValue);
                        BaseTest.TestMap.put(name, list);
                    }
                    finishHim = true;
                } else {
                    readJsonPath(jsonElement1, getJsonPathAsList(a, splitRegex), "JsonArray", mapKey);
                    JsonElement jsonElement2 = (JsonElement) BaseTest.TestMap.get(mapKey);
                    if (jsonElement2.isJsonArray()) {
                        jsonArray = jsonElement2.getAsJsonArray();
                        for (int j = 0; j < jsonArray.size(); j++) {
                            String u = work3.size() != 0 ? work3.get(k) + splitRegex + a + splitRegex + j : a + splitRegex + j;
                            map.put(u, jsonArray.get(j));
                            work2.add(u);
                        }
                    }
                }
            }
            work3.clear();
            work.clear();
            if (!finishHim) {
                for (String workString : work2) {
                    work.add((JsonElement) map.get(workString));
                }
                work3.addAll(work2);
                work2.clear();
            }
        }
        work3.clear();
        work2.clear();
    }

    public JsonElement getJsonElementByMap(String jsonMapKey) {

        String jsonString;
        JsonElement element = null;
        if (BaseTest.apiMap.containsKey(jsonMapKey)) {
            jsonString = ((Response) BaseTest.apiMap.get(jsonMapKey).get("response")).asString();
            element = JsonParser.parseString(jsonString);
        } else if (jsonMapKey.endsWith("KeyValue")) {
            Object value = BaseTest.TestMap.get(jsonMapKey);
            if (value instanceof JsonElement) {
                element = (JsonElement) value;
            } else if (value instanceof String) {
                element = JsonParser.parseString(value.toString());
            } else
                fail("Hatalı json data");
        } else {
            element = JsonParser.parseString(jsonMapKey);
        }
        return element;
    }

    public void getValueFromJsonAndSaveMap(JsonElement jsonElement, String type, String mapKey) {

        switch (type) {
            case "JsonObject":
                BaseTest.TestMap.put(mapKey, jsonElement.getAsJsonObject());
                break;
            case "JsonArray":
                BaseTest.TestMap.put(mapKey, jsonElement.getAsJsonArray());
                break;
            case "JsonPrimitive":
                BaseTest.TestMap.put(mapKey, jsonElement.getAsJsonPrimitive());
                break;
            case "JsonNull":
                BaseTest.TestMap.put(mapKey, "JsonNull");
                break;
            case "String":
                BaseTest.TestMap.put(mapKey, jsonElement.getAsString());
                break;
            case "ToString":
                BaseTest.TestMap.put(mapKey, jsonElement.toString());
                break;
            case "Integer":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsInt());
                break;
            case "Boolean":
                assertTrue(jsonElement.toString().equals("true") || jsonElement.toString().equals("false")
                        , " boolean bir değer değil");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsBoolean());
                break;
            case "Long":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsLong());
                break;
            case "BigInteger":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsBigInteger());
                break;
            case "BigDecimal":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?.*[0-9]+[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                if (!Pattern.matches("[0-9]+[\\.]?[0-9]*", jsonElement.toString()))
                    logger.warn(jsonElement.toString() + " degeri string bir değer");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsBigDecimal());
                break;
            case "Double":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsDouble());
                break;
            case "Float":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsFloat());
                break;
            case "Short":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsShort());
                break;
            case "Number":
                assertTrue(Pattern.matches("[\"]?[0-9]+[\\.]?[0-9]*[\"]?", jsonElement.toString())
                        , jsonElement.toString() + " sayı dışında karakter içeriyor");
                BaseTest.TestMap.put(mapKey, jsonElement.getAsNumber());
                break;
            default:
                fail(type + " data Tipi hatalı");
        }
    }

}

