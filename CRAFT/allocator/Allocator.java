package allocator;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.openqa.selenium.Platform;

import com.cognizant.framework.selenium.*;

import qcupdation.ITDConnection;
import qcupdation.ITDConnection4;
import qualitycenter.QualityCenter;
import supportlibraries.DriverScript;

import com.cognizant.framework.ExcelDataAccessforxlsm;
import com.cognizant.framework.FrameworkParameters;
import com.cognizant.framework.IterationOptions;
import com.cognizant.framework.Report;
import com.cognizant.framework.Settings;
import com.cognizant.framework.TestParameters;
import com.cognizant.framework.Util;

/**
 * Class to manage the batch execution of test scripts within the framework
 * 
 * @author Cognizant
 */


public class Allocator {
	private FrameworkParameters frameworkParameters = FrameworkParameters
			.getInstance();
	private static Properties properties;
	
	private Properties mobileProperties;
	private ResultSummaryManager resultSummaryManager = ResultSummaryManager
			.getInstance();
	public static QualityCenter qcclass = new QualityCenter();
	public static ITDConnection4 QCLoginObj;

	/**
	 * The entry point of the test batch execution <br>
	 * Exits with a value of 0 if the test passes and 1 if the test fails
	 * 
	 * @param args
	 *            Command line arguments to the Allocator (Not applicable)
	 */
	public static void main(String[] args) {
		Allocator allocator = new Allocator();
		allocator.driveBatchExecution();
	}

	private void driveBatchExecution() {
		resultSummaryManager.setRelativePath();
		properties = Settings.getInstance();
		mobileProperties = Settings.getMobilePropertiesInstance();
		String runConfiguration;
		if (System.getProperty("RunConfiguration") != null) {
			runConfiguration = System.getProperty("RunConfiguration");
		} else {
			runConfiguration = properties.getProperty("RunConfiguration");
		}
		resultSummaryManager.initializeTestBatch(runConfiguration);
		if(properties.getProperty("QCOnOff").equalsIgnoreCase("On"))
		{
			qcOneTimeSetUp();
		}
		int nThreads = Integer.parseInt(properties
				.getProperty("NumberOfThreads"));
		resultSummaryManager.initializeSummaryReport(nThreads);

		resultSummaryManager.setupErrorLog();

		int testBatchStatus = executeTestBatch(nThreads);

		resultSummaryManager.wrapUp(false);
		resultSummaryManager.launchResultSummary();
		disconnectfromqc();
		System.exit(testBatchStatus);
		
		
	}
	/***** When working with SeeTest/Perfecto Parellel  *****/
	/*private int executeTestBatch(int nThreads) {
        List<SeleniumTestParameters> testInstancesToRun = getRunInfo(frameworkParameters
                     .getRunConfiguration());
        ExecutorService parallelExecutor = Executors
                     .newFixedThreadPool(nThreads);
        ParallelRunner testRunner = null;
        int i=0;
  while(i<testInstancesToRun.size())
  {
         System.out.println("I:"+i);
         int size=i+nThreads;
         //System.out.println("First For");
         for(int currentTestInstance=size-nThreads;currentTestInstance<size;currentTestInstance++)
         {
        testRunner = new ParallelRunner(
                     testInstancesToRun.get(currentTestInstance));
          parallelExecutor.execute(testRunner);
         
         if(frameworkParameters.getStopExecution()) {
               break;
         }
         }

  parallelExecutor.shutdown();
  while(!parallelExecutor.isTerminated()) {
               try {
                     Thread.sleep(3000);
               } catch (InterruptedException e) {
                     e.printStackTrace();
               }
        } 
  System.out.println("Waitng for thread to stop");
  i=size;
  }
  if (testRunner == null) {
               return 0; // All tests flagged as "No" in the Run Manager
        } else {
               return testRunner.getTestBatchStatus();
        }
 }*/

	
	private int executeTestBatch(int nThreads) {
		List<SeleniumTestParameters> testInstancesToRun = getRunInfo(frameworkParameters
				.getRunConfiguration());
		ExecutorService parallelExecutor = Executors
				.newFixedThreadPool(nThreads);
		ParallelRunner testRunner = null;

		for (int currentTestInstance = 0; currentTestInstance < testInstancesToRun
				.size(); currentTestInstance++) {
			testRunner = new ParallelRunner(
					testInstancesToRun.get(currentTestInstance));
			parallelExecutor.execute(testRunner);

			if (frameworkParameters.getStopExecution()) {
				break;
			}
		}

		parallelExecutor.shutdown();
		while (!parallelExecutor.isTerminated()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (testRunner == null) {
			return 0; // All tests flagged as "No" in the Run Manager
		} else {
			return testRunner.getTestBatchStatus();
		}
		
	}

	private List<SeleniumTestParameters> getRunInfo(String sheetName) {
		ExcelDataAccessforxlsm runManagerAccess = new ExcelDataAccessforxlsm(
				frameworkParameters.getRelativePath(), "Run Manager");
		runManagerAccess.setDatasheetName(sheetName);

		int nTestInstances = runManagerAccess.getLastRowNum();
		List<SeleniumTestParameters> testInstancesToRun = new ArrayList<SeleniumTestParameters>();

		for (int currentTestInstance = 1; currentTestInstance <= nTestInstances; currentTestInstance++) {
			String executeFlag = runManagerAccess.getValue(currentTestInstance,
					"Execute");

			if ("Yes".equalsIgnoreCase(executeFlag)) {
				String currentScenario = runManagerAccess.getValue(
						currentTestInstance, "TestScenario");
				String currentTestcase = runManagerAccess.getValue(
						currentTestInstance, "TestCase");
				SeleniumTestParameters testParameters = new SeleniumTestParameters(
						currentScenario, currentTestcase);

				testParameters.setCurrentTestInstance("Instance"
						+ runManagerAccess.getValue(currentTestInstance,
								"TestInstance"));
				testParameters.setCurrentTestDescription(runManagerAccess
						.getValue(currentTestInstance, "Description"));

				String iterationMode = runManagerAccess.getValue(
						currentTestInstance, "IterationMode");
				if (!"".equals(iterationMode)) {
					testParameters.setIterationMode(IterationOptions
							.valueOf(iterationMode));
				} else {
					testParameters
							.setIterationMode(IterationOptions.RUN_ALL_ITERATIONS);
				}

				String startIteration = runManagerAccess.getValue(
						currentTestInstance, "StartIteration");
				if (!"".equals(startIteration)) {
					testParameters.setStartIteration(Integer
							.parseInt(startIteration));
				}
				String endIteration = runManagerAccess.getValue(
						currentTestInstance, "EndIteration");
				if (!"".equals(endIteration)) {
					testParameters.setEndIteration(Integer
							.parseInt(endIteration));
				}

				String executionMode = runManagerAccess.getValue(
						currentTestInstance, "ExecutionMode");
				if (!"".equals(executionMode)) {
					testParameters.setExecutionMode(ExecutionMode
							.valueOf(executionMode));
				} else {
					testParameters.setExecutionMode(ExecutionMode
							.valueOf(properties
									.getProperty("DefaultExecutionMode")));
				}

				String toolName = runManagerAccess.getValue(
						currentTestInstance, "MobileToolName");
				if (!"".equals(toolName)) {
					testParameters.setMobileToolName(MobileToolName
							.valueOf(toolName));
				} else {
					testParameters.setMobileToolName(MobileToolName
							.valueOf(mobileProperties
									.getProperty("DefaultMobileToolName")));
				}

				String executionPlatform = runManagerAccess.getValue(
						currentTestInstance, "MobileExecutionPlatform");
				if (!"".equals(executionPlatform)) {
					testParameters
							.setMobileExecutionPlatform(MobileExecutionPlatform
									.valueOf(executionPlatform));
				} else {
					testParameters
							.setMobileExecutionPlatform(MobileExecutionPlatform.valueOf(mobileProperties
									.getProperty("DefaultMobileExecutionPlatform")));
				}

				String mobileOSVersion = runManagerAccess.getValue(
						currentTestInstance, "MobileOSVersion");
				if (!"".equals(mobileOSVersion)) {
					testParameters.setmobileOSVersion(mobileOSVersion);
				}

				String deviceName = runManagerAccess.getValue(
						currentTestInstance, "DeviceName");
				if (!"".equals(deviceName)) {
					testParameters.setDeviceName(deviceName);
				} else {
					testParameters.setDeviceName(mobileProperties
							.getProperty("DefaultDevice"));
				}

				String browser = runManagerAccess.getValue(currentTestInstance,
						"Browser");
				if (!"".equals(browser)) {
					testParameters.setBrowser(Browser.valueOf(browser));
				} else {
					testParameters.setBrowser(Browser.valueOf(properties
							.getProperty("DefaultBrowser")));
				}
				String browserVersion = runManagerAccess.getValue(
						currentTestInstance, "BrowserVersion");
				if (!"".equals(browserVersion)) {
					testParameters.setBrowserVersion(browserVersion);
				}
				String platform = runManagerAccess.getValue(
						currentTestInstance, "Platform");
				if (!"".equals(platform)) {
					testParameters.setPlatform(Platform.valueOf(platform));
				} else {
					testParameters.setPlatform(Platform.valueOf(properties
							.getProperty("DefaultPlatform")));
				}
				String persona = runManagerAccess.getValue(currentTestInstance, "Persona");
				if (!"".equals(persona))
				{
					testParameters.setPersona(persona);
				}
				else {
					testParameters.setPersona("");
					
				}
				String seeTestPort = runManagerAccess.getValue(
                        currentTestInstance, "SeeTestPort");
				if (!"".equals(seeTestPort)) {
					testParameters.setSeeTestPort(seeTestPort);
				} else {
					testParameters.setSeeTestPort(properties
                                        .getProperty("SeeTestDefaultPort"));
				}


				testInstancesToRun.add(testParameters);
			}
		}

		return testInstancesToRun;
	}
	/*
	 * This function will create a connection with QC using the credentials provided in Global Settings and Property Files
	 */
	
	public void qcOneTimeSetUp()

	{
			
		System.out.println("Initializing QC Connection....");
			
		// For multi threaded runs it is compulsory to upload to QC only towards end for proper functioning
		//uploadToQCAllAtOnce = Integer.parseInt(properties.getProperty("NumberOfThreads", "1")) > 1 ? true : false;
		
		//Properties QCProperties = new Properties();
		//Loading the QC Credentials property file to read the User ID and Password
		//QCProperties.load(new FileInputStream("QC Credentials.properties"));				
		String qcUsername = properties.getProperty("QCUserName");
		String qcPassword = properties.getProperty("QCPassword");
		String qcurl = properties.getProperty("QCURL");
		String qcDomain = properties.getProperty("QCDomain");
		String qcProject = properties.getProperty("QCProject");
		System.out.println("Authenticate with QC Credentials...");
		
		//Creating a Com4j connection object using QC Class	
		QCLoginObj = qcclass.QClogin(qcurl, qcUsername, qcPassword,
							qcDomain, qcProject);
		
		//If the connection will be successful it will return true
		//System.out.println(QCLoginObj.connected());
		if (QCLoginObj.connected())
			System.out.println("QC Credentials and connection parameters are correct and connection successful");
		else {
				JOptionPane.showMessageDialog(null,
					"Incorrect parameters. Please check your Global Settings.properties. Attempts : "
				);
					
			 }	
		
	}
	
	
	private void disconnectfromqc()
	{
		try{
		if (Allocator.QCLoginObj.connected())
		{
		System.out.println("Disconnecting with QC....");
		Allocator.QCLoginObj.disconnect();
		System.out.println("Disconnected from QC");
		}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}