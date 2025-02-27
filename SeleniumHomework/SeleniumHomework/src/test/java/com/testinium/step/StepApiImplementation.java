package com.testinium.step;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.testinium.base.BaseTest;
import com.thoughtworks.gauge.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepApiImplementation {

    private static final Logger logger = LoggerFactory.getLogger(StepApiImplementation.class);
    Methods methodsUtil;
    ApiMethods apiMethods;
    ReadJsonMethods readJsonMethods;

    public StepApiImplementation() {

        methodsUtil = new Methods();
        apiMethods = new ApiMethods();
        readJsonMethods = new ReadJsonMethods();
    }

    @Step("<apiMapKey> api testi için map key olustur")
    public void setKey(String apiMapKey) {

        BaseTest.apiMap.put(apiMapKey, new ConcurrentHashMap<String, Object>());
    }

    @Step("<baseUri> baseUri ı <apiMapKey> e ekle")
    public void setKeyBaseUri(String baseUri, String apiMapKey) {

        if (baseUri.endsWith("KeyValue")) {
            baseUri = BaseTest.TestMap.get(baseUri).toString();
        }
        logger.info("BaseUri: " + baseUri);
        BaseTest.apiMap.get(apiMapKey).put("baseUri", baseUri);
    }

    @Step("<isRelaxedHTTPSValidation> relaxedHTTPSValidation ı <apiMapKey> e ekle")
    public void setRelaxedHTTPSValidation(Boolean isRelaxedHTTPSValidation, String apiMapKey) {

        logger.info("isRelaxedHTTPSValidation: " + isRelaxedHTTPSValidation);
        BaseTest.apiMap.get(apiMapKey).put("isRelaxedHTTPSValidation", isRelaxedHTTPSValidation);
    }

    @Step("<acceptValue> accept degerini <apiMapKey> e ekle")
    public void setKeyAccept(String acceptValue, String apiMapKey) {

        BaseTest.apiMap.get(apiMapKey).put("accept", acceptValue);
    }

    @Step("<contentType> contentType degerini <apiMapKey> e ekle")
    public void setKeyContentType(String contentType, String apiMapKey) {

        if (contentType.endsWith("KeyValue")) {
            contentType = BaseTest.TestMap.get(contentType).toString();
        }
        logger.info("Content-Type: " + contentType);
        BaseTest.apiMap.get(apiMapKey).put("contentType", contentType);
    }

    //unchecked
    @Step("<headerKey> <headerValue> header degerini <apiMapKey> e ekle")
    public void setKeyHeaders(String headerKey, String headerValue, String apiMapKey) {

        if (headerKey.endsWith("KeyValue")) {
            headerKey = BaseTest.TestMap.get(headerKey).toString();
        }
        if (headerValue.endsWith("KeyValue")) {
            headerValue = BaseTest.TestMap.get(headerValue).toString();
        }
        headerValue = methodsUtil.setValueWithMapKey(headerValue);
        logger.info("**Headers** " + headerKey + " : " + headerValue);
        if (BaseTest.apiMap.get(apiMapKey).containsKey("headers")) {

            ((ConcurrentHashMap<String, String>) BaseTest.apiMap.get(apiMapKey).get("headers"))
                    .put(headerKey, headerValue);
        } else {
            ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
            map.put(headerKey, headerValue);
            BaseTest.apiMap.get(apiMapKey).put("headers", map);
        }
    }

    @Step("<headerKey> <headerValue> header degerini <apiMapKey> e ekle <condition>")
    public void setKeyHeaders(String headerKey, String headerValue, String apiMapKey, String condition) {

        boolean value = condition.startsWith("!");
        condition = value ? condition.substring(1) : condition;
        boolean isTrue = condition.endsWith("KeyValue") ? Boolean.parseBoolean(BaseTest.TestMap.get(condition).toString())
                : Boolean.parseBoolean(condition);
        isTrue = value != isTrue;
        if (isTrue) {
            setKeyHeaders(headerKey, headerValue, apiMapKey);
        }
    }

    @Step("<username> <password> basic auth degerini <apiMapKey> e ekle preemptive <preemptive>")
    public void setBasicAuth(String username, String password, String apiMapKey, Boolean preemptive) {

        if (username.endsWith("KeyValue")) {
            username = BaseTest.TestMap.get(username).toString();
        }
        if (password.endsWith("KeyValue")) {
            password = BaseTest.TestMap.get(password).toString();
        }
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("username", username);
        map.put("password", password);
        map.put("preemptive", preemptive);
        BaseTest.apiMap.get(apiMapKey).put("basicAuth", map);
    }

    @Step("<token> değerini auth2 için <apiMapKey> e ekle")
    public void setAuth2(String token, String apiMapKey) {

        if (token.endsWith("KeyValue")) {
            token = BaseTest.TestMap.get(token).toString();
        }
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("token", token);
        BaseTest.apiMap.get(apiMapKey).put("auth2", map);
    }

    @Step("<host> <port> <username> <password> yada <uri> degerleriyle proxy i <apiMapKey> e ekle")
    public void setProxy(String host, String port, String username, String password, String uri, String apiMapKey) {

        if (host.endsWith("KeyValue")) {
            host = BaseTest.TestMap.get(host).toString();
        }
        if (port.endsWith("KeyValue")) {
            port = BaseTest.TestMap.get(port).toString();
        }
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        if (!host.equals("") && !port.equals("")) {
            map.put("host", host);
            map.put("port", port);
        }
        if (username.endsWith("KeyValue")) {
            username = BaseTest.TestMap.get(username).toString();
        }
        if (password.endsWith("KeyValue")) {
            password = BaseTest.TestMap.get(password).toString();
        }
        if (!username.equals("") && !password.equals("")) {
            map.put("username", username);
            map.put("password", password);
        }
        if (uri.endsWith("KeyValue")) {
            uri = BaseTest.TestMap.get(uri).toString();
        }
        if (!uri.equals("")) {
            map.put("uri", uri);
        }
        BaseTest.apiMap.get(apiMapKey).put("proxy", map);
    }

    @SuppressWarnings("unchecked")
    @Step("<cookiesKey> <cookiesValue> cookies degerini <apiMapKey> e ekle")
    public void setCookies(String cookiesKey, String cookiesValue, String apiMapKey) {

        if (cookiesKey.endsWith("KeyValue")) {
            cookiesKey = BaseTest.TestMap.get(cookiesKey).toString();
        }
        if (cookiesValue.endsWith("KeyValue")) {
            cookiesValue = BaseTest.TestMap.get(cookiesValue).toString();
        }
        logger.info("**Cookies** " + cookiesKey + " : " + cookiesValue);
        if (BaseTest.apiMap.get(apiMapKey).containsKey("cookies")) {

            ((ConcurrentHashMap<String, String>) BaseTest.apiMap.get(apiMapKey).get("cookies"))
                    .put(cookiesKey, cookiesValue);
        } else {
            ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
            map.put(cookiesKey, cookiesValue);
            BaseTest.apiMap.get(apiMapKey).put("cookies", map);
        }
    }

    @Step("<cookiesKey> <cookiesValue> cookies degerini <apiMapKey> e ekle <condition>")
    public void setCookies(String cookiesKey, String cookiesValue, String apiMapKey, String condition) {

        boolean value = condition.startsWith("!");
        condition = value ? condition.substring(1) : condition;
        boolean isTrue = condition.endsWith("KeyValue") ? Boolean.parseBoolean(BaseTest.TestMap.get(condition).toString())
                : Boolean.parseBoolean(condition);
        isTrue = value != isTrue;
        if (isTrue) {
            setCookies(cookiesKey, cookiesValue, apiMapKey);
        }
    }

    @SuppressWarnings("unchecked")
    @Step("<paramKey> <paramValue> parametre degerini <apiMapKey> e ekle")
    public void setKeyParams(String paramKey, String paramValue, String apiMapKey) {

        if (paramValue.endsWith("KeyValue")) {
            paramValue = BaseTest.TestMap.get(paramValue).toString();
        }
        logger.info(paramKey + " " + paramValue);
        if (BaseTest.apiMap.get(apiMapKey).containsKey("params")) {

            ((ConcurrentHashMap<String, Object>) BaseTest.apiMap.get(apiMapKey).get("params"))
                    .put(paramKey, paramValue);
        } else {
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
            map.put(paramKey, paramValue);
            BaseTest.apiMap.get(apiMapKey).put("params", map);
        }
    }

    @Step("<paramKey> <paramValue> parametre degerini <apiMapKey> e ekle <condition>")
    public void setKeyParams(String paramKey, String paramValue, String apiMapKey, String condition) {

        boolean value = condition.startsWith("!");
        condition = value ? condition.substring(1) : condition;
        boolean isTrue = condition.endsWith("KeyValue") ? Boolean.parseBoolean(BaseTest.TestMap.get(condition).toString())
                : Boolean.parseBoolean(condition);
        isTrue = value != isTrue;
        if (isTrue) {
            setKeyParams(paramKey, paramValue, apiMapKey);
        }
    }

    @SuppressWarnings("unchecked")
    @Step("<paramKey> <paramValue> query parametre degerini <apiMapKey> e ekle")
    public void setKeyQueryParams(String paramKey, String paramValue, String apiMapKey) {

        if (paramValue.endsWith("KeyValue")) {
            paramValue = BaseTest.TestMap.get(paramValue).toString();
        }
        logger.info(paramKey + " " + paramValue);
        if (BaseTest.apiMap.get(apiMapKey).containsKey("queryParams")) {

            ((ConcurrentHashMap<String, Object>) BaseTest.apiMap.get(apiMapKey).get("queryParams"))
                    .put(paramKey, paramValue);
        } else {
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
            map.put(paramKey, paramValue);
            BaseTest.apiMap.get(apiMapKey).put("queryParams", map);
        }
    }

    @SuppressWarnings("unchecked")
    @Step("<paramKey> <paramValue> form parametre degerini <apiMapKey> e ekle")
    public void setKeyFormParams(String paramKey, String paramValue, String apiMapKey) {

        if (paramValue.endsWith("KeyValue")) {
            paramValue = BaseTest.TestMap.get(paramValue).toString();
        }
        logger.info(paramKey + " " + paramValue);
        if (BaseTest.apiMap.get(apiMapKey).containsKey("formParams")) {

            ((ConcurrentHashMap<String, Object>) BaseTest.apiMap.get(apiMapKey).get("formParams"))
                    .put(paramKey, paramValue);
        } else {
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
            map.put(paramKey, paramValue);
            BaseTest.apiMap.get(apiMapKey).put("formParams", map);
        }
    }

    @Step("<body> body degerini <bodyType> tipiyle <apiMapKey> e ekle")
    public void setKeyBody(String body, String bodyType, String apiMapKey) {

        if (bodyType.equals("String") && body.endsWith("KeyValue")) {
            body = BaseTest.TestMap.get(body).toString();
        }
        BaseTest.apiMap.get(apiMapKey).put("body", body);
        BaseTest.apiMap.get(apiMapKey).put("bodyType", "body" + bodyType);
        logger.info(body);
    }

    @Step("<requestType> requestType ve <requestPath> i <apiMapKey> e ekle <requestPathType>")
    public void setKeyRequestType(String requestType, String requestPath, String apiMapKey, String requestPathType) {

        requestPath = methodsUtil.setValueWithMap(requestPath);

        if (requestPath.endsWith("KeyValue")) {
            requestPath = BaseTest.TestMap.get(requestPath).toString();
        }

        logger.info(requestPath);
        BaseTest.apiMap.get(apiMapKey).put("requestType", requestType);
        BaseTest.apiMap.get(apiMapKey).put("requestPathType", requestPathType);
        BaseTest.apiMap.get(apiMapKey).put("requestPath", requestPath);
    }

    @Step("<apiMapKey> api testi için istek at, log=<logActive> if <ifCondition>")
    public void sendRequest(String apiMapKey, boolean logActive, String ifCondition) {

        if (ifCondition.endsWith("KeyValue")) {
            ifCondition = BaseTest.TestMap.get(ifCondition).toString();
        }
        boolean condition = Boolean.parseBoolean(ifCondition);
        if (condition) {
            sendRequest(apiMapKey, logActive);
        } else {
            logger.info(condition + " durumu sebebiyle api isteği atılmadı");
        }
    }

    @Step("<apiMapKey> api testi statusCode değeri <statusCode> değerine eşit mi if <ifCondition>")
    public void statusCodeControl(String apiMapKey, String statusCode, String ifCondition) {

        if (ifCondition.endsWith("KeyValue")) {
            ifCondition = BaseTest.TestMap.get(ifCondition).toString();
        }
        boolean condition = Boolean.parseBoolean(ifCondition);
        if (condition) {
            statusCodeControl(apiMapKey, statusCode);
        }
    }

    @Step("<apiMapKey> api testi için istek at")
    public void sendRequest(String apiMapKey) {

        sendRequest(apiMapKey, false);
    }

    @Step("<apiMapKey> api testi için istek at, log=<logActive>")
    public void sendRequest(String apiMapKey, boolean logActive) {

        Response response = apiMethods.getResponse(BaseTest.apiMap.get(apiMapKey));
        logger.info(response.statusLine());
        if (logActive) {
            logger.info("" + response.statusCode());
            logger.info(apiMapKey + "Response:\n" + "" + response.asPrettyString());
            logger.info("" + response.getTime());
            //logger.info("" + response.getCookies().toString());
            logger.info(response.getHeaders().toString());
        }
        BaseTest.apiMap.get(apiMapKey).put("response", response);
    }

    @Step("<apiMapKey> api testi statusCode değeri <statusCode> değerine eşit mi")
    public void statusCodeControl(String apiMapKey, String statusCode) {

        statusCode = statusCode.endsWith("KeyValue") ? BaseTest.TestMap.get(statusCode).toString() : statusCode;
        Response response = (Response) BaseTest.apiMap.get(apiMapKey).get("response");
        assertEquals(Integer.parseInt(statusCode), response.statusCode());
    }

    @Step("<property> <value> json objesi değeri olarak <mapJsonObjectKey> e ekle <valueType>")
    public void addJsonObject(String property, String value, String mapKey, String valueType) {

        if (BaseTest.TestMap.containsKey(mapKey)) {
            JsonObject jsonObject = (JsonObject) BaseTest.TestMap.get(mapKey);
            methodsUtil.setStepJsonObjectValue(jsonObject, property, value, valueType);
            logger.info("" + BaseTest.TestMap.get(mapKey).toString());
        } else {
            JsonObject jsonObject = new JsonObject();
            methodsUtil.setStepJsonObjectValue(jsonObject, property, value, valueType);
            BaseTest.TestMap.put(mapKey, jsonObject);
            logger.info("" + BaseTest.TestMap.get(mapKey).toString());
        }
    }

    @Step("<mapJsonObjectKey> json array olustur <isOverwrite>")
    public void createJsonArray(String mapKey, Boolean isOverwrite) {

        if (isOverwrite || !BaseTest.TestMap.containsKey(mapKey)) {
            JsonArray jsonArray = new JsonArray();
            BaseTest.TestMap.put(mapKey, jsonArray);
        }
    }

    @Step("<mapJsonObjectKey> json object olustur <isOverwrite>")
    public void createJsonObject(String mapKey, Boolean isOverwrite) {

        if (isOverwrite || !BaseTest.TestMap.containsKey(mapKey)) {
            JsonObject jsonObject = new JsonObject();
            BaseTest.TestMap.put(mapKey, jsonObject);
        }
    }

    @Step("<value> json dizisi değerini <mapJsonObjectKey> e ekle <valueType>")
    public void addJsonArray(String value, String mapKey, String valueType) {

        if (BaseTest.TestMap.containsKey(mapKey)) {
            JsonArray jsonArray = (JsonArray) BaseTest.TestMap.get(mapKey);
            methodsUtil.setStepJsonArrayValue(jsonArray, value, valueType);
        } else {
            JsonArray jsonArray = new JsonArray();
            methodsUtil.setStepJsonArrayValue(jsonArray, value, valueType);
            BaseTest.TestMap.put(mapKey, jsonArray);
        }
    }

    @Step("<mapJsonObjectKey> de tutulan json objesini text olarak tut")
    public void saveJsonObjectToString(String mapKey) {

        JsonObject jsonObject = (JsonObject) BaseTest.TestMap.get(mapKey);
        String jsonObjectString = jsonObject.toString();
        logger.info(jsonObjectString);
        BaseTest.TestMap.put(mapKey, jsonObjectString);
    }

    @Step("<mapJsonObjectKey> de tutulan json dizisini text olarak tut")
    public void saveJsonArrayToString(String mapKey) {

        JsonArray jsonArray = (JsonArray) BaseTest.TestMap.get(mapKey);
        String jsonArrayString = jsonArray.toString();
        logger.info(jsonArrayString);
        BaseTest.TestMap.put(mapKey, jsonArrayString);
    }

    @Step("<mapKey> için bearer token ekle")
    public void setBearerToken(String mapKey) {

        setKeyHeaders("Authorization", "Bearer "
                + BaseTest.TestMap.get("token").toString(), mapKey);
    }

    @Step("<mapKey> keyini map ten sil")
    public void removeMapKey(String mapKey) {

        logger.info("" + BaseTest.TestMap.containsKey(mapKey));
        BaseTest.TestMap.remove(mapKey);
        logger.info("" + BaseTest.TestMap.containsKey(mapKey));
    }

    @Step("Map i temizle")
    public void clearMap() {

        BaseTest.TestMap.clear();
        logger.info("" + BaseTest.TestMap.size());
    }

    @Step("<apiMapKey> keyini api map ten sil")
    public void removeApiMapKey(String apiMapKey) {

        logger.info("" + BaseTest.apiMap.containsKey(apiMapKey));
        BaseTest.apiMap.remove(apiMapKey);
        logger.info("" + BaseTest.apiMap.containsKey(apiMapKey));
    }

    @Step("Api map i temizle")
    public void clearApiMap() {

        BaseTest.apiMap.clear();
        logger.info("" + BaseTest.apiMap.size());
    }

    @Step("<number> sayısından rastgele <count> kadar sayıyı listele <mapKey> keyinde tut")
    public void getRamdomNumberList(String number, String count, String mapKey) {

        number = number.endsWith("KeyValue") ? BaseTest.TestMap.get(number).toString() : number;
        count = count.endsWith("KeyValue") ? BaseTest.TestMap.get(count).toString() : count;
        List<Integer> integerList = methodsUtil.getRandomNumberList(Integer.parseInt(number), Integer.parseInt(count));
        BaseTest.TestMap.put(mapKey, integerList);
        BaseTest.TestMap.put("size_" + mapKey, integerList.size());
    }

    @Step("<property> <valueListMapKey> json objesi değeri olarak <mapJsonObjectKeySuffix> e ekle <valueType> loop")
    public void addJsonObjectLoopForArray(String property, String valueListMapKey, String mapJsonObjectKeySuffix, String valueType) {

        boolean trimActive = BaseTest.TestMap.containsKey("trimOptionsKeyValue")
                && BaseTest.TestMap.get("trimOptionsKeyValue").toString().equals("true");
        List<String> list = (List<String>) BaseTest.TestMap.get(valueListMapKey);
        for (int i = 0; i < list.size(); i++) {
            String value = trimActive ? list.get(i).trim() : list.get(i);
            if (BaseTest.TestMap.containsKey(i + mapJsonObjectKeySuffix)) {
                JsonObject jsonObject = (JsonObject) BaseTest.TestMap.get(i + mapJsonObjectKeySuffix);
                methodsUtil.setStepJsonObjectValue(jsonObject, property, value, valueType);
            } else {
                JsonObject jsonObject = new JsonObject();
                methodsUtil.setStepJsonObjectValue(jsonObject, property, value, valueType);
                BaseTest.TestMap.put(i + mapJsonObjectKeySuffix, jsonObject);
            }
        }
    }

    @Step("<mapJsonObjectKeySuffix> json dizisi değerini <mapJsonArrayKey> e ekle <valueType> loop <number>")
    public void setJsonArrayWithJsonObjectLoop(String mapJsonObjectKeySuffix, String mapJsonArrayKey, String valueType, String number) {

        number = number.endsWith("KeyValue") ? BaseTest.TestMap.get(number).toString() : number;
        JsonArray jsonArray = new JsonArray();
        if (valueType.equals("JsonObject")) {
            for (int i = 0; i < Integer.parseInt(number); i++) {
                methodsUtil.setStepJsonArrayValue(jsonArray, i + mapJsonObjectKeySuffix, "JsonObject");
                BaseTest.TestMap.put(mapJsonArrayKey, jsonArray);
                BaseTest.TestMap.remove(i + mapJsonObjectKeySuffix);
            }
        } else {
            List<String> list = (List<String>) BaseTest.TestMap.get(mapJsonObjectKeySuffix);
            for (int i = 0; i < Integer.parseInt(number); i++) {
                methodsUtil.setStepJsonArrayValue(jsonArray, list.get(i), valueType);
                BaseTest.TestMap.put(mapJsonArrayKey, jsonArray);
            }
        }
    }

    @Step("<jsonMapKey> json üzerindeki <jsonPath> json yolunu oku <type> tipindeki degeri <mapKey> keyinde tut")
    public void readJsonPath(String jsonMapKey, String jsonPath, String type, String mapKey) {

        jsonPath = methodsUtil.setValueWithMapKey(jsonPath);
        readJsonMethods.readJsonPath(readJsonMethods.getJsonElementByMap(jsonMapKey)
                , readJsonMethods.getJsonPathAsList(jsonPath, "|?"), type, mapKey);
    }

    @Step("<jsonPath> json path <value> value <type> type ve <id> degerini <mapKey> keyinde tut")
    public void setJsonPathMultipleValue(String jsonPath, String value, String type, String id, String mapKey) {

        setJsonPathMultipleValue(jsonPath, value, type, id, mapKey, "equal", false);
    }

    @Step("<jsonPath> json path <value> value <type> type ve <id> degerini <mapKey> keyinde tut <valueControlType>")
    public void setJsonPathMultipleValue(String jsonPath, String value, String type, String id, String mapKey, String valueControlType) {

        setJsonPathMultipleValue(jsonPath, value, type, id, mapKey, valueControlType, false);
    }

    @Step("<jsonPath> json path <value> value <type> type ve <id> degerini <mapKey> keyinde tut <valueControlType> OrActive <OrActive>")
    public void setJsonPathMultipleValue(String jsonPath, String value, String type, String id, String mapKey, String valueControlType, boolean OrActive) {

        readJsonMethods.setJsonPathMultipleValue(jsonPath, value, type, id, mapKey, valueControlType, OrActive);
    }

    @Step("<jsonPath> json path <value> value <type> type ve <id> degerini <mapKey> keyinde tut <valueControlType> OrActive <OrActive> if <condition>")
    public void setJsonPathMultipleValue(String jsonPath, String value, String type, String id, String mapKey, String valueControlType, boolean OrActive, String condition) {

        boolean reverseCondition = condition.startsWith("!");
        condition = reverseCondition ? condition.substring(1) : condition;
        boolean isTrue = condition.endsWith("KeyValue") ? Boolean.parseBoolean(BaseTest.TestMap.get(condition).toString())
                : Boolean.parseBoolean(condition);
        isTrue = reverseCondition != isTrue;
        if (isTrue) {
            readJsonMethods.setJsonPathMultipleValue(jsonPath, value, type, id, mapKey, valueControlType, OrActive);
        }
    }

    @Step("<jsonMapKey> json üzerinden <jsonPathKey> json degerlerini al ve <mapKeySuffix> keyinde tut")
    public void getMultipleValue(String jsonMapKey, String jsonPathKey, String mapKeySuffix) {

        readJsonMethods.getMultipleValue(jsonPathKey, readJsonMethods.getJsonElementByMap(jsonMapKey), "|?", mapKeySuffix);
    }

    @Step("<jsonMapKey> json listesi üzerinden <controlJsonPathKey> varsa degerleri kontrol et <jsonPathKey> json degerlerini listele ve <mapKeySuffix> keyinde tut")
    public void getMultipleListValue(String jsonMapKey, String controlJsonPathKey, String jsonPathKey, String mapKeySuffix) {

        readJsonMethods.getMultipleListValue(controlJsonPathKey, jsonPathKey
                , readJsonMethods.getJsonElementByMap(jsonMapKey), "|?", mapKeySuffix);
    }

    @Step("<jsonString> json textini <fileLocation> dosya yoluna yaz")
    public void writeJson(String jsonString, String fileLocation) {

        String json = readJsonMethods.getJsonElementByMap(jsonString).toString();
        methodsUtil.writeJson(json, fileLocation, true, true, false);
    }

    @Step("<mapJsonKey> json liste sayısını <mapKey> degerinde tut")
    public void getJsonListSize(String mapJsonKey, String mapKey) {

        int i = readJsonMethods.getJsonListSize((JsonArray) BaseTest.TestMap.get(mapJsonKey));
        BaseTest.TestMap.put(mapKey, i);
        logger.info("JsonListSize: " + i);
    }

    @Step("")
    public void readJsonPath() {

    }

    @Step("<listMapKey> listesinin elemanlarını key olarak <listMapNames> listelerini value olarak <mapKeySuffix> keyinde tut <valueTrim>")
    public void setMapWithList(String listMapKey, String listMapNames, String mapKeySuffix, boolean valueTrim) {

        List<String> keyList = new ArrayList<>((List<String>) BaseTest.TestMap.get(listMapKey.trim()));
        List<String> mapValueList = new ArrayList<>();
        List<List<String>> valuesList = new ArrayList<>();
        List<String> cacheKeyList = new ArrayList<>();
        String key = "";
        for (String listName : listMapNames.split(",")) {
            valuesList.add((List<String>) BaseTest.TestMap.get(listName.trim()));
        }
        for (int i = 0; i < keyList.size(); i++) {

            mapValueList = new ArrayList<>();
            for (List<String> list : valuesList) {
                mapValueList.add(valueTrim ? list.get(i).trim() : list.get(i));
            }
            key = keyList.get(i).trim() + mapKeySuffix;
            cacheKeyList.add(key);
            BaseTest.TestMap.put(key, mapValueList);
        }
        BaseTest.TestMap.put("cacheKeyList" + mapKeySuffix, cacheKeyList);
    }

    @Step("<jsonPath> json pathi <id> degeri ile <mapKey> keyinde tut")
    public void setJsonPathMultipleValue2(String jsonPath, String id, String mapKey) {

        setJsonPathMultipleValue(jsonPath, "", "String", id, mapKey, "equal", false);
    }

}
