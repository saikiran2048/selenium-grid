package com.tests;

import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DropdownTest extends BaseTest {

    @Test
    public void canSelectOptionTwo() {
        driver.navigate().to("https://the-internet.herokuapp.com/dropdown");

        Select dropdown = new Select(driver.findElement(org.openqa.selenium.By.id("dropdown")));
        dropdown.selectByVisibleText("Option 2");

        Assert.assertEquals(dropdown.getFirstSelectedOption().getText(), "Option 2");
    }
}
