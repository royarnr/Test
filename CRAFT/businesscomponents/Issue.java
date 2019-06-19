package businesscomponents;

import com.cognizant.framework.FrameworkParameters;
import com.cognizant.framework.Status;
import com.cognizant.framework.selenium.ResultSummaryManager;

import supportlibraries.ReusableLibrary;
import supportlibraries.ScriptHelper;

public class Issue extends ReusableLibrary {

	public Issue(ScriptHelper scriptHelper) {
		super(scriptHelper);
		// TODO Auto-generated constructor stub
	}
	
	public void googlesearchnext() throws InterruptedException
	{
		driver.get("http://www.google.com");
		Thread.sleep(3000);
	}
	
	public void sfdc() throws InterruptedException
	{
		driver.get("https://login.salesforce.com");
		Thread.sleep(3000);
		report.addResultSummaryHeading("SFDC Launch");
		report.addTestLogHeading("SFDC Heading");
		report.addTestLogSection("Section");
		report.addTestLogTableHeadings();
		report.updateTestLog("Login to SFDC", "Login Successful", Status.PASS);
		
	}

}
