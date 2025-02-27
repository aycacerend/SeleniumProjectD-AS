package com.testinium.step;

import com.google.gson.JsonElement;
import com.testinium.base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public class ReadJsonRestAssured {

    public ReadJsonRestAssured() {

    }

    public JsonPath getJsonPathByMap(String jsonMapKey) {

        JsonPath jsonPath = null;
        if (BaseTest.apiMap.containsKey(jsonMapKey)) {
            jsonPath = ((Response) BaseTest.apiMap.get(jsonMapKey).get("response")).jsonPath();

        } else if (jsonMapKey.endsWith("KeyValue")) {
            Object value = BaseTest.TestMap.get(jsonMapKey);
            if (value instanceof JsonElement) {
                jsonPath = new JsonPath(((JsonElement) value).toString());
            } else if (value instanceof String) {
                jsonPath = new JsonPath(value.toString());
            } else
                fail("Hatalı json data");
        } else {
            jsonPath = new JsonPath(jsonMapKey);
        }
        return jsonPath;
    }

    public void readJsonWithRestAssured(JsonPath jsonPath, String path, String type, String mapKey) {

        String[] types = type.split(",");
        switch (types[0].trim()) {

            case "String":
                BaseTest.TestMap.put(mapKey, jsonPath.getString(path));
                break;
            case "Boolean":
                BaseTest.TestMap.put(mapKey, jsonPath.getBoolean(path));
                break;
            case "Integer":
                BaseTest.TestMap.put(mapKey, jsonPath.getInt(path));
                break;
            case "Long":
                BaseTest.TestMap.put(mapKey, jsonPath.getLong(path));
                break;
            case "Float":
                BaseTest.TestMap.put(mapKey, jsonPath.getFloat(path));
                break;
            case "Double":
                BaseTest.TestMap.put(mapKey, jsonPath.getDouble(path));
                break;
            case "Byte":
                BaseTest.TestMap.put(mapKey, jsonPath.getByte(path));
                break;
            case "Short":
                BaseTest.TestMap.put(mapKey, jsonPath.getShort(path));
                break;
            case "JsonObject":
                if (types.length == 2) {
                    BaseTest.TestMap.put(mapKey, jsonPath.getObject(path, getClassForName(types[1].trim())));
                    break;
                }
                BaseTest.TestMap.put(mapKey, jsonPath.getJsonObject(path));
                break;
            case "List":
                if (types.length == 2) {
                    BaseTest.TestMap.put(mapKey, jsonPath.getList(path, getClassForName(types[1].trim())));
                    break;
                }
                BaseTest.TestMap.put(mapKey, jsonPath.getList(path, Object.class));
                break;

            case "Map":
                if (types.length == 3) {
                    BaseTest.TestMap.put(mapKey, jsonPath.getMap(path, getClassForName(types[1].trim()), getClassForName(types[2].trim())));
                    break;
                }
                BaseTest.TestMap.put(mapKey, jsonPath.getMap(path, Object.class, Object.class));
                break;
        }
    }

    private Class getClassForName(String classForName) {

        Class clazz = null;
        switch (classForName) {
            case "String":
                clazz = String.class;
                break;
            case "Boolean":
                clazz = Boolean.class;
                break;
            case "Integer":
                clazz = Integer.class;
                break;
            case "Long":
                clazz = Long.class;
                break;
            case "Float":
                clazz = Float.class;
                break;
            case "Double":
                clazz = Double.class;
                break;
            case "List":
                clazz = List.class;
                break;
            case "Map":
                clazz = Map.class;
                break;
            case "Object":
                clazz = Object.class;
                break;

            default:
                try {
                    clazz = Class.forName(classForName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
        return clazz;
    }

}
