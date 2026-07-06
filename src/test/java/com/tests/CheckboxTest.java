package com.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CheckboxTest extends BaseTest {

    @Test
    public void togglingCheckboxChangesItsState() {
        driver.navigate().to("https://the-internet.herokuapp.com/checkboxes");

        List<WebElement> checkboxes = driver.findElements(By.cssSelector("#checkboxes input[type='checkbox']"));
        Assert.assertEquals(checkboxes.size(), 2, "Expected 2 checkboxes on the page");

        WebElement first = checkboxes.get(0);
        boolean initialState = first.isSelected();
        first.click();
        Assert.assertNotEquals(first.isSelected(), initialState, "Checkbox state did not toggle");
    }
}
