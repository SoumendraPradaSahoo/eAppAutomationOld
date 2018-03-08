package eApp_Automation;

import java.io.IOException;


import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class log_out {
	public static void logout(WebDriver d, int sec) throws InterruptedException, IOException {
		String[] currHandle= new String[5];
		int i=0;
		System.out.println("Went to logout java");
		for(String winHandle : d.getWindowHandles()){
			currHandle[i++]=winHandle;
		}
		new WebDriverWait(d, sec).until(ExpectedConditions.elementToBeClickable((By.id("logout")))).click();
		System.out.println("Logout is clicked");
		//Handle Popup	
		Alert alert = d.switchTo().alert();
		alert.accept();
		d.switchTo().window(currHandle[0]);
		
		TakeScreenShot.Screenshot("Screenshot4.png",d);
		//driver.quit();
	}
}