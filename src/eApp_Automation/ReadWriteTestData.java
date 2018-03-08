package eApp_Automation;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadWriteTestData 
{
	//Method to Get the Total No of Cases to Execute
	public static int getTestCases(String fileName) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestCase");
		Iterator<Row> iterator = TestCase_Sheet.iterator();
		int rowCount = 0;
		while (iterator.hasNext()) 
		{
			rowCount++;
			iterator.next();
		}
		workbook.close();
		excelFile.close();
		return rowCount-1;
	}

	//Method to Get if Particular test case needs to be executed
	public static Boolean getExecutable(String fileName, int row_num) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestCase");
		Row currentRow = TestCase_Sheet.getRow(row_num);
		Cell currentCell = currentRow.getCell(2);
		if (currentCell.getStringCellValue().equalsIgnoreCase("Yes")) 
		{
			workbook.close();
			excelFile.close();
			return true;
		}
		else
		{
			workbook.close();
			excelFile.close();
			return false;
		}
	}

	//Method to Get the data values of the test cases
	public static String getTestData(String fileName, int col_num, String rowName) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestData");
		int rowCount = 0;
		Row currentRow;
		Cell currentCell;
		Iterator<Row> iterator = TestCase_Sheet.iterator();
		while (iterator.hasNext()) 
		{
			currentRow = TestCase_Sheet.getRow(rowCount);
			currentCell = currentRow.getCell(0);
			if (currentCell.getStringCellValue().equalsIgnoreCase(rowName))
			{
				currentCell = currentRow.getCell(col_num);
				String keyValue = currentCell.getStringCellValue();
				workbook.close();
				excelFile.close();
				return keyValue;
			}
			rowCount++;
			iterator.next();
		}
		workbook.close();
		excelFile.close();
		return "";
	}

	//Method to Set the data values of the test cases
	public static void setTestData(String fileName, int col_num, String rowName, String DataValue) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestData");
		int rowCount = 0;
		Row currentRow;
		Cell currentCell;
		Iterator<Row> iterator = TestCase_Sheet.iterator();
		while (iterator.hasNext()) 
		{
			currentRow = TestCase_Sheet.getRow(rowCount);
			currentCell = currentRow.getCell(0);
			if (currentCell.getStringCellValue().equalsIgnoreCase(rowName))
			{
				currentCell = currentRow.getCell(col_num);
				currentCell.setCellValue(DataValue);
				excelFile.close();
				FileOutputStream outputStream = new FileOutputStream(new File(fileName));
				workbook.write(outputStream);
				workbook.close();
				outputStream.close();
				return;
			}
			rowCount++;
			iterator.next();
		}
		workbook.close();
		excelFile.close();
	}
}