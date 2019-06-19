package businesscomponents;

import java.io.IOException;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import com.cognizant.framework.Status;
import com.cognizant.framework.selenium.SeleniumTestParameters;

import supportlibraries.ReusableLibrary;
import supportlibraries.ScriptHelper;

public class AEMScenario extends ReusableLibrary {
	
	//private static String url;
	
	public AEMScenario(ScriptHelper scriptHelper) {
		super(scriptHelper);
		
	}

	
	public void manulifecontactus()
	{
		driver.get("https://www.google.com");
		
		report.updateTestLog("Launch Google", "Google Launched", Status.PASS);
		//url = driver.getCurrentUrl();
	}
	
	public void googlesearch() throws Exception
	{
		driver.get("http://www.google.com");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.findElement(By.name("q")).sendKeys("google");
		report.updateTestLog("Search Google", "Google Searched", Status.PASS);
		
	}
	
	public void googleclicksearch()
	{
		//driver.get(url);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.findElement(By.name("btnG")).click();
		report.updateTestLog("Click", "Google Searched", Status.PASS);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.quit();
		
	}
	
	public void closeotherwindows()
	{
		
	}
}
