package io.Manager;

import com.ubs.Utilies.GenericUtil;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author t618320
 */

public class WebDriverManager {

    private WebDriver _driver= null;
    private WiniumDriver _windriver = null;
    private static WebDriverManager instance = null;
    Process p = null;

    private static class WebDriverManagerInner{

        public static final WebDriverManager obj = new WebDriverManager();
    }

    private WebDriverManager(){

    }

    public WebDriver getDriver(){
        if(_driver == null)
            System.out.println("There is no browser initialize, please initiate.");
        return _driver;
    }

    public static WebDriverManager getInstance(){

        return WebDriverManagerInner.obj;
        /*if(instance==null){
            instance = new WebDriverManager();
        }
        return instance;*/
    }

    public WebDriver getWindowDriver(){
        if(_windriver == null)
            System.out.println("There is no App initialize, please mention correct path.");
        return _windriver;
    }

    public WebDriver initialize(String browser){

        if (browser.equalsIgnoreCase("chrome")) {

            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "Resources/Drivers" + File.separator + "chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("useAutomationExtension", false);
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put( "profile.content_settings.exceptions.automatic_downloads.*.setting", 1 );
            prefs.put("download.prompt_for_download", "false");
            prefs.put("download.default_directory",System.getProperty("user.dir") + File.separator + ScreenShots.getInstance().getHtmlOutputFolder());
            options.setExperimentalOption("prefs", prefs);
            _driver = new ChromeDriver(options);
            _driver.manage().window().maximize();

        } else if(browser.equalsIgnoreCase("chrome beta")){

            ChromeOptions optionsBeta = new ChromeOptions();
            optionsBeta.setBinary("C:\\Program Files (x86)\\Google\\Chrome Beta\\Application\\chrome.exe");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "Resources/Drivers" + File.separator + "chromedriver-beta.exe");
            optionsBeta.setExperimentalOption("useAutomationExtension", false);
            _driver = new ChromeDriver(optionsBeta);
            _driver.manage().window().maximize();

        } else if (browser.equalsIgnoreCase("ie")) {

            System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + File.separator + "Resources/Drivers" + File.separator + "IEDriverServer.exe");
            DesiredCapabilities ieCapabilities=DesiredCapabilities.internetExplorer();
            ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
            ieCapabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            ieCapabilities.setJavascriptEnabled(true);
            _driver = new InternetExplorerDriver();
            _driver.manage().deleteAllCookies();
            _driver.manage().window().maximize();

        } else if(browser.equalsIgnoreCase("headless")){
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "Resources/Drivers" + File.separator + "chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.setHeadless(true);
            options.addArguments("--window-size=1200x600","--ignore-certificate-errors");
            DesiredCapabilities crcapabilities = DesiredCapabilities.chrome();
            crcapabilities.setCapability(ChromeOptions.CAPABILITY, options);
            crcapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            crcapabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
            _driver = new ChromeDriver(crcapabilities);
        }
        return _driver;
    }

    public void closeAllBrowser(){

        if(_driver !=null){
            _driver.quit();
        }
    }

    public void closeBrowser(){
        if(_driver!=null){
            _driver.close();
        }
    }

    public WiniumDriver getApplication(String appPath) throws MalformedURLException {

        p = GenericUtil.runCommandProcess(System.getProperty("user.dir")+File.separator+"Resources/Drivers"+File.separator+"Winium.Desktop.Driver.exe");
        DesktopOptions option = new DesktopOptions();
        option.setApplicationPath(appPath);
        _windriver = new WiniumDriver(new URL("http://localhost:9999"), option);
        return _windriver;
    }

    public void closeApp(){
        if(_windriver!=null){
            p.destroyForcibly();
        }
    }

    public void switchWindow(String windowName) {

        Set<String> handle = WebDriverManager.getInstance().getDriver().getWindowHandles();
        for (String mywindows : handle) {
            String myTitle = WebDriverManager.getInstance().getDriver().switchTo().window(mywindows).getTitle();
            if (myTitle.equals(windowName)) {
                WebDriverManager.getInstance().getDriver().switchTo().window(mywindows);
                break;
            }
        }
    }
}
