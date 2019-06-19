package businesscomponents;

import java.io.IOException;

import com.cognizant.framework.Status;

import supportlibraries.ReusableLibrary;
import supportlibraries.ScriptHelper;

public class Batch extends ReusableLibrary {

	public Batch(ScriptHelper scriptHelper) {
		super(scriptHelper);
		// TODO Auto-generated constructor stub
	}
	
	public void runBatch()
	{
		try {
			
			
			//Runtime.getRuntime().exec("cmd /c start C:\\Users\\579572\\Desktop\\sample.bat");
			//Runtime.getRuntime().exec("cmd /c start "+properties.getProperty("SecuritybatchFilePath"));
			Process process = Runtime.getRuntime().exec("cmd /c start "+properties.getProperty("SecuritybatchFilePath"));
			report.updateTestLog("BatchScriptExecution", "Batch execution started", Status.PASS);
			while (process.isAlive())
			{
				report.updateTestLog("BatchScriptExecutionProgress", "Batch execution under progress", Status.PASS);
				Thread.sleep(5000);
			}
			
			report.updateTestLog("BatchScriptExecutionComplete", "Batch execution completed", Status.PASS);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
