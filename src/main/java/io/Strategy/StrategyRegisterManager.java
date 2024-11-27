package io.Strategy;


import com.ubs.ActionStrategy.JQueryActionStrategy;
import com.ubs.ActionStrategy.WebDriverActionStrategy;
import com.ubs.LocationStrategy.*;
import com.ubs.Steps.ProjectSteps.TRsteps;
import com.ubs.WireFrame.ActionStrategy;
import com.ubs.WireFrame.LocatorStatergy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.support.ui.Select;

public class StrategyRegisterManager {

    private static StrategyRegisterManager instance = null;
    private String application;

    private StrategyRegisterManager(){

    }

    public static StrategyRegisterManager getInstance(){

        if(instance == null){
            instance = new StrategyRegisterManager();
        }
        return instance;
    }

    public String getApplication(){
        return application;
    }

    public void setApplication(String applciation){
        this.application = applciation;
    }

    public LocatorStatergy getLocationStrategy() {

        if (application.equalsIgnoreCase("callsheet"))
            return new CallSheetStatergy();
        if (application.equalsIgnoreCase("emaillaunchpad"))
            return new EmailLaunchPadStatergy();
        if (application.equalsIgnoreCase("EvidenceLab"))
            return new EvidenceLabStatergy();
        if (application.equalsIgnoreCase("scheduling"))
            return new SmartSchedulingStatergy();
        if (application.equalsIgnoreCase("Bliss"))
            return new BlissStrategy();
        if (application.equalsIgnoreCase("TR"))
            return new TRStrategy();
        if(application.equalsIgnoreCase("walletmanager"))
            return new WMStatergy();
        if(application.equalsIgnoreCase("salestrading"))
            return new STAStrategy();
        else
            return new CallSheetStatergy();
    }

    public ActionStrategy getActionStrategy(){
        if(application.equalsIgnoreCase("callsheet"))
            return new WebDriverActionStrategy();
        if(application.equalsIgnoreCase("emaillaunchpad"))
            return new WebDriverActionStrategy();
        if(application.equalsIgnoreCase("EvidenceLab"))
            return new WebDriverActionStrategy();
        if (application.equalsIgnoreCase("scheduling"))
            return new WebDriverActionStrategy();
        if (application.equalsIgnoreCase("Bliss"))
            return new WebDriverActionStrategy();
        if (application.equalsIgnoreCase("TR"))
            return new TRStrategy();
        if(application.equalsIgnoreCase("walletmanager"))
            return new WMStatergy();
        if(application.equalsIgnoreCase("salestrading"))
            return new WebDriverActionStrategy();
        else
            return new JQueryActionStrategy();
    }

    public ActionStrategy getActionStrategy(Object obj){

        if(obj.toString().contains("$") || obj instanceof String)
            return new JQueryActionStrategy();
        else if(obj instanceof WebElement || obj instanceof By || obj instanceof Select || obj instanceof Action)
            return new WebDriverActionStrategy();
        return null;
    }
}
