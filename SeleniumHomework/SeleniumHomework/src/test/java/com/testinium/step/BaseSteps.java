package com.testinium.step;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.testinium.base.BaseTest;
import com.testinium.model.ElementInfo;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class BaseSteps extends BaseTest {

    private static HashMap<String, String> dataList = new HashMap<>();
    ArrayList<String> array = new ArrayList<>();
    StringBuilder sb = null;
    Request request = null;
    RequestSpecification httpRequest = given();
    OkHttpClient client = null;
    String token = "Bearer ";

    private List<String> saveList = new ArrayList<>();

    private Map<String, List<String>> saveListMap = new HashMap<>();
    public void storeData(String key, String value) {
        dataList.put(key, value);

    }

    public String getData(String key) {
        return dataList.get(key);
    }

    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;


    private static String SAVED_ATTRIBUTE;

    private String compareText;
    private static int sayi;


    public BaseSteps() {
        initMap(getFileList());
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, 60);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }

    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    private void clickElementBy(String key) {
        findElement(key).click();
    }

    private void hoverElement(WebElement element) {
        actions.moveToElement(element).build().perform();
    }

    private void hoverElementBy(String key) {
        WebElement webElement = findElement(key);
        actions.moveToElement(webElement).build().perform();
    }

    private void sendKeyESC(String key) {
        findElement(key).sendKeys(Keys.ESCAPE);

    }

    private boolean isDisplayed(WebElement element) {
        return element.isDisplayed();
    }

    private boolean isDisplayedBy(By by) {
        return driver.findElement(by).isDisplayed();
    }

    private String getPageSource() {
        return driver.switchTo().alert().getText();
    }

    public static String getSavedAttribute() {
        return SAVED_ATTRIBUTE;
    }

    public String randomString(int stringLength) {

        Random random = new Random();
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUWVXYZabcdefghijklmnopqrstuwvxyz0123456789".toCharArray();
        String stringRandom = "";
        for (int i = 0; i < stringLength; i++) {

            stringRandom = stringRandom + chars[random.nextInt(chars.length)];
        }

        return stringRandom;
    }

    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    public String getElementText(String key) {
        return findElement(key).getText();
    }

    public String getElementAttributeValue(String key, String attribute) {
        return findElement(key).getAttribute(attribute);
    }

    @Step("Print page source")
    public void printPageSource() {
        System.out.println(getPageSource());
    }

    public void javaScriptClicker(WebDriver driver, WebElement element) {

        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", element);
    }

    public void javascriptclicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    @Step({"Wait <value> seconds",
            "<int> saniye bekle"})
    public void waitBySeconds(int seconds) {
        try {
            logger.info(seconds + " saniye bekleniyor.");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            logger.info(milliseconds + " milisaniye bekleniyor.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step({"Click to element <key>",
            "<key> elementine tiklanir"})
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
        }
    }

    @Step({"Frame <number> e geç ve <key> elementine tıkla"})
    public void clickElements(int number, String key) {
        driver.switchTo().frame(number);
        clickElement(findElement(key));
        driver.switchTo().defaultContent();
    }


    @Step("<key> elementin üstünde bekle")
    public void hover(String key) {
        hoverElement(findElement(key));
    }

    @Step({"Click to element <key> with focus",
            "<key> elementine focus ile tıkla"})
    public void clickElementWithFocus(String key) {
        actions.moveToElement(findElement(key));
        actions.click();
        actions.build().perform();
        logger.info(key + " elementine focus ile tıklandı.");
    }


    @Step("<key> elementini kontrol et")
    public void checkElement(String key) {
        assertTrue(findElement(key).isDisplayed(), "Aranan element bulunamadı");
    }

    @Step({"Go to <url> address",
            "<url> adresine git"})
    public void goToUrl(String url) {
        driver.get(url);
        logger.info(url + " adresine gidiliyor.");
    }

    @Step({"Wait for element to load with id <id>",
            "Elementin yüklenmesini bekle id <id>"})
    public void waitElementLoadWithId(String id) {
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(By.cssSelector(id)).size() > 0) {
                logger.info(id + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element: '" + id + "' doesn't exist.");
    }

    @Step({"Wait for element to load with xpath <xpath>",
            "Elementinin yüklenmesini bekle xpath <xpath>"})
    public void waitElementLoadWithXpath(String xpath) {
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(By.xpath(xpath)).size() > 0) {
                logger.info(xpath + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element: '" + xpath + "' doesn't exist.");
    }

    @Step({"Check if element <key> exists else print message <message>",
            "Element <key> var mi kontrol et yoksa hata mesaji ver <message>"})
    public void getElementWithKeyIfExistsWithMessage(String key, String message) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                logger.info(key + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(message);
    }

    @Step({"Check if element <key> not exists",
            "<key> element yok mu kontrol et"})
    public void checkElementNotExists(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() == 0) {
                logger.info(key + " elementinin olmadığı kontrol edildi.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element '" + key + "' still exist.");
    }

    @Step({"Check if element <key> exists, click element",
            "Element var mı kontrol et <key>, varsa tıkla"})
    public void checkElementExistsAndClick(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);
        System.out.println("********************Dongu Oncesi********************");
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            System.out.println("********************If oncesi********************");
            if (driver.findElements(by).size() > 0) {
                System.out.println("********************If icerisi********************");
                driver.findElement(by).click();
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element '" + key + "' not exist.");
    }

    @Step({"Upload file in project <path> to element <key>",
            "Proje içindeki <path> dosyayı <key> elemente upload et"})
    public void uploadFile(String path, String key) {
        String pathString = System.getProperty("user.dir") + "/";
        pathString = pathString + path;
        findElement(key).sendKeys(pathString);
        logger.info(path + " dosyası " + key + " elementine yüklendi.");
    }

    @Step({"Write value <text> to element <key>",
            "<text> textini <key> elemente yaz"})
    public void ssendKeys(String text, String key) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazıldı.");
        }
    }

    @Step({"Click with javascript to css <css>",
            "Javascript ile css tıkla <css>"})
    public void javascriptClickerWithCss(String css) {
        assertTrue(isDisplayedBy(By.cssSelector(css)), "Element bulunamadı");
        javaScriptClicker(driver, driver.findElement(By.cssSelector(css)));
        logger.info("Javascript ile " + css + " tıklandı.");
    }

    @Step({"Click with javascript to xpath <xpath>",
            "Javascript ile xpath tıkla <xpath>"})
    public void javascriptClickerWithXpath(String xpath) {
        assertTrue(isDisplayedBy(By.xpath(xpath)), "Element bulunamadı");
        javaScriptClicker(driver, driver.findElement(By.xpath(xpath)));
        logger.info("Javascript ile " + xpath + " tıklandı.");
    }

    @Step({"Check if current URL contains the value <expectedURL>",
            "Şuanki URL <url> değerini içeriyor mu kontrol et"})
    public void checkURLContainsRepeat(String expectedURL) {
        int loopCount = 0;
        String actualURL = "";
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            actualURL = driver.getCurrentUrl();

            if (actualURL != null && actualURL.contains(expectedURL)) {
                logger.info("Suanki URL" + expectedURL + " degerini iceriyor.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(
                "Actual URL doesn't match the expected." + "Expected: " + expectedURL + ", Actual: "
                        + actualURL);
    }

    @Step({"Send TAB key to element <key>",
            "Elemente TAB keyi yolla <key>"})
    public void sendKeyToElementTAB(String key) {
        findElement(key).sendKeys(Keys.TAB);
        logger.info(key + " elementine TAB keyi yollandı.");
    }

    @Step({"Send BACKSPACE key to element <key>",
            "Elemente BACKSPACE keyi yolla <key>"})
    public void sendKeyToElementBACKSPACE(String key) {
        findElement(key).sendKeys(Keys.BACK_SPACE);
        logger.info(key + " elementine BACKSPACE keyi yollandı.");
    }

    @Step({"Send ESCAPE key to element <key>",
            "Elemente ESCAPE keyi yolla <key>"})
    public void sendKeyToElementESCAPE(String key) {
        findElement(key).sendKeys(Keys.ESCAPE);
        logger.info(key + " elementine ESCAPE keyi yollandı.");
    }

    @Step({"Check if element <key> has attribute <attribute>",
            "<key> elementi <attribute> niteliğine sahip mi"})
    public void checkElementAttributeExists(String key, String attribute) {
        WebElement element = findElement(key);
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (element.getAttribute(attribute) != null) {
                logger.info(key + " elementi " + attribute + " niteliğine sahip.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element DOESN't have the attribute: '" + attribute + "'");
    }

    @Step({"Check if element <key> not have attribute <attribute>",
            "<key> elementi <attribute> niteliğine sahip değil mi"})
    public void checkElementAttributeNotExists(String key, String attribute) {
        WebElement element = findElement(key);

        int loopCount = 0;

        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (element.getAttribute(attribute) == null) {
                logger.info(key + " elementi " + attribute + " niteliğine sahip olmadığı kontrol edildi.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element STILL have the attribute: '" + attribute + "'");
    }

    @Step({"Check if <key> element's attribute <attribute> equals to the value <expectedValue>",
            "<key> elementinin <attribute> niteliği <value> değerine sahip mi"})
    public void checkElementAttributeEquals(String key, String attribute, String expectedValue) {
        WebElement element = findElement(key);

        String actualValue;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            actualValue = element.getAttribute(attribute).trim();
            if (actualValue.equals(expectedValue)) {
                logger.info(
                        key + " elementinin " + attribute + " niteliği " + expectedValue + " değerine sahip.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element's attribute value doesn't match expected value");
    }

    @Step({"Check if <key> element's attribute <attribute> contains the value <expectedValue>",
            "<key> elementinin <attribute> niteliği <value> değerini içeriyor mu"})
    public void checkElementAttributeContains(String key, String attribute, String expectedValue) {
        WebElement element = findElement(key);

        String actualValue;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            actualValue = element.getAttribute(attribute).trim();
            if (actualValue.contains(expectedValue)) {
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element's attribute value doesn't contain expected value");
    }

    @Step({"Write <value> to <attributeName> of element <key>",
            "<value> değerini <attribute> niteliğine <key> elementi için yaz"})
    public void setElementAttribute(String value, String attributeName, String key) {
        String attributeValue = findElement(key).getAttribute(attributeName);
        findElement(key).sendKeys(attributeValue, value);
    }

    @Step({"Write <value> to <attributeName> of element <key> with Js",
            "<value> değerini <attribute> niteliğine <key> elementi için JS ile yaz"})
    public void setElementAttributeWithJs(String value, String attributeName, String key) {
        WebElement webElement = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('" + attributeName + "', '" + value + "')",
                webElement);
    }

    @Step({"Clear text of element <key>",
            "<key> elementinin text alanını temizle"})
    public void clearInputArea(String key) {
        findElement(key).clear();
    }

    @Step({"Clear text of element <key> with BACKSPACE",
            "<key> elementinin text alanını BACKSPACE ile temizle"})
    public void clearInputAreaWithBackspace(String key) {
        WebElement element = findElement(key);
        element.clear();
        element.sendKeys("a");
        actions.sendKeys(Keys.BACK_SPACE).build().perform();
    }

    @Step({"Save attribute <attribute> value of element <key>",
            "<attribute> niteliğini sakla <key> elementi için"})
    public void saveAttributeValueOfElement(String attribute, String key) {
        SAVED_ATTRIBUTE = findElement(key).getAttribute(attribute);
        System.out.println("Saved attribute value is: " + SAVED_ATTRIBUTE);
    }

    @Step({"Write saved attribute value to element <key>",
            "Kaydedilmiş niteliği <key> elementine yaz"})
    public void writeSavedAttributeToElement(String key) {
        findElement(key).sendKeys(SAVED_ATTRIBUTE);
    }

    @Step({"Check if element <key> contains text <expectedText>",
            "<key> elementi <text> degerini iceriyor mu kontrol et"})
    public void checkElementContainsText(String key, String expectedText) {

        Boolean containsText = findElement(key).getText().trim().contains(expectedText);
        System.out.println("Elementin text degeri:" + findElement(key).getText().trim());
        System.out.println(key + " elementi " + expectedText + " degerini iceriyor.");
        assertTrue(containsText, "Expected text contained");
        logger.info(key + " elementi " + expectedText + " degerini iceriyor.");
    }

    @Step({"Check if element <key> contains text saved as <saveKey>",
            "<key> elementi <saveKey> olarak kaydedilmis degeri iceriyor mu kontrol et"})
    public void checkElementContainsSavedText(String key, String saveKey) {
        String savedText = getValue(saveKey);
        if (savedText != null) {
            Boolean containsText = findElement(key).getText().trim().contains(savedText);
            System.out.println("Elementin text degeri: " + findElement(key).getText().trim());
            System.out.println(key + " elementi '" + savedText + "' degerini iceriyor mu: " + containsText);
            assertTrue(containsText, "Expected text contained");
            logger.info(key + " elementi '" + savedText + "' degerini iceriyor.");
        } else {
            System.out.println("Hafizada kaydedilmis deger bulunamadi: " + saveKey);
            assertTrue(false, "Saved value not found in memory");
            logger.error("Hafizada kaydedilmis deger bulunamadi: " + saveKey);
        }
    }


    @Step({"Check if element <key> contains text <expectedText>",
            "<key> elementi <text> değerini içermediğini kontrol et"})
    public void checkElementContainsTextt(String key, String expectedText) {

        Boolean containsText = findElement(key).getText().contains(expectedText);
        assertFalse(containsText, "Expected text is not contained");
        logger.info(key + " elementi" + expectedText + "değerini içermiyor.");
    }

    @Step({"Write random value to element <key>",
            "<key> elementine random deger yaz"})
    public void writeRandomValueToElement(String key) {
        findElement(key).sendKeys(randomString(15));
    }

    @Step({"Write random value to element <key> starting with <text>",
            "<key> elementine <text> değeri ile başlayan random değer yaz"})
    public void writeRandomValueToElement(String key, String startingText) {
        String randomText = startingText + randomString(15);
        findElement(key).sendKeys(randomText);
    }

    @Step({"Print element text by css <css>",
            "Elementin text değerini yazdır css <css>"})
    public void printElementText(String css) {
        System.out.println(driver.findElement(By.cssSelector(css)).getText());
    }

    @Step({"Write value <string> to element <key> with focus",
            "<string> değerini <key> elementine focus ile yaz"})
    public void sendKeysWithFocus(String text, String key) {
        actions.moveToElement(findElement(key));
        actions.click();
        actions.sendKeys(text);
        actions.build().perform();
        logger.info(key + " elementine " + text + " değeri focus ile yazıldı.");
    }

    @Step({"Refresh page",
            "Sayfa refresh edilir"})
    public void refreshPage() {
        driver.navigate().refresh();
        logger.info("Sayfa refresh edildi");
    }


    @Step({"Change page zoom to <value>%",
            "Sayfanın zoom değerini değiştir <value>%"})
    public void chromeZoomOut(String value) {
        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        jsExec.executeScript("document.body.style.zoom = '" + value + "%'");
    }

    @Step({"Open new tab",
            "Yeni sekme aç"})
    public void chromeOpenNewTab() {
        ((JavascriptExecutor) driver).executeScript("window.open()");
    }

    @Step({"Focus on tab number <number>",
            "<number> numaralı sekmeye odaklan"})//Starting from 1
    public void chromeFocusTabWithNumber(int number) {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(number - 1));
    }

    @Step("popupa gec")
    public void switchTo() {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    @Step({"Focus on last tab",
            "Son sekmeye odaklan"})
    public void chromeFocusLastTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
    }

    @Step({"Focus on frame with <key>",
            "Frame'e odaklan <key>"})
    public void chromeFocusFrameWithNumber(String key) {
        WebElement webElement = findElement(key);
        driver.switchTo().frame(webElement);
    }

    @Step({"Accept Chrome alert popup",
            "Chrome uyarı popup'ını kabul et"})
    public void acceptChromeAlertPopup() {
        driver.switchTo().alert().accept();
    }

    @Step({"Dismiss Chrome alert popup",
            "Chrome uyarı popup'ını reddet"})
    public void dismissChromeAlertPopup() {
        driver.switchTo().alert().dismiss();
        logger.info("Popup iptal butonuna tıklandı");
    }


    // Key değeri alınan listeden rasgele element seçme amacıyla yazılmıştır.
    public void randomPick(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = random.nextInt(elements.size());
        elements.get(index).click();
    }

    //Javascript driverın başlatılması
    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }

    //Javascript scriptlerinin çalışması için gerekli fonksiyon
    private Object executeJS(String script, boolean wait) {
        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }

    //Belirli bir locasyona sayfanın kaydırılması
    private void scrollTo(int x, int y) {
        String script = String.format("window.scrollTo(%d, %d);", x, y);
        executeJS(script, true);
    }

    //Belirli bir elementin olduğu locasyona websayfasının kaydırılması
    public WebElement scrollToElementToBeVisible(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement webElement = driver.findElement(getElementInfoToBy(elementInfo));
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
        }
        return webElement;
    }


    @Step({"<key> alanına kaydir"})
    public void scrollToElement(String key) {
        scrollToElementToBeVisible(key);
        logger.info(key + " elementinin olduğu alana kaydırıldı");

    }


    @Step({"<key> alanina js ile kaydır"})
    public void scrollToElementWithJs(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement element = driver.findElement(getElementInfoToBy(elementInfo));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    @Step({"<length> uzunlugunda random bir kelime uret ve <saveKey> olarak sakla"})
    public void createRandomString(int length, String saveKey) {
        saveValue(saveKey, randomString(length));

    }

    @Step({"<key> li elementi bul ve değerini <saveKey> saklanan degeri yazdir",
            "Find element by <key> and compare saved key <saveKey>"})
    public void equalsSendTextByKey(String key, String saveKey) throws InterruptedException {
        WebElement element;
        int waitVar = 0;
        element = findElementWithKey(key);
        while (true) {
            if (element.isDisplayed()) {
                logger.info("WebElement is found at: " + waitVar + " second.");
                element.clear();
                getValue(saveKey);
                element.sendKeys(getValue(saveKey));
                break;
            } else {
                waitVar = waitVar + 1;
                Thread.sleep(1000);
                if (waitVar == 20) {
                    throw new NullPointerException(String.format("by = %s Web element list not found"));
                } else {
                }
            }
        }
    }

    //Zaman bilgisinin alınması
    private Long getTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return (timestamp.getTime());
    }

    @Step({"<key> li elementi bul, temizle ve rasgele  email değerini yaz",
            "Find element by <key> clear and send keys  random email"})
    public void RandomMail(String key) {
        Long timestamp = getTimestamp();
        WebElement webElement = findElementWithKey(key);
        webElement.clear();
        webElement.sendKeys("testotomasyon" + timestamp + "@sahabt.com");

    }

    @Step("<key> drop down menuden <text> secimi yapilirr")
    public void choosingTextFromListr(String key, String text) throws InterruptedException {
        List<WebElement> comboBoxElement = findElements(key);
        for (int i = 0; i < comboBoxElement.size(); i++) {
            String texts = comboBoxElement.get(i).getText();
            String textim = text;
            if (texts.contains(textim)) {
                comboBoxElement.get(i).click();
                break;
            }
        }
        logger.info(key + " comboboxından " + text + " değeri seçildi");
    }

    @Step("<key> drop down menuden <text> secimi yapilir")
    public void comboboxvalue(String key, String text) throws InterruptedException {
        List<WebElement> comboBoxElement = findElements(key);
        for (int i = 0; i < comboBoxElement.size(); i++) {
            String texts = comboBoxElement.get(i).getText();
            String textim = text;
            if (texts.contains(textim)) {
                comboBoxElement.get(i).click();
                break;
            }
        }
        logger.info(key + " comboboxından " + text + " değeri seçildi");

    }

    @Step("<key> drop down menusunden herahangi bir secim yapilir ve tiklanir")
    public void comboBoxRandom(String key) throws InterruptedException {

        List<WebElement> comboBoxElement = findElements(key);
        int randomIndex = 0;
        for (int i = 1; i < comboBoxElement.size(); i++) {

            randomIndex = new Random().nextInt(comboBoxElement.size());
            Thread.sleep(2000);
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", comboBoxElement.get(randomIndex));
            System.out.println(randomIndex);
        }
        logger.info(comboBoxElement.get(randomIndex) + " comboboxından herhangi bir değer seçildi ");
    }


    @Step("<key> elementine javascript ile tıkla")
    public void clickToElementWithJavaScript(String key) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile tıklandı");
    }


    // Belirli bir key değerinin olduğu locasyona websayfasının kaydırılması
    public void scrollToElementToBeVisiblest(WebElement webElement) {
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
        }
    }


    //Çift tıklama fonksiyonu
    public void doubleclick(WebElement elementLocator) {
        Actions actions = new Actions(driver);
        actions.doubleClick(elementLocator).perform();
    }

    @Step("<key> alanını javascript ile temizle")
    public void clearWithJS(String key) {
        WebElement element = findElement(key);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value ='';", element);
    }

    @Step("<key> elementleri arasından <text> kayıtlı değişkene tıkla")
    public void clickParticularElement(String key, String text) {
        List<WebElement> anchors = findElements(key);
        Iterator<WebElement> i = anchors.iterator();
        while (i.hasNext()) {
            WebElement anchor = i.next();
            if (anchor.getText().contains(getValue(text))) {
                scrollToElementToBeVisiblest(anchor);
                doubleclick(anchor);
                break;
            }
        }
    }

    @Step("<key> menu listesinden rasgele seç")
    public void chooseRandomElementFromList(String key) {
        for (int i = 0; i < 3; i++)
            randomPick(key);
    }


    @Step("<key> olarak <index> indexi seçersem")
    public void choosingIndexFromDemandNo(String key, String index) {

        try {
            TimeUnit.SECONDS.sleep(3);

            List<WebElement> anchors = findElements(key);
            WebElement anchor = anchors.get(Integer.parseInt(index));
            anchor.click();
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step("Siparis durmununu <kartDurumu> elementinden bul")
    public void findOrderStatus(String kartDurumu) throws InterruptedException {
        WebElement webElement = findElement(kartDurumu);
        logger.info(" webelement bulundu");
        compareText = webElement.getText();
        logger.info(compareText + " texti bulundu");
    }

    @Step("<key> elementiyle karsilastir")
    public void compareOrderStatus(String key) throws InterruptedException {
        WebElement cardDetail = findElement(key);
        String supplyDetailStatus = cardDetail.getText();
        logger.info(supplyDetailStatus + " texti bulundu");
        assertTrue(compareText.equals(supplyDetailStatus));
        logger.info(compareText + " textiyle " + supplyDetailStatus + " texti karşılaştırıldı.");
    }

    @Step("<text> textini <key> elemente tek tek yaz")
    public void sendKeyOneByOne(String text, String key) throws InterruptedException {

        WebElement field = findElement(key);
        field.clear();
        if (!key.equals("")) {
            for (char ch : text.toCharArray())
                findElement(key).sendKeys(Character.toString(ch));
            Thread.sleep(10);
            logger.info(key + " elementine " + text + " texti karakterler tek tek girlilerek yazıldı.");
        }
    }

    @Step("<key> elementine <text> değerini js ile yaz")
    public void writeToKeyWithJavaScript(String key, String text) {
        WebElement element = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value=arguments[1]", element, text);
        logger.info(key + " elementine " + text + " değeri js ile yazıldı.");
    }


    //Bugünün Tarihinin seçilmesi
    public String chooseDate() {
        Calendar now = Calendar.getInstance();
        int tarih = now.get(Calendar.DATE) + 2;
        return String.valueOf(tarih);
    }

    @Step("<key> tarihinden 2 gün sonraya al")
    public void chooseTwoDaysFromNow(String key) {
        List<WebElement> elements = findElements(key);
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getText().equals(chooseDate())) {
                elements.get(i).click();
            }
        }
    }

    @Step("<variable> değişkenini <key> elementine yaz")
    public void sendKeysVariable(String variable, String key) {
        if (!key.equals("")) {
            clearInputArea(key);
            findElement(key).sendKeys(getValue(variable));
            logger.info(key + " elementine " + getValue(variable) + " texti yazıldı.");
        }
    }


    @Step("<key> drop down menusunden <text> secimi yapilir ve tiklanir")
    public void selectDropDown(String key, String text) {
        Select drpCountry = new Select(findElement(key));
        drpCountry.selectByVisibleText(text);
        logger.info(key + " drop down menusunden " + text + " secimi yapilir ve tiklanir");
    }

    @Step("<key> li comboboxu aç ve ilk elementi seç")
    public void openComboboxAndSelect(String key) {
    }

    @Step("<key> olarak seçimini yap")
    public void randomPick2(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = random.nextInt(elements.size());
        elements.get(index).click();
    }

    @Step("Diğer sekmeye geç")
    public void switchToPage() {

        for (String curWindow : driver.getWindowHandles()) {
            driver.switchTo().window(curWindow);
        }
    }

    @Step("İkinci sekmeye geç")
    public void switchToPage2() {
        String parentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String curWindow : allWindows) {
            driver.switchTo().window(curWindow);
        }
    }

    @Step("Aşağıya scroll et")
    public void scrollDownOrUp() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,500)");
    }

    @Step("Aşağıya scroll et2")
    public void scrollDownOrUp2() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,300)");
    }

    @Step("Aşağıya scroll et1000")
    public void scrollDownOrUp3() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,500)");
    }

    @Step("Aşağıya scroll et10001")
    public void scrollDownOrUp4() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,1000)");
    }

    @Step({"Send ENTER key to element <key>",
            "Elemente ENTER keyi yolla <key>"})
    public void sendKeyToElementENTER(String key) {
        findElement(key).sendKeys(Keys.ENTER);
        logger.info(key + " elementine ENTER keyi yollandı.");
    }

    @Step("<key> elementin asagi ok tusuna tiklanir")
    public void sendDownElement(String key) {
        findElement(key).sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
        logger.info(key + "elementine asagi ok islemi yapildi ");
    }

    @Step("<key> li elementin text degerini hafizada kayitli olan <saveKey> degeriyle karsilastir")
    public void compareRuleSet(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertTrue(elementTextValue.equalsIgnoreCase(getValue(saveKey)));
    }

    @Step("<key> li elementin <attribute> degerini hafizada kayitli olan <saveKey> degeriyle karsilastir")
    public void compareRuleSett(String key, String attribute, String saveKey) {
        String elementTextValue = findElement(key).getAttribute(attribute);
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertTrue(elementTextValue.equalsIgnoreCase(getValue(saveKey)));
    }

    @Step("<key> li elementin text degerini hafizada kayitli olan <saveKey> degeriyle esit degil mi karsilastir")
    public void compareRuleSettt(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertFalse(elementTextValue.equalsIgnoreCase(getValue(saveKey)));
        System.out.println("Degerler esit degil");
    }



    @Step("<key> li elementin text degerini listedeki <saveKey> degerler ile karsilastir")
    public void compareRuleSetWithList2(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        List<String> tempList = saveListMap.get(saveKey);
        if (tempList != null) {
            for (String item : tempList) {
                System.out.println("Listede bulunan deger: " + item);
                if (elementTextValue.contains(item)) {
                    System.out.println("Listedeki değer elementin metni içeriyor: " + item);
                } else {
                    System.out.println("Hata: Listede bulunan değer elementin metni içermiyor: " + item);
                    assertTrue(false, "Listede bulunan değer elementin metni içermiyor: " + item);
                }
            }
            System.out.println("Listede tüm değerler başarıyla bulundu.");
        } else {
            System.out.println("Hata: " + saveKey + " anahtarıyla ilişkilendirilmiş bir liste bulunamadı.");
            assertTrue(false, saveKey + " anahtarıyla ilişkilendirilmiş bir liste bulunamadı.");
        }
    }


    @Step("<key> li elementin text degerini listedeki degerler ile karsilastir")
    public void compareRuleSetWithList(String key) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        for (int i = 0; i < saveList.size(); i++) {
            System.out.println(i + " indexindeki text degeri: " + saveList.get(i));
            assertTrue(elementTextValue.contains(saveList.get(i)));
        }
    }

    @Step("<key> li elementin text degerini listedeki degerler ile karsilastirma")
    public void compareRuleSetWithList3(String key) {
        List<WebElement> elements = findElements(key);
        System.out.println("Keyli elementin text degerleri:");
        for (WebElement element : elements) {
            String elementTextValue = element.getText();
            System.out.println(elementTextValue);
            assertTrue(saveList.contains(elementTextValue));
        }
        System.out.println("Tüm listedeki değerler başarıyla kontrol edildi.");
    }

    @Step("<key> li elementin text degerini hafizada kayitli olan <saveKey> degerini iceriyor mu kontrol et")
    public void compareRuleSet2(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        Assertions.assertTrue(getValue(saveKey).contains(elementTextValue));
    }


    @Step("<key> li elementin text degerini hafizadaki <saveKey> degerini iceriyor mu kontrol et")
    public void compareRuleSett2(String key, String saveKey) {
        List<WebElement> elements = findElements(key);
        StringBuilder allElementTexts = new StringBuilder();
        for (WebElement element : elements) {
            allElementTexts.append(element.getText().trim()).append(" ");
        }
        String allTextInElements = allElementTexts.toString().trim();
        String savedText = getValue(saveKey);
        System.out.println("Tüm elementlerin text degerleri : " + allTextInElements);
        System.out.println("Hafizada saklanan deger : " + savedText);
        boolean found = allTextInElements.contains(savedText);
        Assertions.assertTrue(found, "Saved text value is contained in element texts");
    }


    @Step("<key> li elementin text degeri hafizaya <saveKey> ismiyle kaydet")
    public void saveTextValueOfElement(String key, String saveKey) {
        String saveValueText = findElement(key).getText();
        System.out.println("Elementin text degeri: " + saveValueText);
        saveValue(saveKey, saveValueText);
    }

    // public static final String BASE_URL = "https://api.spotify.com/v1/";
    // public static final String BASE_URL = "https://testinium.io/Testinium.RestApi/api/";
    public static final String BASE_URL = "http://gckus2253.ford.com.tr/testdataapi";

    @Step("<key> li elementin text degeri map e <mapKey> ismiyle kaydet")
    public void saveTextValueOfElement2(String key, String saveKey) {
        String saveValueText = findElement(key).getText();
        System.out.println("Elementin text degeri: " + saveValueText);
        BaseTest.TestMap.put(saveKey, saveValueText);
    }

    @Step("specsheet <key> li text değerini <mapkey> kaydet")
    public void splitt(String key, String saveKey) {
        String saveValueText = findElement(key).getText();
        System.out.println("Elementin text degeri: " + saveValueText);
        BaseTest.TestMap.put(saveKey, saveValueText);
        String test = (String) BaseTest.TestMap.get(saveKey);
        String[] arrOfStr = test.split("\\r?\\n", 2);
        BaseTest.TestMap.put(saveKey, arrOfStr[1]);
        System.out.println(arrOfStr[1] + " Değeri alındı");
    }


    @Step("<key> drop down menunun tab sayısı ve tum textleri getirilir")
    public void saveDropDownMenu(String key) {
        WebElement selectElement = findElement(key);
        Select list = new Select(selectElement);
        String selectedOption = list.getFirstSelectedOption().getText();
        System.out.println(selectedOption);

        List<WebElement> options = list.getOptions();
        System.out.println("options.size(): " + options.size());
        for (WebElement option : options) {
            System.out.println(option.getText());
        }


    }

    @Step("<key> grid viewdeki satır sayısı bulunur")
    public void gridViewFindingNumberOfRows(String key) {
        List<WebElement> rowsNumber = findElements(key);
        int rowCount = rowsNumber.size();
        System.out.println("Grid viewdeki satır sayısı: " + rowCount);
        sayi = rowCount;

    }

    @Step("<key> grid viewdeki satır sayısı <expectedRowCount> e eşit mi kontrol edilir")
    public void gridViewRowCountVerification(String key, int expectedRowCount) {
        List<WebElement> rowsNumber = findElements(key);
        int actualRowCount = rowsNumber.size();
        System.out.println("Grid viewdeki satır sayısı: " + actualRowCount);
        Assertions.assertEquals(expectedRowCount, actualRowCount,"Grid viewdeki satır sayısı beklenen değerle eşleşmiyor!");
    }


    @Step("KPS katalog uzunlugu icin olusturulan menudeki <numberOfLines> secimine gore sayfalandirma butonlarinin ve son sayfadaki KPS sayisinin kontrolu")
    public void paginationButton(int numberOfLines) {
        int numberPages = 0;
        int numberOfElementsOnTheLastPage = 0;
        logger.info("Sayfadaki satir sayisi : " + numberOfLines);
        if (sayi > 0) {
            numberOfElementsOnTheLastPage = sayi % numberOfLines;

            if (numberOfElementsOnTheLastPage != 0)
                numberPages = (sayi / numberOfLines) + 1;
            else
                numberPages = sayi / numberOfLines;

        }

        String numberPageStr = findElement("geriBildirimSonSayfalandirmaButonu").getText();

        int elementNumbersInt = Integer.parseInt(numberPageStr);

        logger.info("Otomasyonla bulunan sayfalandirma butonu sayisi: " + numberPages);
        logger.info("Web arayuzunden alinan sayfalandirma butonu sayisi: " + elementNumbersInt);

        assertTrue(numberPages == elementNumbersInt);

        clickElement("geriBildirimSonSayfalandirmaButonu");
        waitBySeconds(5);
        List<WebElement> elementNumbers = findElements("sonSayfaElemanSayisiTxt");

        logger.info("Otomasyonla bulunan son sayfadaki satir sayisi : " + numberOfElementsOnTheLastPage);
        logger.info("Web arayuzunden alinan son sayfadaki satir sayisi:  " + elementNumbers.size());

        assertTrue(numberOfElementsOnTheLastPage == elementNumbers.size());

    }


    @Step("<numberOfLines> secimine gore <sonSayfalandirmaButonu> ve <kayit> sayisinin kontrolu")
    public void paginationButtonTanimEkranlari(int numberOfLines, String sonSayfalandirmaButonu, String kayit) {
        int numberPages = 0;
        int numberOfElementsOnTheLastPage = 0;
        logger.info("Sayfadaki satir sayisi : " + numberOfLines);
        if (sayi == 10) {
            numberOfElementsOnTheLastPage = 10;
            if (numberOfElementsOnTheLastPage != 0)
                numberPages = (sayi / numberOfLines);
        } else if (sayi > 0) {
            numberOfElementsOnTheLastPage = (sayi % numberOfLines);

            if (numberOfElementsOnTheLastPage != 0)
                numberPages = (sayi / numberOfLines) + 1;

            else
                numberPages = sayi / numberOfLines;
        }
        String numberPageStr = findElement(sonSayfalandirmaButonu).getText();

        int elementNumbersInt = Integer.parseInt(numberPageStr);

        logger.info("Otomasyonla bulunan sayfalandirma butonu sayisi: " + numberPages);
        logger.info("Web arayuzunden alinan sayfalandirma butonu sayisi: " + elementNumbersInt);

        assertTrue(numberPages == elementNumbersInt);
        clickElement("acilirYanMenuİkonu");
        clickElement(sonSayfalandirmaButonu);
        waitBySeconds(5);
        List<WebElement> elementNumbers = findElements(kayit);
        logger.info("Otomasyonla bulunan son sayfadaki satir sayisi : " + numberOfElementsOnTheLastPage);
        logger.info("Web arayuzunden alinan son sayfadaki satir sayisi:  " + elementNumbers.size());

        assertTrue(numberOfElementsOnTheLastPage == elementNumbers.size());

    }

    @Step("<key> drop down menusunden <value> secimi yapilir ve tiklanirr")
    public void selectDropDownValue(String key, int value) {
        Select drpCountryy = new Select(findElement(key));
        drpCountryy.selectByIndex(value);
    }


    @Step("<key> drop down menusundeki <selectedText> seciminin gorunurlugunun kontrolu")
    public void checkTheSelectedDropDown(String key, String selectedText) {

        Select select = new Select(findElement(key));
        WebElement option = select.getFirstSelectedOption();
        String optionText = option.getText();
        logger.info("Secilen elementin text degeri: " + optionText);

        assertTrue(optionText.equals(selectedText));
        logger.info(optionText + " textiyle " + selectedText + " texti karsılaştırıldı.");
    }

    @Step("<key> li elementin textt degerini hafizada kayıtlı olan <saveKey> degeriyle harfe duyarli karsilastir")
    public void compareRuleSet22(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertTrue(elementTextValue.equalsIgnoreCase(getValue(saveKey)));
    }

    @Step("<key> check box seciminin gorunurluk kontrolu")
    public void verifyCheckBoxSelection(String key) {

        WebElement checkBoxx = findElement(key);
        Assertions.assertEquals(true, checkBoxx.isSelected());

        logger.info(key + "check box secili durumda");
    }

    @Step("<key> aktif mi kontrol et")
    public void aktiflikkontrolu(String key) {

        WebElement secilili = findElement(key);
        Assertions.assertEquals(true, secilili.isDisplayed());

        logger.info(key + "etkin");
    }

    @Step("Olusturulma tarihi ve saati <saveKey> olarak kaydedilir")
    public void tarihVeSaatKaydetme(String saveKey) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        String date1 = dateFormat.format(date);
        logger.info("Olusturulma tarihi ve saati: " + date1);
        saveValue(saveKey, date1);
    }

    @Step("<key> elementine tiklandiginda olusturulma tarihi ve saati <saveKey> olarak kaydedilir")
    public void tiklandigindeSaatVeTarihGetirme(String key, String saveKey) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        String date1 = dateFormat.format(date);
        logger.info("Olusturulma tarihi: " + date1);
        saveValue(saveKey, date1);
    }

    @Step("<key> li elemente tiklandiginda olusturulma tarihi ve saati <saveKey> olarak kaydedilir")
    public void tiklandigindeSaatVeTarihGetirme2(String key, String saveKey) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date();
        String date1 = dateFormat.format(date);
        logger.info("Olusturulma tarihi: " + date1);
        saveValue(saveKey, date1);
    }

    @Step("<key> li elemente tiklandiginda olusturulma tarihi ve saati <saveKey> olarak hafizaya kaydedilir")
    public void tiklandigindeSaatVeTarihGetirme3(String key, String saveKey) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        String date1 = dateFormat.format(date);
        logger.info("Olusturulma tarihi: " + date1);
        saveValue(saveKey, date1);
    }

    @Step("<key> elementine tiklandiginda olusturulma tarihini <saveKey> olarak kaydedilir")
    public void tiklandigindeTarihiniGetirme(String key, String saveKey) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile tıklandı");

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String date1 = dateFormat.format(date);
        logger.info("Olusturulma tarihi: " + date1);
        saveValue(saveKey, date1);
    }

    @Step("<key> elementine tiklandiginda olusturulma saatini <saveKey> olarak kaydedilir")
    public void tiklandigindeSaatiniGetirme(String key, String saveKey) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile tıklandı");

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String date1 = dateFormat.format(date);
        logger.info("Olusturulma saati: " + date1);
        saveValue(saveKey, date1);
    }

    @Step("<key> drop down menununn tab sayısı ve tum textleri kaydedilir <saveKey>")
    public void saveDropDownMenukaydetme(String key, String saveKey) {
        List<WebElement> rowsNumber = findElements(key);
        int rownCount = rowsNumber.size();
        System.out.println("No of columns in this table : " + rownCount);
        int rownum;
        rownum = 1;
        for (WebElement tdElement : rowsNumber) {
            System.out.println(", col # " + rownum + "text=" + tdElement.getText());
            rownum++;

        }
        saveValue(saveKey, getElementText(key));
    }

    @Step("<key> textinden <cikarilacakİfade> ifadesi cikarilir ve <saveKey> olarak hafizada saklanir")
    public void stringBolme(String key, String cikarilacakİfade, String saveKey) {
        String str = findElement(key).getText();
        String split[] = str.split(cikarilacakİfade);
        for (String s : split) {
            System.out.println("Islem sonucunda elde edilen text" + s);
            logger.info("Islem sonucunda elde edilen text" + s);
        }
        saveValue(saveKey, split[1]);
    }

    @Step("<key> li elementin textinden <cikarilacakİfade> cikarilir ve degeri hafizada kayıtlı olan <saveKey> degeriyle harfe duyarli karsilastir")
    public void savekeySplit(String key, String cikarilacakİfade, String saveKey) {
        String str = findElement(key).getText();
        String split[] = str.split(cikarilacakİfade);
        for (String s : split) {
            System.out.println("Islem sonucunda elde edilen text" + s);
            logger.info("Islem sonucunda elde edilen text" + s);

            System.out.println("Keyli elementin text degeri : " + s);
            System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
            assertTrue(s.equalsIgnoreCase(getValue(saveKey)));
        }
    }

    @Step("<key> li elementin textinde parantezler varsa cikar ve <saveKey> degeri ile karsilastir")
    public void idDegeriEsitmiKontrol(String key, String saveKey) {
        String str = findElement(key).getText();
        if (str.contains("(") && str.contains(")")) {
            str = str.replace(")", "");
            str = str.replace("(", "");
        }
        String hafizayaAlinanDeger = getValue(saveKey);
        assertEquals(hafizayaAlinanDeger, str, "Degerler birbirine esit değil");
        System.out.println("Hafizada saklanan elementin text degeri: " + hafizayaAlinanDeger);
        System.out.println("Elementin text degeri :" + str);
        logger.info(hafizayaAlinanDeger + " ile " + str + " birbirine esit! ");
    }

    @Step("<key> li elementin textinde [OK] ifadesini cikar ve <saveKey> degeri ile karsilastir")
    public void DegerlerEsitmiKontrol(String key, String saveKey) {
        String str = findElement(key).getText();
        if (str.contains("[OK]")) {
            str = str.replace("[OK]", "");
        }
        String hafizayaAlinanDeger = getValue(saveKey);
        assertEquals(hafizayaAlinanDeger, str, "Degerler birbirine esit değil");
        System.out.println("Hafizada saklanan elementin text degeri: " + hafizayaAlinanDeger);
        System.out.println("Elementin text degeri :" + str);
        logger.info(hafizayaAlinanDeger + " ile " + str + " birbirine esit! ");
    }


    @Step("Grid viewdeki <key> satirlarinin tum textleri hafizaya <saveKey> adinda kaydedilir")
    public void saveGridView(String key, String saveKey) {
        List<WebElement> gridView = findElements(key);
        for (WebElement tdElement : gridView) {
            System.out.println(tdElement.getText());
        }
        saveValue(saveKey, getElementText(key));
    }

    @Step("Grid viewdeki <key> satirlarinin tum textleri listeye <saveKey> kaydet")
    public void saveGridToList2(String key, String saveKey) {
        List<WebElement> gridView = findElements(key);
        List<String> tempList = new ArrayList<>();
        for (WebElement tdElement : gridView) {
            tempList.add(tdElement.getText());
            System.out.println(tdElement.getText());
        }
        saveListMap.put(saveKey, tempList);
    }

    @Step("Grid viewdeki <key> satirlarinin tum textleri listeye kaydet")
    public void saveGridToList(String key) {
        List<WebElement> gridView = findElements(key);
        for (WebElement tdElement : gridView) {
            saveList.add(tdElement.getText());
            System.out.println(tdElement.getText());
        }
    }

    @Step("<key> drop down menununn tab sayısı ve tum textleri kaydedilirr <saveKey>")
    public void saveDropDownMenukaydetmeee(String key, String saveKey) {
        List<WebElement> rowsNumber = findElements(key);
        int rownCount = rowsNumber.size();
        System.out.println("No of columns in this table : " + rownCount);
        int rownum;
        rownum = 1;
        for (WebElement tdElement : rowsNumber) {
            System.out.println(", col # " + rownum + "text=" + tdElement.getText());
            rownum++;

        }
        BaseTest.TestMap.put(saveKey, getElementText(key));
    }

    Map<String, String> headers = new HashMap<>();
    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    JSONObject jObject = null;
    Response response = null;

    @Step("<key> keyli elementin <attribute> degerini hafizaya <saveKey> ismiyle kaydet")
    public void saveTextValueOfElementtt(String key, String attribute, String saveKey) {
        String saveValueText = findElement(key).getAttribute(attribute);
        System.out.println("Elementin text degeri: " + saveValueText);
        saveValue(saveKey, saveValueText);
    }

    @Step("<key> elementine tiklanir ve <key2> <saveKey> olarakk kaydedilir")
    public void tiklandigindeSaatVeTarihGetirmeeee(String key, String key2, String saveKey) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
        }

        String saveValueText = findElement(key2).getText();
        System.out.println("Elementin text degeri: " + saveValueText);
        saveValue(saveKey, saveValueText);
    }

    @Step("JS ile <vin> vin numarası, <second> encoder saniyesi,<station> station numarasıyla sorgula")
    public void sendJsScriptForRequest(String vin, String second, String station) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("$.connection.encoderDataHub.client.addNewMessageToPage('" + vin + "'," + second + "," + station + ");");
    }

    @Step("<kuratSetleri> kural setlerini <kuralSetiBilgisi> bilgilerini al ve <apiKurakSetiResponse> ismiyle kontrol et")
    public void kuralSetiListeleme(String kuratSetleri, String kuralSetiBilgisi, String apiKurakSetiResponse) {
        List<WebElement> kuralSetiList = findElements(kuratSetleri);
        List<WebElement> kuralSetiBilgisiList = findElements(kuralSetiBilgisi);
        int kuralSetiSayisiHesaplama = kuralSetiList.size();
        System.out.println("Kural Setindeki Eleman Sayisi: " + kuralSetiSayisiHesaplama);
        for (int i = 0; i < kuralSetiSayisiHesaplama; i++) {
            String kuralSeti = kuralSetiList.get(i).getText().trim();
            String kuralSetiBilgileri = kuralSetiBilgisiList.get(i).getText().trim();
            logger.info(" Kural Seti: " + kuralSeti);
            logger.info(" Kural Seti Bilgileri: " + kuralSetiBilgileri);
            Assertions.assertTrue(BaseTest.TestMap.containsKey(kuralSeti + apiKurakSetiResponse), kuralSeti + " Kural seti apiden dogrulanamadi...");
            String kuralSetiBilgisiApi = ((List<String>) BaseTest.TestMap.get(kuralSeti + apiKurakSetiResponse)).get(0);
            //Assertions.assertEquals(kuralSetiBilgisiApi, kuralSetiBilgileri);
            Assertions.assertTrue(kuralSetiBilgileri.contains(kuralSetiBilgisiApi));
        }
    }

    @Step("<kuratSetleri> kural setlerini ve bilgilerini al ve <apiKurakSetiResponse> ismiyle kontrol et")
    public void kuralSetiListeleme(String kuratSetleri, String apiKurakSetiResponse) {
        List<WebElement> kuralSetiList = findElements(kuratSetleri);
        int kuralSetiSayisiHesaplama = kuralSetiList.size();
        System.out.println("Kural Setindeki Eleman Sayisi: " + kuralSetiSayisiHesaplama);
        for (int i = 0; i < kuralSetiSayisiHesaplama; i++) {
            String kuralSeti = kuralSetiList.get(i).getAttribute("value").trim();
            logger.info(" Kural Seti: " + kuralSeti);
            Assertions.assertTrue(BaseTest.TestMap.containsKey(kuralSeti + apiKurakSetiResponse), kuralSeti + " Kural seti apiden dogrulanamadi...");
            String kuralSetibilgisiApi = ((List<String>) BaseTest.TestMap.get(kuralSeti + apiKurakSetiResponse)).get(0);
            //Assertions.assertEquals(kuralSetiBilgisiApi, kuralSetiBilgileri);
            Assertions.assertTrue(kuralSeti.contains(kuralSetibilgisiApi));
        }
    }


    @Step("<buyOff> BuyOfflarini ve <buyOffBilgileri> bilgilerini al ve <apiBuyOffResponse> ismiyle kontrol et")
    public void buyOffListeleme(String buyOff, String buyOffBilgileri, String apiBuyOffResponse) {
        List<WebElement> buyOffList = findElements(buyOff);
        List<WebElement> buyOffBilgisiList = findElements(buyOffBilgileri);
        int buyOffSayisiHesaplama = buyOffList.size();
        System.out.println("BuyOff Eleman Sayisi: " + buyOffSayisiHesaplama);
        for (int i = 0; i < buyOffSayisiHesaplama; i++) {
            String buyOfff = buyOffList.get(i).getText().trim();
            String buyOffBilgilerii = buyOffBilgisiList.get(i).getText().trim();
            logger.info(" BuyOff: " + buyOfff);
            logger.info(" BuyOff Bilgileri: " + buyOffBilgilerii);
            Assertions.assertTrue(BaseTest.TestMap.containsKey(buyOfff + apiBuyOffResponse), buyOfff + "BuyOfflar apiden dogrulanamadi...");
            String buyOffBilgisiApi = ((List<String>) BaseTest.TestMap.get(buyOfff + apiBuyOffResponse)).get(0);
            Assertions.assertEquals(buyOffBilgisiApi, buyOffBilgilerii);
        }
    }

    @Step("<buyOff> BuyOfflarini al ve <apiBuyOffResponse> ismiyle kontrol et")
    public void buyOffListeleme(String buyOff, String apiBuyOffResponse) {
        List<WebElement> buyOffList = findElements(buyOff);
        int buyOffSayisiHesaplama = buyOffList.size();
        System.out.println("BuyOff Eleman Sayisi: " + buyOffSayisiHesaplama);
        for (int i = 0; i < buyOffSayisiHesaplama; i++) {
            String buyOfff = buyOffList.get(i).getText().trim();
            logger.info(" BuyOff: " + buyOfff);
            Assertions.assertTrue(BaseTest.TestMap.containsKey(buyOfff + apiBuyOffResponse), buyOfff + "BuyOfflar apiden dogrulanamadi...");
            String buyOffBilgisiApi = ((List<String>) BaseTest.TestMap.get(buyOfff + apiBuyOffResponse)).get(0);
        }
    }

    @BeforeScenario
    public void before() {
        RestAssured.baseURI = BASE_URL;
    }


    @Step("Jobject Oluştur")
    public void createJObject() {
        jObject = new JSONObject();
        System.out.print("Yeni bir JObject Olusturuldu");
    }

    @Step("<key> key ve <value> value degerini JObjecte ekle")
    public void addToRequestBody(String key, String value) {
        jObject.put(key, value);
        System.out.print("JObject'e " + key + ":" + value + " degeri eklendi");
    }


    @Step("<api> apiye <type> methoduyla istek at")
    public void setApi(String api, String type) {
        System.out.println(RestAssured.baseURI + RestAssured.basePath + " servisine " + type + " istegi atildi");
        if (type.equals("post")) {
            response = RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .body(jObject.toString())
                    .post(api);
        } else if (type.equals("put")) {
            response = RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .body(jObject.toString())
                    .put(api);
        } else if (type.equals("get")) {
            response = RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .queryParam(jObject.toString())
                    .get(api);
        } else if (type.equals("delete")) {
            response = RestAssured.given().headers(headers)
                    .contentType(ContentType.JSON)
                    .body(jObject.toString())
                    .delete(api);
        } else {
            System.out.println("Lütfen geçerli bir deger giriniz");
        }
        System.out.println("Request:" + jObject.toString());
        System.out.println("Response:" + response.getBody().asString());
    }


    @Step("status kod <statusCode> ile ayni mi kontrol et")
    public void checkStatusCode2(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCode(), "Status code eslesmiyor...");
        System.out.print("status kod hata kodu " + response.getStatusCode() + " ile ayni mi kontrol edildi");
    }

    @Step("response <key> alanı <value> iceriyor mu kontrol et")
    public void checkResponseMethod(String key, String value) {
        if (value.contains("null")) {
            Assertions.assertNull(response.jsonPath().get(key), key + " değeri null değil");
        } else {
            Assertions.assertTrue(value.equals(response.jsonPath().get(key).toString()), value + " degeri ile " + response.jsonPath().getString(key) + " degeri uyusmuyor");
        }
        System.out.print("response " + key + " alani  " + value + " iceriyor mu kontrol edildi");
    }

    @Step("response <key> keyinin degerini <hashmapKey> olarak kaydet")
    public void responseHashmap(String key, String hashmapKey) {
        hashMap.put(hashmapKey, response.jsonPath().get(key).toString().trim());
        System.out.println("response " + key + " keyinin degerini " + response.jsonPath().get(key).toString().trim() + " olarak kaydedildi");
    }


    @Step("<key> keyine hashmapdeki <value> keyli değeri JObjecte ekle")
    public void AddElementHashmapToRest(String key, String value) {
        jObject.put(key, hashMap.get(value).toString());
        System.out.print("JObject'e " + key + ":" + hashMap.get(value).toString() + " degeri eklendi");
    }


    @Step("<key> key <value> value degerini headera ekle")
    public void addHeader(String key, String value) {
        headers.put(key, value);
        System.out.print("Header'a " + key + "," + value + " degeri eklendi");
    }

    @Step("<key> keyli <value> degeri hashmap'e ekle")
    public void addHashmapManuel(String key, String value) {
        hashMap.put(key, value);
        System.out.print(key + " keyli " + value + " degeri manuel olarak hashmap'e eklendi");
    }


    @Step("Hashmapin icindeki <hashmapKey> keyinin degeri <hashmapKey1> keyinin degeri ile <type> mı kontrol et")
    public void checkDifferenceHashmap(String hashmapKey, String hashmapKey1, String type) {
        if ("aynı".equals(type)) {
            Assertions.assertEquals(hashMap.get(hashmapKey).toString(), hashMap.get(hashmapKey1).toString(), "hashmapteki degerler eslesmiyor...");
            System.out.println(hashMap.get(hashmapKey).toString() + " , " + hashMap.get(hashmapKey1).toString() + " ile " + type + "mi kontrol edildi");
        } else if ("farklı".equals(type)) {
            Assertions.assertNotEquals(hashMap.get(hashmapKey).toString(), hashMap.get(hashmapKey1).toString(), "hashmapteki degerler eslesmiyor...");
            System.out.println(hashMap.get(hashmapKey).toString() + " , " + hashMap.get(hashmapKey1).toString() + " ile " + type + " mi kontrol edildi");
        } else {
            Assertions.fail("Lütfen Gecerli bir tip giriniz");
        }
    }


    @Step("<key> li elementin text degeri hasmap e <mapKey> ismiyle kaydet")
    public void saveTextValueOfElement3(String key, String saveKey) {
        String saveValueText = findElement(key).getText();
        System.out.println("Elementin text degeri: " + saveValueText);
        hashMap.put(saveKey, saveValueText);
    }

    @Step("<key> elementinin icindeki degerleri <hashmapKey> ismiyle hafizaya kaydet")
    public void apiIleListeKarsilastirma(String key, String hashmapKey) {
        List<WebElement> saveValueText = findElements(key);
        for (int i = 0; i < saveValueText.size(); i++) {
            String saveValueTexts = saveValueText.get(i).getText().trim();
            System.out.println("Kaydedilen liste  :  " + saveValueTexts);
            hashMap.put(hashmapKey, saveValueTexts);
            Assertions.assertTrue(hashMap.containsKey(saveValueTexts + hashmapKey), saveValueTexts + "apiden dogrulanamadi");
            String saveValueTextApi = ((List<String>) hashMap.get(saveValueTexts + hashmapKey)).get(0);
            Assertions.assertEquals(saveValueTextApi, saveValueTexts);
        }
    }

    @Step("<key> datalist menusune <text> yazilir ve <attribute> attribute niteligi <key2> iceren eleman secilir")
    public void comboboxOptionSelect(String key, String text, String attribute, String key2) {
        WebElement element = findElement(key);
        element.clear();
        element.sendKeys(text);

        String optionValue = findElement(key2).getAttribute(attribute);
        element.clear();
        element.sendKeys(optionValue);
    }

    @Step("<key> elementine hover yap")
    public void elementHover(String key) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
        }
    }


    @Step({"Check if element <key> exists",
            "Element var mı kontrol et <key>"})
    public WebElement getElementWithKeyIfExists(String key) {
        WebElement webElement;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            try {
                webElement = findElementWithKey(key);
                logger.info(key + " elementi bulundu.");
                return webElement;
            } catch (WebDriverException e) {
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        assertFalse(Boolean.parseBoolean("Element: '" + key + "' doesn't exist."));
        return null;
    }

    @Step({"Wait for element to load with css <css>",
            "Elementin yüklenmesini bekle css <css>"})
    public void waitElementLoadWithCss(String css) {
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(By.cssSelector(css)).size() > 0) {
                logger.info(css + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element: '" + css + "' doesn't exist.");
    }

    @Step("sekmeye odaklan deneme")
    public void sekmeOdakDeneme() {
        String firstWindow = driver.getWindowHandle();
        System.out.println("First Window" + firstWindow);
        Set<String> windows = driver.getWindowHandles();
        Iterator<String> iterator = windows.iterator();
        while (iterator.hasNext()) {
            String secondWindow = iterator.next();
            if (!firstWindow.equalsIgnoreCase(secondWindow)) {
                driver.switchTo().window(secondWindow);
                System.out.println("Second Window" + secondWindow);
            }
        }

    }

    @Step("All Window Handles")
    public void AllWindow() {
        String firstWindow = driver.getWindowHandle();
        System.out.println("First Window" + firstWindow);
        Set<String> windows = driver.getWindowHandles();
        Iterator<String> iterator = windows.iterator();
        while (iterator.hasNext()) {
            String secondWindow = iterator.next();
            if (!firstWindow.equalsIgnoreCase(secondWindow)) {
                driver.switchTo().window(secondWindow);
                System.out.println("Second Window" + secondWindow);
            }

        }
    }

    @Step("Current Page handle")
    public void oneHandle() {
        driver.getWindowHandle();
        System.out.println("Current Page Handle:" + driver.getWindowHandle());
    }


    @Step("Specsheet Buyoff status degeri 200 oldugu kontrol edilir")
    public void buyoffStatus200Control() {
        List<LogEntry> logEntryList = driver.manage().logs().get(LogType.BROWSER).getAll();
        //List<LogEntry> logEntryList = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        for (int i = 0; i < logEntryList.size(); i++) {
            String a = logEntryList.get(i).toString();
            //https://testdfab.ford.com.tr/SpecSheet/AddBuyoffResponse
            if (a.contains("AddBuyoffResponse") && a.contains("responseReceived")) {
                System.out.println(a);
                System.out.println(logEntryList.get(i).toJson().get("status"));
                new Methods().writeJson(logEntryList.get(i).toJson().toString(), "./BuyoffResponse.json", true, true, false);
                JsonElement jsonElement = JsonParser.parseString(logEntryList.get(i).toJson().toString());
                int status = jsonElement.getAsJsonObject().get("message").getAsJsonObject().get("message")
                        .getAsJsonObject().get("params").getAsJsonObject().get("response").getAsJsonObject().get("status").getAsInt();
                System.out.println(status);
                assertEquals(200, status);
            }
        }
    }

    @Step("Specsheet OK status degeri 200 oldugu kontrol edilir")
    public void okStatus200Control() {

        List<LogEntry> logEntryList = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        for (int i = 0; i < logEntryList.size(); i++) {
            String a = logEntryList.get(i).toString();
            //https://testdfab.ford.com.tr/SpecSheet/AddSideOkResponse
            if (a.contains("AddSideOkResponse") && a.contains("responseReceived")) {
                System.out.println(a);
                System.out.println(logEntryList.get(i).toJson().get("status"));
                new Methods().writeJson(logEntryList.get(i).toJson().toString(), "./denemee.json", true, true, false);
                JsonElement jsonElement = JsonParser.parseString(logEntryList.get(i).toJson().toString());
                int status = jsonElement.getAsJsonObject().get("message").getAsJsonObject().get("message")
                        .getAsJsonObject().get("params").getAsJsonObject().get("response").getAsJsonObject().get("status").getAsInt();
                System.out.println(status);
                assertEquals(200, status);
            }
        }
    }

    @Step("<keyValue> den cekilen datayi <data> de degistirerek <fileName> dosyasina yazdir")
    public void printDataByReplacingInFile(String keyValue, String data, String fileName) {

        String data1 = getValue(data);
        JSONObject jsonObject = null;
        try {
            FileReader fileReader = new FileReader("./" + fileName + ".json");
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            jsonObject = new JSONObject(jsonTokener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            // JSON içeriğinde değişiklik yapın
            jsonObject.remove(keyValue);
            jsonObject.put(keyValue, data1);

            // JSON dosyasına değişiklikleri kaydedin
            try {
                FileWriter fileWriter = new FileWriter("./" + fileName + ".json");
                fileWriter.write(jsonObject.toString());
                fileWriter.close();
                System.out.println("Değişiklikler JSON dosyasına kaydedildi.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Step("Onceki sayfaya geri don")
    public void backToPreviousPage() {
        driver.navigate().back();
        System.out.println("Önceki sayfaya geri dönüldü");
    }

    @Step("<key>li datalist menusunun <attribute> nitelikli tum degerleri listelenir")
    public void deneme(String key, String attribute) {
        List<WebElement> datalistElements = findElements(key);

        System.out.println("Tum datalist degerleri:");
        for (WebElement datalistElement : datalistElements) {
            String valueAttribute = datalistElement.getAttribute(attribute);
            System.out.println(valueAttribute);
        }
}

    @Step("Hafizadaki <rowNumber>. satiri yazdir")
    public void printSavedRowByNumber(int rowNumber) {
        int rowIndex = rowNumber - 1;  // Satır numarası 0'dan başladığı için bir çıkartma yapılır
        if (rowIndex >= 0 && rowIndex < saveList.size()) {
            System.out.println(saveList.get(rowIndex));
        } else {
            System.out.println("Hafizada " + rowNumber + ". satir bulunamadi.");
        }
    }



}
