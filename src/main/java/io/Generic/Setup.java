package io.Generic;

import cucumber.api.Scenario;
import cucumber.api.java.*;
import io.Manager.ScreenShots;
import io.Manager.TagMap;
import io.Manager.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Setup {

    @Before
    public void stepUp(final Scenario scenario){
        ScreenShots.getInstance().clearScreenShot();
        TagMap.getInstance().tagMapping(scenario);
    }

    @After
    public void tearDown(final Scenario scenario){
        WebDriverManager.getInstance().closeAllBrowser();
        if(scenario.isFailed()){
            byte[] screenshotbytes = ((TakesScreenshot) WebDriverManager.getInstance().getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshotbytes,"image/png");
        }
    }
}
