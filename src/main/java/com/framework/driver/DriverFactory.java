package com.framework.driver;

import com.framework.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builds a RemoteWebDriver pointed at the Selenium Grid Hub.
 *
 * Key interview concept: the test code NEVER talks to a local chromedriver/geckodriver
 * binary. It talks to the Grid Hub over HTTP (the gridUrl). The Hub inspects the
 * "capabilities" (browserName, platform, etc.) in the session request and routes
 * it to a registered Node that can satisfy them (the chrome-node or firefox-node
 * container in docker-compose.yml). That's the whole point of Grid: hub does
 * routing/queueing, nodes do the actual browser execution.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver initDriver() {
        String browser = ConfigManager.getBrowser();
        String gridUrl = ConfigManager.getGridUrl();
        WebDriver driver;

        try {
            switch (browser) {
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    // Headless is what lets this run inside a container with no display.
                    firefoxOptions.addArguments("-headless");
                    driver = new RemoteWebDriver(new URL(gridUrl), firefoxOptions);
                    break;
                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    driver = new RemoteWebDriver(new URL(gridUrl), chromeOptions);
                    break;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Grid URL: " + gridUrl, e);
        }

        driverThreadLocal.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
