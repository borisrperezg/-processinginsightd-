package uniandes.insightd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uniandes.insightd.util.Registro;
import uniandes.insightd.util.Util;

/**
 * This class get all repayment strategies based on the codes of the answers file.
 * It filters only answers where respondent did pay the debt.
 * @param args
 * @throws IOException
 */
public class CharacterizationOfData {
	
	public static void main(String[] args) throws IOException {
		
		String country = "CH";
		
		String excelFilePath = Util.getExcelFile(country);
		
		CharacterizationOfData reader = new CharacterizationOfData();
		
		HashMap<String, Integer> codes = reader.readBooksFromExcelFile(excelFilePath, 0);	    
		Util.printCounts(codes);
		System.out.println("******************************");
		
		codes = reader.readBooksFromExcelFile(excelFilePath, 3);	    
		Util.printCounts(codes);
		System.out.println("******************************");
		
		codes = reader.readBooksFromExcelFile(excelFilePath, 4);	    
		Util.printCounts(codes);
		System.out.println("******************************");
		
		codes = reader.readBooksFromExcelFile(excelFilePath, 5);	    
		Util.printCounts(codes);
		System.out.println("******************************");
	}

	/**
	 * 
	 * @param excelFilePath
	 * @param listOfCodesToTranslate
	 * @param columna Column number for the code of the repayment strategy
	 * @param criterionColumn 	Column number of the criterion, for example, size of the company, role, development process, among others.
	 * 							0 (Q1) - Company size; 
	 * 							1 (Q2) - Country; 
	 * 							2 (Q3) - LOC; 
	 * 							3 (Q4) - Number of people; 
	 * 							4 (Q5) - System age; 
	 * 							5 (Q6) - Role; 
	 * 							6 (Q7) - Experience; 
	 * 							7 (Q8) - Delopment process model. 
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Integer> readBooksFromExcelFile(String excelFilePath, int criterionColumn) throws IOException {
	    FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
	 
	    // Open excel file
	    Workbook workbook = new XSSFWorkbook(inputStream);
	    // Read first sheet of the Excel file
	    Sheet firstSheet = workbook.getSheetAt(0);
	    
	    // 
        HashMap<String, Integer> totals = new HashMap<String, Integer>();
        
        
	    for(int t=1;t<150;t++) {
	    	
	    	if(firstSheet.getRow(t)!=null) {
	    		
	    		Row row = firstSheet.getRow(t);
	    		
	    		// Only answers where the debt was paid
	    		String sCeldaCriterio = row.getCell(criterionColumn).toString();
	    		
	    		if(totals.get(sCeldaCriterio)==null) {
		    		
	    			totals.put(sCeldaCriterio, 1);
		    	}else {
		    		
		    		int cont = totals.get(sCeldaCriterio);
		    		cont++;
		    		totals.put(sCeldaCriterio, cont);
		    	}
	    		
	    	}
	    }
        
	    
	    workbook.close();
	    inputStream.close();
	    
	    return totals;
	}
	
	
	
}
