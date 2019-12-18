package uniandes.insightd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

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
public class DataExtractionRQ2 {
	
	
	public static void main(String[] args) throws IOException {
		
		String country = "US";
		
		String excelFilePath = Util.getExcelFile(country);
		HashMap<String, String> listOfCodesToTranslate = Util.loadTransformationsOfCodes(country);
		
		DataExtractionRQ2 reader = new DataExtractionRQ2();
	    HashMap<String, Registro> codes = reader.readBooksFromExcelFile(excelFilePath, 
	    		listOfCodesToTranslate, Util.getColumnForPayment(country), 4);
	    
	    Util.printCriterion(codes);
	}

	/**
	 * 
	 * @param excelFilePath
	 * @param listOfCodesToTranslate
	 * @param columna Column number for the code of the repayment strategy
	 * @param criterionColumn 	Column number of the criterion, for example, size of the company, role, development process, among others.
	 * 							0 - Company size; 1 - Country; 2 - LOC; 3 - Number of people; 4 - System age; 5 - Role; 6 - Experience; 7 - Delopment process model. 
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Registro> readBooksFromExcelFile(String excelFilePath, 
			HashMap<String, String> listOfCodesToTranslate, 
			int columna, int criterionColumn) throws IOException {
		
	    FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
	 
	    Workbook workbook = new XSSFWorkbook(inputStream);
	    Sheet firstSheet = workbook.getSheetAt(0);
	    
	    HashMap<String, Registro> codes = new HashMap<String, Registro>();
	    // Loading categories of codes. This is not used in analysis at this moment.
	    HashMap<String, String> categories = Util.loadCategories('R');
	    
	    for(int t=1;t<150;t++) {
	    	
	    	if(firstSheet.getRow(t)!=null) {
	    		
	    		Row row = firstSheet.getRow(t);
	    		
	    		String sCeldaSiPago = row.getCell(columna-2).toString();
	    	
	    		// Only answers that paid the debt
	    		if(sCeldaSiPago!=null && sCeldaSiPago.trim().length()>0 && 
	    				(sCeldaSiPago.equals("Sí") || sCeldaSiPago.equals("Yes") || 
	    						sCeldaSiPago.equals("Sim"))) {
	    		
	    			// Getting the value of the required column. For this case, the column with the code of the repayment strategy.
		    		String celda = row.getCell(columna).toString();
		    		
		    		if(celda!=null && celda.trim().length()>0) {
			    		
		    			String sCellFilter = row.getCell(criterionColumn).toString();
		    			
		    			// This is required because some cells have more than one code
				    	String[] lines = celda.split("\r\n|\r|\n");
				    	
				    	for(int y=0;y<lines.length;y++) {
				    		
				    		String code = lines[y].trim();
				    		
				    		if(code!=null && code.trim().length()>0) {
				    			code = code.toUpperCase();
				    			
				    			// Use the code in the file and tranformed into a new global code 
				    			String translatedCode = listOfCodesToTranslate.get(code.toUpperCase());
				    			
				    			// Store the new code and its value
						    	if(codes.get(sCellFilter+";"+translatedCode)==null) {
						    		
						    		String category = categories.get(translatedCode);
						    		
						    		Registro record = new Registro();
						    		record.setRepayment(translatedCode);
						    		record.setRepaymentCategory(category);
						    		record.setCriterion(sCellFilter);
						    		record.setTotal(1);
						    		
						    		codes.put(sCellFilter+";"+translatedCode, record);
						    	}else {
						    		
						    		Registro record = codes.get(sCellFilter+";"+translatedCode); 
						    		
						    		int cont = record.getTotal();
						    		cont++;
						    		record.setTotal(cont);
						    		codes.put(sCellFilter+";"+translatedCode, record);
						    	}
				    		}
				    	}			    		
			    	}	
	    		}
	    	}
	    }
	    
	    workbook.close();
	    inputStream.close();
	    
	    return codes;
	}
	
	
	
}
