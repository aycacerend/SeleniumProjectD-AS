package com.testinium.driver;

import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

public class BaseTest {
    public static WebDriver driver;
    @Before
    public void setUp() {
        System.out.println("Basta Calıstım");
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-translate");
        chromeOptions.addArguments("disable-popup-blocking");
        chromeOptions.addArguments("--disable-notifications");

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browser", "Chrome 11");
        caps.setCapability("os", "Windows");
        caps.merge(caps);


        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(15,TimeUnit.SECONDS);
        driver.get("https://www.kitapyurdu.com");
    }

    @After
    public void tearDown(){
        System.out.println("Sonda Çalıştım");
        if(driver !=null){
            driver.close();
            driver.quit();
        }
    }
}