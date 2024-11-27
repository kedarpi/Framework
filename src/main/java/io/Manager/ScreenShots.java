package io.Manager;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author t618320
 */
public class ScreenShots {

    public List<byte[]> screenshot;
    public List<File> screenshotAsFile;
    private static ScreenShots _instance;
    private boolean screenFlag = true;
    private String htmlOutputFolder;

    private ScreenShots(){

        this.screenshot = new ArrayList<byte[]>();
        this.screenshotAsFile = new ArrayList<File>();
    }

    public static ScreenShots getInstance(){

        if(_instance ==null){
            _instance = new ScreenShots();
        }
        return _instance;
    }

    public void clearScreenShot(){

        this.screenshot.clear();
        this.screenshotAsFile.clear();
    }

    public void setScreenFlagAsFalse(){

        screenFlag = false;
    }

    public boolean getScreenShotFlag(){
        return screenFlag;
    }

    public String getHtmlOutputFolder(){

        return htmlOutputFolder;
    }

    public void setHtmlOutputFolder(String foldername){
        htmlOutputFolder = foldername;
    }

    public void addScreenshot(){
        if(screenFlag){
            if(screenshot == null)
                screenshot = new ArrayList<byte[]>();
            byte[] currentss = getCurrentScreenshot();
            if(currentss !=null){
                screenshot.add(getCurrentScreenshot());
            }
        }else
            screenFlag = true;
    }

    private byte[] getCurrentScreenshot(){
        if(WebDriverManager.getInstance().getDriver()!=null){
            return ((TakesScreenshot) WebDriverManager.getInstance().getDriver()).getScreenshotAs(OutputType.BYTES);
        }
        return null;
    }

    public File getCurrentScreenshotAsFile(){
        if(WebDriverManager.getInstance().getDriver()!=null){
            return ((TakesScreenshot) WebDriverManager.getInstance().getDriver()).getScreenshotAs(OutputType.FILE);
        }
        return null;
    }

    public void addScreenshotAsFile(){
        if(screenFlag){
            if(screenshotAsFile == null)
                screenshotAsFile = new ArrayList<File>();
            File currentss = getCurrentScreenshotAsFile();
            if(currentss !=null){
                screenshotAsFile.add(getCurrentScreenshotAsFile());
            }
        }else
            screenFlag = true;
    }
}
