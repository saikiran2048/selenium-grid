package com.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void validLoginShowsSuccessMessage() {
        driver.navigate().to("https://the-internet.herokuapp.com/login");

        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement flash = driver.findElement(By.id("flash"));
        Assert.assertTrue(flash.getText().contains("You logged into a secure area"),
                "Expected success message not found. Actual: " + flash.getText());
    }

    @Test
    public void invalidLoginShowsErrorMessage() {
        driver.navigate().to("https://the-internet.herokuapp.com/login");

        driver.findElement(By.id("username")).sendKeys("wronguser");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement flash = driver.findElement(By.id("flash"));
        Assert.assertTrue(flash.getText().contains("Your username is invalid"),
                "Expected error message not found. Actual: " + flash.getText());
    }
}
