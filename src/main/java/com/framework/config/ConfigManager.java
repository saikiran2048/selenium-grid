package com.framework.config;

/**
 * Central place to read run parameters.
 *
 * These come from -D system properties, which in turn are set by:
 *  - Maven pom.xml defaults (browser=chrome, environment=qa)
 *  - Command line: mvn test -Dbrowser=firefox -Denvironment=staging
 *  - Jenkinsfile matrix axes (each matrix cell sets these before calling mvn)
 *
 * Interview point: this is what makes ONE test suite runnable across N browsers
 * x M environments without duplicating code - the matrix just varies these inputs.
 */
public class ConfigManager {

    public static String getBrowser() {
        return System.getProperty("browser", "chrome").toLowerCase();
    }

    public static String getEnvironment() {
        return System.getProperty("environment", "qa").toLowerCase();
    }

    public static String getGridUrl() {
        return System.getProperty("gridUrl", "http://localhost:4444/wd/hub");
    }

    /**
     * Maps a logical "environment" to a base URL.
     * In a real project this would come from a properties/yaml file per env
     * (qa.properties, staging.properties...). Kept inline here to stay minimal -
     * the-internet.herokuapp.com only has one real environment, so "staging" and
     * "qa" both point at it, which we call out explicitly in the README/quiz.
     */
    public static String getBaseUrl() {
        switch (getEnvironment()) {
            case "staging":
                return "https://the-internet.herokuapp.com"; // stand-in for a real staging URL
            case "qa":
            default:
                return "https://the-internet.herokuapp.com";
        }
    }
}
