package businesscomponents;

import com.cognizant.framework.Status;

import supportlibraries.*;



/**
 * Class for storing general purpose business components
 * @author Cognizant
 */
public class ComponentsOfiConnect extends ReusableLibrary {
	/**
	 * Constructor to initialize the component library
	 * @param scriptHelper The {@link ScriptHelper} object passed from the {@link DriverScript}
	 */
	public ComponentsOfiConnect(ScriptHelper scriptHelper) {
		super(scriptHelper);
	}
	
	public void loginTest(){
		report.updateTestLog("Laucn", "successful", Status.PASS);
	}

	
	
	
}