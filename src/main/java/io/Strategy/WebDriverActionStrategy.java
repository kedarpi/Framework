package io.Strategy;

/**
 * @author t618320
 */

import com.ubs.Managers.WebDriverManager;
import com.ubs.WireFrame.ActionStrategy;
import io.Manager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

public class WebDriverActionStrategy implements ActionStrategy {

    public void click(Object element) {

        if(element instanceof WebElement){
            ((WebElement) element).click();
        }
    }

    public void type(Object element, String text) {

        if(element instanceof WebElement){
            ((WebElement) element).clear();
            ((WebElement) element).sendKeys(text);
        }
    }

    public boolean isDisplayed(Object obj) {

        boolean flag = false;
        if(obj instanceof WebElement){
           flag = ((WebElement) obj).isDisplayed();
        }
        return flag;
    }

    public boolean isEnabled(Object obj) {

        boolean flag = false;
        if(obj instanceof WebElement){
           flag = ((WebElement) obj).isEnabled();
        }
        return flag;
    }

    public boolean isChecked(Object obj) {

        boolean flag = false;
        if(obj instanceof WebElement){
           flag = ((WebElement) obj).isSelected();
        }
        return flag;
    }

    public Object runJavascript(String query) {

        JavascriptExecutor js = (JavascriptExecutor) WebDriverManager.getInstance().getDriver();
        return js.executeScript(query);
    }

    public Object runJavascript(String query, Object obj) {

        JavascriptExecutor js = (JavascriptExecutor) WebDriverManager.getInstance().getDriver();
        return js.executeScript(query,obj);
    }

    public void doubleClick(Object obj) {

        Actions action = new Actions(WebDriverManager.getInstance().getDriver());
        action.doubleClick().build().perform();
    }

    public String getText(Object obj) {
        if(obj instanceof WebElement)
            return ((WebElement) obj).getText();
        return null;
    }

    public void select(Object obj, String type) {

        if(obj instanceof Select) {
            ((Select) obj).selectByVisibleText(type);
        }
    }

    public void pressKey(Object element, String key){

        ((WebElement)element).sendKeys(Keys.chord(Keys.CONTROL, key), "");
    }

    public void rightClick(Object obj){

        Actions action= new Actions(WebDriverManager.getInstance().getDriver());
        action.moveToElement((WebElement) obj);
        action.contextClick((WebElement) obj).build().perform();
    }

    public void uploadFile(String filePath){

        StringSelection ss = new StringSelection(filePath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        Robot robot = null;
        try {
            robot = new Robot();
            robot.delay(250);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void moveMouseToElement(Object element){
        Actions action = new Actions(WebDriverManager.getInstance().getDriver());
        action.moveToElement((WebElement) element);
        action.build().perform();
    }
}
