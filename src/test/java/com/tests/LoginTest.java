package com.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest extends BaseTest {

    // Renamed from wait() -> explicitWait(): java.lang.Object already declares
    // a final wait() method (used for thread synchronization), which every
    // class inherits, including this one via BaseTest. A private method named
    // wait() with no args collides with that final method and fails to
    // compile ("cannot override final method" / "attempting to assign weaker
    // access privileges"). Renaming avoids the collision entirely - nothing
    // to do with Selenium, purely a Java inheritance rule.
    private WebDriverWait explicitWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void validLoginShowsSuccessMessage() {
        driver.navigate().to("https://the-internet.herokuapp.com/login");

        explicitWait().until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
                .sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement flash = explicitWait().until(ExpectedConditions.presenceOfElementLocated(By.id("flash")));
        Assert.assertTrue(flash.getText().contains("You logged into a secure area"),
                "Expected success message not found. Actual: " + flash.getText());
    }

    @Test
    public void invalidLoginShowsErrorMessage() {
        driver.navigate().to("https://the-internet.herokuapp.com/login");

        explicitWait().until(ExpectedConditions.presenceOfElementLocated(By.id("username")))
                .sendKeys("wronguser");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement flash = explicitWait().until(ExpectedConditions.presenceOfElementLocated(By.id("flash")));
        Assert.assertTrue(flash.getText().contains("Your username is invalid"),
                "Expected error message not found. Actual: " + flash.getText());
    }
}