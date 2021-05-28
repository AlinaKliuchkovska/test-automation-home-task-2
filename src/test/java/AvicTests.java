import com.google.common.collect.Ordering;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.Keys.ENTER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
        wait = new WebDriverWait(driver, 30);
    }

    @Test(priority = 1)
    public void checkThatPasswordChangeWorksCorrect() {
        logIn("123456789");

        driver.findElement(xpath("//div[contains(@class,'header-bottom__right')]//a[contains(@href,'/user-profile')]")).click();
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(xpath("//a[contains(@href,'changePassword')]"))).click().perform();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_changePassword")));
        driver.findElement(xpath("//input[@name='old_password']")).sendKeys("123456789", ENTER);
        driver.findElement(xpath("//input[@name='password']")).sendKeys("1234567890", ENTER);
        driver.findElement(xpath("//input[@name='password_confirmation']")).sendKeys("1234567890", ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAlert")));
        driver.findElement(xpath("//button[contains(@class,'fancybox-close')]")).click();
        driver.findElement(xpath("//a[contains(@href,'/logout')]")).click();

        logIn("1234567890");
        assertTrue(driver.getTitle().contains("AVIC"));

        driver.findElement(xpath("//div[contains(@class,'header-bottom__right')]//a[contains(@href,'/user-profile')]")).click();
        actions.moveToElement(driver.findElement(xpath("//a[contains(@href,'changePassword')]"))).click().perform();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_changePassword")));
        driver.findElement(xpath("//input[@name='old_password']")).sendKeys("1234567890", ENTER);
        driver.findElement(xpath("//input[@name='password']")).sendKeys("123456789", ENTER);
        driver.findElement(xpath("//input[@name='password_confirmation']")).sendKeys("123456789", ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAlert")));
        driver.findElement(xpath("//button[contains(@class,'fancybox-close')]")).click();
        driver.findElement(xpath("//a[contains(@href,'/logout')]")).click();
    }

    @Test(priority = 2)
    public void checkThatElementsSortedInReverseOrder() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        driver.findElement(xpath("//li[contains(@class, 'sidebar')]//a[contains(@href, '/gadzhetyi1')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']//a[contains(@href,'/kvadrokopteryi')]")).click();
        Select sortingTypeDropdown = new Select(driver.findElement(xpath("//div[@class='category-top']//select[contains(@class, 'sort')]")));
        sortingTypeDropdown.selectByVisibleText("За зменшенням");
        List<WebElement> priceOfElementsListWebElements = driver.findElements(xpath("//div[@class='prod-cart__prise-new']"));
        List<String> priceOfElementsListString = new ArrayList<>();
        for (WebElement price : priceOfElementsListWebElements) {
            priceOfElementsListString.add(price.getText().replace("грн", ""));
        }
        assertTrue(Ordering.natural().reverse().isOrdered(priceOfElementsListString));
    }


    @Test(priority = 3)
    public void checkThatUserDataIsDisplayedInTheCart() throws InterruptedException {
        logIn("123456789");

        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        driver.findElement(xpath("//li[contains(@class, 'sidebar')]//a[contains(@href, '/gadzhetyi1')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']//a[contains(@href,'/kvadrokopteryi')]")).click();

        Thread.sleep(2000);
        wait.until(ExpectedConditions.elementToBeClickable(xpath("//a[@class='prod-cart__buy']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("js_cart")));
        driver.findElement(xpath("//a[contains(@href,'/checkout')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath("//input[@name='name']")));
        assertEquals(driver.findElement(xpath("//input[@name='name']")).getAttribute("value"), "Test");
        assertEquals(driver.findElement(xpath("//input[@name='phone']")).getAttribute("value"), "+38(098) 749 03 15");
        assertEquals(driver.findElement(xpath("//input[@name='email']")).getAttribute("value"), "test.kliuchkovska@gmail.com");

        driver.get("https://avic.ua/");
        driver.findElement(xpath("//div[contains(@class, 'cart active-cart')]")).click();
        driver.findElement(xpath("//i[@class='icon icon-close js-btn-close']")).click();
        driver.get("https://avic.ua/");
        wait.until(ExpectedConditions.elementToBeClickable(xpath("//div[contains(@class,'header-bottom__right')]//a[contains(@href,'/user-profile')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(xpath("//a[contains(@href,'/logout')]"))).click();
    }

    @Test(priority = 4)
    public void testOnlyAvailableItemCheckBox() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        driver.findElement(xpath("//li[contains(@class, 'sidebar')]//a[contains(@href, '/gadzhetyi1')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']//a[contains(@href,'/kvadrokopteryi')]")).click();
        driver.findElement(xpath("//label[contains(text(), 'Тільки товари у наявності')]")).click();
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("scroll(0, 250);");
        List<WebElement> pagesOfAvailableItems = driver.findElements(xpath("//li[@class='page-item']"));
        pagesOfAvailableItems.get(pagesOfAvailableItems.size() - 1).click();
        List<WebElement> onlyAvailableItems = driver.findElements(xpath("//div[@class='item-prod col-lg-3']"));
        for (WebElement item : onlyAvailableItems) {
            assertTrue(item.findElement(xpath("//a[@class='prod-cart__buy']")).isEnabled());
        }
    }

    private void logIn(String password) {
        driver.findElement(xpath("//div[contains(@class,'header-bottom__right')]//a[contains(@href,'/sign-in')]")).click();
        driver.findElement(xpath("//div[@class='sign-holder clearfix']//input[@name='login']")).sendKeys("test.kliuchkovska@gmail.com", ENTER);
        driver.findElement(xpath("//div[@class='sign-holder clearfix']//input[@name='password']")).sendKeys(password, ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAlert")));
        driver.findElement(xpath("//button[contains(@class,'fancybox-close')]")).click();
    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }
}