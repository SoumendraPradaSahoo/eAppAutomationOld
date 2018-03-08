package eApp_Automation;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class login_console {
	static int testCaseNo;
	private static int TimeOutSeconds; // = 30; //Time to wait for the elements to be available
	private static String FILE_NAME; //= "D:/eApp Automation/eApp Test Data.xlsx"; //Path for Test Data
	private static String eApp_URL; //= "http://20.15.86.50:8080/eApps/"; //Eapp URL
	private static String agent_ID; //= "ssahoo43"; //Agent ID
	private static String agent_PWD; //= "vilink"; //Password
	login_console() throws IOException{
		Properties p = new Properties();
		p=ReadConfigFile.getObjectRepository();
		FILE_NAME = p.getProperty("TestData_Filename");
		eApp_URL = p.getProperty("URL");
		agent_ID = p.getProperty("agent_ID");
		agent_PWD = p.getProperty("agent_PWD");
		TimeOutSeconds = Integer.parseInt(p.getProperty("time_out_Second"));
	}
	
	public void login() throws InterruptedException, IOException
	{  
		System.out.println("Starting Automation");  
		//Get total no. of Test Cases to Execute	
		int total_TestCases = ReadWriteTestData.getTestCases(FILE_NAME);
		System.out.println("Total No of Cases: " + total_TestCases);  
		// Optional, if not specified, WebDriver will search your path for chromedriver.
		System.setProperty("webdriver.chrome.driver", "D:/chromedriver_win32/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(TimeOutSeconds,TimeUnit.SECONDS);
		try
		{		
			//driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS); ! PageLoad Time out = Not working
			driver.get(eApp_URL);
			driver.manage().window().maximize();

			//Print Page Tile for information
			System.out.println("Launch Console Page Title: " + driver.getTitle());
			TakeScreenShot.Screenshot("Screenshot1.png",driver);
			Thread.sleep(3000);  // Let the user actually see something!
			for(String winHandle : driver.getWindowHandles()){
				System.out.println(winHandle);
				driver.switchTo().window(winHandle);
			}
			System.out.println("Driver changed Successfully");
			System.out.println("Login Console Page Title: " + driver.getTitle());
			//Thread.sleep(3000);
			new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
			driver.findElement(By.name("username")).sendKeys(agent_ID);
			driver.findElement(By.name("password")).sendKeys(agent_PWD);
			TakeScreenShot.Screenshot("Screenshot2.png",driver);
			driver.findElement(By.name("_eventId__logon")).click();
			//Thread.sleep(3000);
			TakeScreenShot.Screenshot("Screenshot3.png",driver);

			for(int i=1; i <= total_TestCases; i++)
			{
				if (ReadWriteTestData.getExecutable(FILE_NAME, i))
				{
					//call to new application
					testCaseNo = i;
					System.out.println("Starting Test Case# "+i);
					New_Quote(driver);
				}
			}

			//Call the method to verify Client and Server Side message
			//New_Test(driver);

			//Closing the driver
			log_out.logout(driver,TimeOutSeconds);
			System.out.println("Automation Ends");
			driver.quit();
		}
		catch (Exception e)
		{
			TakeScreenShot.Screenshot("Error.png",driver);	
			System.out.println("error hai : "+ e);
			driver.quit();
		}

	}

	public static void New_Quote(WebDriver d) throws IOException, InterruptedException
	{
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.xpath("//li[@class='startNew']/a")))).click();
		//d.findElement(By.xpath("//*[@id='startNew']/a")).click();
		String PlanofInsurace = getData("Plan of Insurance");
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.xpath("//td[@title='"+PlanofInsurace+"']")))).click();
		//d.findElement(By.xpath("//*[@id='2']/td[3]")).click();
		d.findElement(By.id("Quote")).click();

		//Change to the new Quote window
		String currHandle = d.getWindowHandle();
		for(String winHandle : d.getWindowHandles()){
			System.out.println(winHandle);
			//if (!d.getTitle().equalsIgnoreCase("Accelerator Console - Agent") && !d.getTitle().equals(""))
			//{
			d.switchTo().window(winHandle);
			//break;
			//}
		}
		System.out.println("Driver Changed to Windows: " + d.getTitle());
		//Fill Data in Quote Screen
		waitForAjax(d);
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.name("a_FirstName_PINS"))).sendKeys(getData("First Name"));
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_MiddleName_PINS")))).sendKeys(getData("MI"));
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_LastName_PINS")))).sendKeys(getData("Last Name"));
		Select suffix_dropdown= new Select(new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_Suffix_PINS")))));
		suffix_dropdown.selectByVisibleText(getData("Suffix"));
		Select appJuridiction_dropdown= new Select(new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_ApplicationJurisdiction")))));
		appJuridiction_dropdown.selectByVisibleText(getData("Application Jurisdiction"));
		List<WebElement> saveAge_Ind= new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(d.findElements(By.name("a_SaveAgeInd"))));
		setYesNo(saveAge_Ind, getData("Save Age"));
		WebElement DOB_Pins = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.name("a_DOB_PINS")));
		DOB_Pins.sendKeys(getData("DOB"));
		DOB_Pins.sendKeys(Keys.TAB);
		waitForAjax(d);
		String age = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_Age_PINS")))).getAttribute("value");
		System.out.println("Age for case" + testCaseNo + ": "+ age);
		setData("Age", age);	
		//new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_DOB_PINS")))).sendKeys(getData("DOB"));
		Select Gender_dropdown= new Select(new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_Gender_PINS")))));
		Gender_dropdown.selectByVisibleText(getData("Gender"));
		waitForAjax(d);	
		List<WebElement> Tabacco_Ind= new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(d.findElements(By.name("a_TobaccoPremiumBasis_PINS"))));
		setYesNo(Tabacco_Ind, getData("Tobacco User"));	
		waitForAjax(d);	
		
		WebElement Curr_Amount = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.name("a_CurrentAmt")));
		Curr_Amount.sendKeys(getData("Coverage Amount"));
		Curr_Amount.sendKeys(Keys.TAB);
		waitForAjax(d);
		//new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_CurrentAmt")))).sendKeys(getData("Coverage Amount"));
		
		Select UWClass_dropdown= new Select(new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_RateClassAppliedFor_PINS")))));	
	
		UWClass_dropdown.selectByVisibleText(getData("Risk Classification Applied For"));
		
		Select Frequency_dropdown= new Select(new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_PaymentMode")))));
		Frequency_dropdown.selectByVisibleText(getData("Billing Frequency"));
		
		//Setting ADB
		if (getData("ADB Rider").equalsIgnoreCase("YES")){
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(d.findElement(By.name("a_ADBRider")))).click();
		waitForAjax(d);
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_ADBRiderAmt")))).sendKeys(getData("ADB Amount"));
		}
		
		//Setting WOP
		if (getData("WOP Rider").equalsIgnoreCase("YES")){
			new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(d.findElement(By.name("a_WOPRider")))).click();
			waitForAjax(d);
			}
		//Setting CIR
		if (getData("Child Rider").equalsIgnoreCase("YES")){
			new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(d.findElement(By.name("a_CTRider")))).click();
			waitForAjax(d);
			}
		//Setting Discounts
		if (getData("Same Payor Discount").equalsIgnoreCase("YES")){//Same Payor
			new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(d.findElement(By.name("a_SamePyrDiscountInd")))).click();
			waitForAjax(d);
			}
		if (getData("Employee Discount").equalsIgnoreCase("YES")){//Employee
			new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(d.findElement(By.name("a_EmployeeDiscountInd")))).click();
			waitForAjax(d);
			}
		if (getData("Association Discount").equalsIgnoreCase("YES")){//Association
			new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(d.findElement(By.name("a_AstDiscountInd")))).click();
			waitForAjax(d);
			}
		
		//Click Quote
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.xpath("//input[@id='quoteButton']")))).click();
		//Thread.sleep(3000);
		waitForAjax(d); //Wait for Quote to be Generated
		//Key event for PageDown
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.xpath("//input[@id='quoteButton']")))).sendKeys(Keys.PAGE_DOWN);
		//scrollToBottom(d);
		Thread.sleep(1000);
		TakeScreenShot.Screenshot("Quote Screen " +testCaseNo+ ".png",d);
		d.close();
		d.switchTo().window(currHandle);
	}

	//Method to get the data from ReadWriteTestData classes
	public static String getData(String FieldName) throws IOException
	{
		return ReadWriteTestData.getTestData(FILE_NAME, testCaseNo, FieldName);
	}	

	//Method to set the data from ReadWriteTestData classes
	public static void setData(String FieldName, String DataValue) throws IOException
	{
		ReadWriteTestData.setTestData(FILE_NAME, testCaseNo, FieldName, DataValue);
	}	

	//Method to set the Yes/No Radio Options
	public static void setYesNo(List<WebElement> radio_element, String DataValue)
	{
		if (DataValue.equalsIgnoreCase("YES")){
			radio_element.get(0).click();
		}
		else if (DataValue.equalsIgnoreCase("NO")){
			radio_element.get(1).click();
		}
	}
	//Wait method for Ajax call to be completed
	public static void waitForAjax(WebDriver d) {
		new WebDriverWait(d, TimeOutSeconds).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				JavascriptExecutor js = (JavascriptExecutor) d;
				return (Boolean) js.executeScript("return jQuery.active == 0");
			}
		});
	}
	//Scroll to Bottom of the page
	public static void scrollToBottom(WebDriver d) {
		JavascriptExecutor js = (JavascriptExecutor) d;
		//js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		js.executeScript("scrollTo(0,250)");
	}
	
//Verify Client and Server Side Message	
	public static void New_Test(WebDriver d) throws IOException, InterruptedException
	{
		String PlanofInsurace = "FLX 25 Year Term";
		String expected_msg = "Proposed Insured's First Name is missing.";
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.xpath("//li[@class='startNew']/a")))).click();
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.xpath("//td[@title='"+PlanofInsurace+"']")))).click();
		d.findElement(By.id("Quote")).click();

		//Change to the new Quote window
		String currHandle = d.getWindowHandle();
		for(String winHandle : d.getWindowHandles()){
			//System.out.println(winHandle);
			d.switchTo().window(winHandle);
		}
		
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_FirstName_PINS")))).sendKeys("");
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_FirstName_PINS")))).sendKeys(Keys.TAB);
		waitForAjax(d);
		String actual_msg = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.xpath("//div[@class='validationMsg']/label[@for='a_FirstName_PINS_quote_lifeterm']")))).getText();
		System.out.println(actual_msg);
		if (actual_msg.equals(expected_msg))
		System.out.println("First Name Pass");
		else
		System.out.println("First Name Fail");
		expected_msg="Proposed Insured's Last Name is missing.";
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_MiddleName_PINS")))).sendKeys("P");
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_LastName_PINS")))).sendKeys("");
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.name("a_LastName_PINS")))).sendKeys(Keys.TAB);
		waitForAjax(d);
		actual_msg = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOf(d.findElement(By.xpath("//div[@class='validationMsg']/label[@for='a_LastName_PINS_quote_lifeterm']")))).getText();
		System.out.println(actual_msg);
		if (actual_msg.equals(expected_msg))
			System.out.println("Last Name Pass");
			else
			System.out.println("Last Name Fail");
		new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.id("saveAndExitButton")))).click();
		waitForAjax(d);
		//List<WebElement> actual_ser_msg = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(d.findElements(By.cssSelector("div.messagesDiv > li"))));
		List<WebElement> actual_ser_msg = new WebDriverWait(d, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(d.findElements(By.xpath("//div[@id='centerContent']/div[@id='eastDiv']//div[@id='Messages']/ul/li"))));
		expected_msg="Application cannot be saved. Minimum required data First Name and Last Name of Proposed Insured has not been captured.";
		System.out.println(actual_ser_msg.get(0).getText());
		if (actual_ser_msg.get(0).getText().equals(expected_msg))
			System.out.println("Save and Exit Pass");
			else
			System.out.println("Save and Exit Fail");
		TakeScreenShot.Screenshot("Quote Screen_Test.png",d);
		d.close();
		d.switchTo().window(currHandle);
	}
	
}