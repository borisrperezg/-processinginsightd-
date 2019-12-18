package uniandes.insightd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uniandes.insightd.util.Util;

/**
 * This class get all repayment strategies based on the codes of the answers file.
 * It filters only answers where respondent did pay the debt.
 * @param args
 * @throws IOException
 */
public class DataExtractionPercentajesPayment {
	
	public static void main(String[] args) throws IOException {
		
		String country = "US";
		
		String excelFilePath = Util.getExcelFile(country);
		
		DataExtractionPercentajesPayment reader = new DataExtractionPercentajesPayment();
	    reader.readBooksFromExcelFile(excelFilePath, Util.getColumnForPayment(country), 4);
	    
	    
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
	public void readBooksFromExcelFile(String excelFilePath, int columna, int criterionColumn) throws IOException {
	    FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
	 
	    // Open excel file
	    Workbook workbook = new XSSFWorkbook(inputStream);
	    // Read first sheet of the Excel file
	    Sheet firstSheet = workbook.getSheetAt(0);
	    
	    // Get all the possible and unique value for the selected column
	    ArrayList<String> criterionColumns = new ArrayList<String>();
	    criterionColumns.addAll(Util.obtenerCamposPregunta(firstSheet, criterionColumn));
	    LinkedHashSet<String> hashSet = new LinkedHashSet<>(criterionColumns);        
        ArrayList<String> uniqueColumns = new ArrayList<>(hashSet);
        
        // Iterate the columns
        for(String s : uniqueColumns) {
	    
		    int countYes = 0;
		    int countNo = 0;
		    
		    for(int t=1;t<150;t++) {
		    	
		    	if(firstSheet.getRow(t)!=null) {
		    		
		    		Row row = firstSheet.getRow(t);
		    		
		    		// Only answers where the debt was paid
		    		String sCeldaSiPago = row.getCell(columna-2).toString();
		    		String sCeldaCriterio = row.getCell(criterionColumn).toString();
		    		
		    		// Ask if the answer if equals to the column value of the criterium
		    		if(sCeldaCriterio.equals(s)) {
		    			// Using filter for the three languages
			    		if(sCeldaSiPago.equals("Sí") || sCeldaSiPago.equals("Yes") || sCeldaSiPago.equals("Sim")) {
			    			countYes++;
				    	}else {			    		
				    		countNo++;
				    	}
		    		}
		    	}
		    }
		    
		    System.out.println(s+"|"+countYes+"|"+countNo);
		    
        }
	    
	    workbook.close();
	    inputStream.close();
	}
	
	
	
}
