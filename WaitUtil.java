package com.ubs.ActionStrategy;
import com.ubs.Managers.LoggerManager;
import com.ubs.Managers.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;

import java.util.List;

public class WaitUtil {

    public static void waitForElementToBeClickable(final By by,int waitTime)
    {
       new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.elementToBeClickable(by));
    }
    public static void waitForElementToBeVisible(final By by,int waitTime)
    {
        new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.visibilityOfElementLocated(by));
    }
    public static void waitForElementToBePresent(final By by,int waitTime)
    {
        new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.presenceOfElementLocated(by));
    }
    public static void waitForElementNotToBePresent(final By by,int waitTime)
    {
        new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static void waitForPageToBeLoaded(int waitTimeout) throws InterruptedException {

        WebDriverManager.getInstance().getDriver().manage().timeouts().pageLoadTimeout(waitTimeout, TimeUnit.SECONDS);
    }
    public static void waitForElementsToBePresent(final By by,int waitTime)
    {
        List<WebElement> elements=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
     }
    public static boolean waitForAttributeToBePresentInElement(final By by,String attribute,String value,int waitTime)
    {
       boolean flag=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.attributeContains(by,attribute,value));
        return flag;

    }
    public static boolean waitForAttributeToBePresentInElement(final WebElement element,String attribute,String value,int waitTime)
    {
        boolean flag=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.attributeContains(element,attribute,value));
        return flag;
   }
    public static void waitForVisibilityOfAllElements(final By by,int waitTime)
    {
        List<WebElement> elements=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }
    public static void waitForPresenceOfAllElements(final By by,int waitTime)
    {
        List<WebElement> elements=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
    public static boolean waitForTextToBePresentInElement(final WebElement element,String value,int waitTime)
    {
        boolean flag=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.textToBePresentInElement(element,value));
        return flag;
    }
    public static boolean waitForTextToBe(final By by,String value,int waitTime)
    {
        boolean flag=new WebDriverWait(WebDriverManager.getInstance().getDriver(),waitTime).until(ExpectedConditions.textToBe(by,value));
        return flag;
    }

    public static void implicitWait(int TimeOut){
        WebDriverManager.getInstance().getDriver().manage().timeouts().implicitlyWait(TimeOut, TimeUnit.SECONDS);
    }
}

