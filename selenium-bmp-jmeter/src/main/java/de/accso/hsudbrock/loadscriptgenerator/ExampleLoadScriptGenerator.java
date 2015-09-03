package de.accso.hsudbrock.loadscriptgenerator;

import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Example loadscript generator.
 * 
 * @author Henning Sudbrock, Accso GmbH
 */
public class ExampleLoadScriptGenerator {

	/**
	 * Orchestrates the loadscript generation by starting the BrowserMob proxy,
	 * executing the Selenium script (against the proxy), and generating the
	 * JMeter loadscript from the proxy log.
	 */
	public static void main(String[] args) throws Exception {
		ProxyServer proxy = setupAndStartProxy();
		WebDriver driver = setupWebDriver(proxy);
		
		executeTestScenario(proxy, driver);
		
		driver.quit();
		stopProxy(proxy);
		
		HarLog httpLog = proxy.getHar().getLog();
		JmxGenerator.generateJmeterScript(httpLog);
	}

	private static ProxyServer setupAndStartProxy() throws Exception {
		ProxyServer proxy = new ProxyServer(9090);
		startProxy(proxy);
		proxy.setCaptureHeaders(true);
		proxy.setCaptureContent(true);
		proxy.newHar("Testszenario für Lastskript Generierung");
		return proxy;
	}

	private static void startProxy(ProxyServer proxy) throws Exception {
		try {
            proxy.start();
        } catch (Exception e) {
            System.out.println("Exception when starting BrowserMob Proxy: " + e.getMessage());
            throw e;
        }
	}
	private static void stopProxy(ProxyServer proxy) throws Exception {
		try {
            proxy.stop();
        } catch (Exception e) {
            System.out.println("Exception when stopping BrowserMob Proxy: " + e.getMessage());
            throw e;
        }
	}
	
	private static WebDriver setupWebDriver(ProxyServer proxy) {
        Proxy seleniumProxy = proxy.seleniumProxy();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);        
        return new FirefoxDriver(capabilities);
    }
	
	private static void executeTestScenario(ProxyServer proxy, WebDriver driver) {
		proxy.newPage("Startseite der Suche aufrufen");
		driver.get("http://duckduckgo.de");
		
		proxy.newPage("Suche ausführen");
		driver.findElement(By.id("search_form_input_homepage")).sendKeys("Selenium JMeter BrowserMob Proxy");
		driver.findElement(By.id("search_button_homepage")).click();
	}
}
