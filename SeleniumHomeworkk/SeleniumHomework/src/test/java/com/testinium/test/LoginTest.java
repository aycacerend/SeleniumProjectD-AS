package com.testinium.test;

import com.testinium.driver.BaseTest;
import com.testinium.methods.Methods;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

public class LoginTest extends BaseTest {
    @Test
    public void LoginTest(){
        Methods methods= new Methods();
        methods.click(By.xpath("//div[@class='menu-top-button login']"));
        methods.waitBySeconds(3);
        methods.sendKeys(By.id("login-email"),"aycaceren26@gmail.com");
        methods.waitBySeconds(5);
        methods.sendKeys(By.id("login-password"),"AyCa6842");
        methods.waitBySeconds(5);
        methods.click(By.cssSelector(".ky-btn.ky-btn-orange.w-100.ky-login-btn"));
        methods.waitBySeconds(10);
        String basariliGiris = methods.getText(By.cssSelector(".common-sprite"));
        Assert.assertEquals("Merhaba Ayça Ceren Dinçer", basariliGiris );
        System.out.println("Yazı : "+ basariliGiris);
        methods.waitBySeconds(3);
        methods.sendKeys(By.id("search-input"),"oyuncak");
        methods.click(By.cssSelector(".common-sprite.button-search"));
        methods.scrollWithAction(By.xpath("//img[@src=\"https://img.kitapyurdu.com/v1/getImage/fn:4856384/wi:100/wh:true\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//div[@id='product-table']/div[3]//i[@class='fa fa-heart']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//div[@id='product-table']/div[4]//i[@class='fa fa-heart']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//div[@id='product-table']/div[5]//i[@class='fa fa-heart']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//div[@id='product-table']/div[6]//i[@class='fa fa-heart']"));
        methods.waitBySeconds(3);
        String favoriKontrol = methods.getText(By.xpath("//h2[@class='swal2-title ky-swal-title-single']"));
        Assert.assertEquals("Ürün başarılı bir şekilde favorilerinize eklendi!", favoriKontrol );
        System.out.println("Yazı : "+ favoriKontrol);
        methods.waitBySeconds(3);
        methods.click(By.xpath("//img[@src=\"https://img.kitapyurdu.com/v1/getImage/fn:11682842/wh:dec2d77ad\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@href=\"puan-katalogu\"]"));
        methods.waitBySeconds(3);
        methods.scrollWithAction(By.id("point-carousel-tab-title-952"));
        methods.waitBySeconds(5);
        methods.click(By.xpath("//img[@title=\"Puan Kataloğundaki Türk Klasikleri\"]"));
        methods.waitBySeconds(5);
        methods.click(By.xpath("//div[@class='sort']/select[1]"));
        methods.waitBySeconds(3);
        methods.selectByText(By.xpath("//select[@onchange=\"location = this.value;\"]"),"Yüksek Oylama" );
        methods.waitBySeconds(3);
        methods.click(By.xpath("//span[.=\"Tüm Kitaplar\"] "));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@href=\"kategori/kitap-hobi/1_212.html\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//img[@src=\"https://img.kitapkurdu.com/v1/getImage/fn:62170/wi:100:wh:true\"]"));
        methods.waitBySeconds(3);
        methods.click(By.cssSelector(".add-to-cart.btn-orange.btn-ripple"));
        methods.waitBySeconds(3);
        methods.scrollWithAction(By.xpath("//td[text()=\"Yayın Tarihi:\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@id=\"button-cart\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@class=\"common-sprite\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@href=\"https://www.kitapyurdu.com/index.php?route=account/favorite&selected_tags=0\"]"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//i[@class='fa fa-heart-o']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//div[@id='cart']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@href='https://www.kitapyurdu.com/index.php?route=checkout/cart']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@style='width:22px']"));
        methods.waitBySeconds(3);
        methods.findElement(By.xpath("//input[@style='width:22px']")).clear();//adet siler
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@style='width:22px']"));
        methods.waitBySeconds(3);
        methods.sendKeys(By.name("quantity"),"10");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//i[@onclick='cartProductUpdate($(this).parent())']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@href='https://www.kitapyurdu.com/index.php?route=checkout/checkout']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='address-firstname-companyname']"));
        methods.sendKeys(By.name("firstname-companyname"),"Ayça Ceren");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='address-lastname-title']"));
        methods.sendKeys(By.name("lastname_title"),"Dinçer");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//select[@id='address-zone-id']"));
        methods.waitBySeconds(3);
        methods.selectByText(By.xpath("//select[@id='address-zone-id=this.value;']"),"İstanbul");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//select[@id='address-country-id']"));
        methods.waitBySeconds(3);
        methods.selectByText(By.xpath("//select[@id='address-country-id=this.value;']"),"ATAŞEHİR");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='district']"));
        methods.waitBySeconds(3);
        methods.selectByText(By.xpath("//input[@id='district']"),"KAYIŞDAĞI MAH");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//textarea[@id='address-address-text']"));
        methods.waitBySeconds(3);
        methods.sendKeys(By.name("address"),"Çakmakçı sokak 12/9");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='address-mobile-telephone']"));
        methods.sendKeys(By.name("mobile-telephone"),"5446277042");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//button[@id='button-checkout-continue']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//button[@id='button-checkout-continue']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='credit-card-owner']"));
        methods.sendKeys(By.name("credit_card_owner"),"Ayça Ceren Dinçer");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='credit_card_number_1']"));
        methods.sendKeys(By.name("credit_card_number_1"),"4544627387283934");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//select[@id='credit-card-expire-date-month']"));
        methods.waitBySeconds(3);
        methods.selectByText(By.xpath("//select[@id='credit-card-expire-date-month']"),"08");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//select[@id='credit-card-expire-date-year']"));
        methods.waitBySeconds(3);
        methods.selectByText(By.xpath("//select[@id='credit-card-expire-date-month']"),"2026");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@id='credit-card-security-code']"));
        methods.waitBySeconds(2);
        methods.sendKeys(By.name("credit_card_security_code"),"453");
        methods.waitBySeconds(3);
        methods.click(By.xpath("//button[@id='button-checkout-continue']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@type='checkbox']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//input[@type='submit']"));
        methods.waitBySeconds(3);
        String hataliBilgiler = methods.getText(By.xpath("//div[@class='warning']"));
        Assert.assertEquals("Kart bilgileriniz doğrulanmadı, lütfen tekrar deneyiniz", hataliBilgiler );
        System.out.println("Yazı : "+ hataliBilgiler);
        methods.waitBySeconds(2);
        methods.click(By.xpath("//img[@src='https://img.kitapyurdu.com/v1/getImage/fn:11596679/wh:57b377137']"));
        methods.waitBySeconds(3);
        methods.click(By.xpath("//a[@href='https://www.kitapyurdu.com/index.php?route=account/logout']"));
























    }
}