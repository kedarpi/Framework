package io.Strategy;

import com.ubs.Managers.WebDriverManager;
import com.ubs.WireFrame.ActionStrategy;
import org.openqa.selenium.JavascriptExecutor;

/**
 * @author kedarpi
 */
public class JQueryActionStrategy implements ActionStrategy {

    public void click(Object element) {

        runJavascript((String) element+".get(0).click()");
    }

    public void type(Object element, String text) {

        runJavascript(element.toString()+".get(0).value('"+text+"')");
    }

    public boolean isDisplayed(Object obj) {
        return false;
    }

    public boolean isEnabled(Object obj) {
        return (Boolean)runJavascript("return "+(String)obj+".is(':enabled')");
    }

    public boolean isChecked(Object obj) {
        return (Boolean)runJavascript("return "+(String) obj+".is(':checked')");
    }

    public Object runJavascript(String query) {

        JavascriptExecutor js = (JavascriptExecutor) WebDriverManager.getInstance().getDriver();
        return js.executeScript(query);
    }

    public void doubleClick(Object obj) {

    }

    public String getText(Object obj) {
        return null;
    }

    public void select(Object obj, String type){
        String query = ".filter(function() {return $(this).text()=='"+type+"'}).click()";
        runJavascript((String) obj+query);
    }

    public void pressKey(Object ele, String keys) {
        System.out.println();
    }

    public void rightClick(Object obj){}
}
