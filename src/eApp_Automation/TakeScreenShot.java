package eApp_Automation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class TakeScreenShot {
	//Function to take the screenshots
	public static void Screenshot(String FileName, WebDriver d) throws IOException
	{
		TakesScreenshot camera = (TakesScreenshot) d;
		byte[] imageBytes = camera.getScreenshotAs(OutputType.BYTES);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FileName));
		out.write(imageBytes);
		out.close();
	}
}
