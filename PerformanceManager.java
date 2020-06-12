package com.ubs.Managers;

import com.rsa.sslj.x.j;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.control.IfController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kedarpi
 */
public class PerformanceManager {

    public static void loadJmeterPropertiesFromFiles() {
        JMeterUtils.loadJMeterProperties("Resources/jmeter/jmeter.properties");
        JMeterUtils.setJMeterHome(".");
        JMeterUtils.setProperty("saveservice_properties", "/Resources/jmeter/saveservice.properties");
    }

    public static HeaderManager createHeaderBody(HashMap<String,String> map){
        HeaderManager headerManager = new HeaderManager();
        for (String key : map.keySet()) {
            System.out.println("key: " + key + " value: " + map.get(key));
            headerManager.add(new Header(key, map.get(key)));
        }
        headerManager.setName("Header Body");
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        System.out.println("Headers: " + headerManager.getHeaders());
        return headerManager;
    }

    public static HTTPSamplerProxy createHttpSampler(String method, String domain, int port, String path, String protocol, String payload) {
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setName("Rest Request");
        httpSampler.setEnabled(true);
        httpSampler.setPostBodyRaw(true);

        HTTPArgument httpArgument = new HTTPArgument();
        httpArgument.setAlwaysEncoded(false);
        httpArgument.setMetaData("=");
        httpArgument.setValue(payload);
        Arguments arguments = new Arguments();
        arguments.addArgument(httpArgument);

        System.out.println("HTTP Argument value: "  + httpArgument.getValue());

        httpSampler.setArguments(arguments);
        httpSampler.setDomain(domain);
        if(port!=0)
            httpSampler.setPort(port);
        httpSampler.setMethod(method);
        httpSampler.setProtocol(protocol);
        httpSampler.setPath(path);
        httpSampler.setFollowRedirects(true);
        httpSampler.setAutoRedirects(false);
        httpSampler.setUseKeepAlive(true);
        httpSampler.setDoMultipartPost(false);
        httpSampler.setMonitor(false);
        httpSampler.setProperty(TestElement.GUI_CLASS,  HttpTestSampleGui.class.getName());
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        return httpSampler;
    }

    private static LoopController createLoopController(int loops) {

        LoopController loopController = new LoopController();
        loopController.setEnabled(true);
        loopController.setFirst(true);
        loopController.setContinueForever(false);
        loopController.setLoops(loops);
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.initialize();
        return loopController;
    }

    /**
     *
     * @param condition condition is in only javascript! No other language
     * @return
     */
    public static IfController createIfController(String condition) {
        IfController ifController = new IfController(condition);
        return ifController;
    }

    public static ThreadGroup createSetupThreadGroup(int loop, int numThreads, int rampUp) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUp);
        threadGroup.setScheduler(false);
        threadGroup.setSamplerController(createLoopController(loop));
        threadGroup.setEnabled(true);
        threadGroup.setProperty(TestElement.GUI_CLASS,ThreadGroupGui.class.getName());
        threadGroup.setProperty(TestElement.TEST_CLASS,ThreadGroup.class.getName());
        return threadGroup;
    }

    public static TestPlan createTestPlan(String name) {
        TestPlan testPlan = new TestPlan(name);
        testPlan.setComment("Smart Research Load test plan");
        testPlan.setFunctionalMode(false);
        testPlan.setSerialized(false);
        //testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
        testPlan.setProperty(TestElement.GUI_CLASS, "");
        testPlan.setProperty(TestElement.TEST_CLASS, "");
        return testPlan;
    }

    /**
     *  This is required to execute jmx project by command line
     * @param testElement
     * @param <T>
     * @return
     */
    public static <T extends TestElement> T enhanceWithGuiClass(T testElement) {
        testElement.setProperty(TestElement.GUI_CLASS, " "/*testElement.getClass().getName()+"Gui"*/);
        return testElement;
    }

    public static CSVDataSet createDataSet(String filepath, String delimtier, List<String> values) {
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setName("CSV Data Set Config");
        csvDataSet.setProperty("delimiter", delimtier);
        csvDataSet.setProperty("fileEncoding", "");
        csvDataSet.setProperty("filename", filepath);
        csvDataSet.setProperty("ignoreFirstLine", false);
        csvDataSet.setProperty("quotedData", false);
        csvDataSet.setProperty("recycle", true);
        csvDataSet.setProperty("shareMode", "shareMode.all");
        csvDataSet.setProperty("stopThread", false);
        for (String val : values) {
            csvDataSet.setProperty(new StringProperty("variableNames", val.toString()));
        }
        csvDataSet.setProperty(TestElement.TEST_CLASS, csvDataSet.getClass().getName());
        csvDataSet.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        return csvDataSet;
    }

    public static void startEngine(HashTree testPlanTree, String reportPath){
        Summariser summary = null;
        String summariserName = JMeterUtils.getPropDefault(
                "summariser.name", "summary");
        if (summariserName.length() > 0) {
            summary = new Summariser(summariserName);
        }
        String reportFile = reportPath;
        ResultCollector logger = new ResultCollector(summary);
        logger.setFilename(reportFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(testPlanTree);
        jmeter.run();
    }
}
