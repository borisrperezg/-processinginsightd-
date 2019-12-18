package uniandes.insightd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
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
public class DataExtractionRQ1 {
	
	
	public static void main(String[] args) throws IOException {
		
		String country = "US";
		
		// Getting the file path
		String excelFilePath = Util.getExcelFile(country);
		HashMap<String, String> listOfCodesToTranslate = Util.loadTransformationsOfCodes(country);
		
		DataExtractionRQ1 reader = new DataExtractionRQ1();
	    HashMap<String, Registro> codes = reader.readBooksFromExcelFile(excelFilePath, listOfCodesToTranslate, Util.getColumnForPayment(country));
	    
	    Util.print(codes);
	    System.out.println("-----");
//	    Util.printGlobalCodes(codes);
	}

	public HashMap<String, Registro> readBooksFromExcelFile(String excelFilePath, HashMap<String, String> listOfCodesToTranslate, int columna) throws IOException {
	    FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
	 
	    // Open Excel file
	    Workbook workbook = new XSSFWorkbook(inputStream);
	    Sheet firstSheet = workbook.getSheetAt(0);
	    
	    HashMap<String, Registro> codes = new HashMap<String, Registro>();
	    // Loading categories of codes. This is not used in analysis at this moment.
	    HashMap<String, String> categories = Util.loadCategories('R');
	    
	    for(int t=1;t<150;t++) {
	    	
	    	if(firstSheet.getRow(t)!=null) {
	    		
	    		Row row = firstSheet.getRow(t);
	    		
	    		// Only answers that paid the debt
	    		Cell celdaSiPago = row.getCell(columna-2);
	    		String sCeldaSiPago = celdaSiPago.toString();
	    		
	    		if(sCeldaSiPago!=null && sCeldaSiPago.trim().length()>0 && 
	    				(sCeldaSiPago.equals("Sí") || sCeldaSiPago.equals("Yes") || 
	    						sCeldaSiPago.equals("Sim"))) {
	    		
	    			// Getting the value of the required column. For this case, the column with the code of the repayment strategy.
		    		Cell celdaPago = row.getCell(columna); 
		    		String celda = celdaPago.toString();
		    		
		    		if(celda!=null && celda.trim().length()>0) {
			    		
		    			// This is required because some cells have more than one code
				    	String[] lines = celda.split("\r\n|\r|\n");
				    	
				    	for(int y=0;y<lines.length;y++) {
				    		
				    		String code = lines[y].trim();
				    		
				    		if(code!=null && code.trim().length()>0) {
				    			code = code.toUpperCase();
				    			
				    			String translatedCode = getGlobalCode(code, listOfCodesToTranslate);
				    			
				    			// Store the new code and its value
						    	if(codes.get(translatedCode)==null) {
						    		
						    		String category = categories.get(translatedCode);
						    								    		
						    		Registro record = new Registro();
						    		record.setRepayment(translatedCode);
						    		record.setRepaymentCategory(category);
						    		record.setTotal(1);
						    		
						    		codes.put(translatedCode, record);
						    	}else {
						    		
						    		Registro record = codes.get(translatedCode); 
						    		
						    		int cont = record.getTotal();
						    		cont++;
						    		record.setTotal(cont);
						    		codes.put(translatedCode, record);
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
	
	public String getGlobalCode(String code, HashMap<String, String> listOfCodesToTranslate) {
		String newCode = "";
		
		// Use the code in the file and tranformed into a new global code 
		String translatedCode = listOfCodesToTranslate.get(code.toUpperCase());
				
		HashMap<String, String> gCodes = Util.loadTransformationsOfGlobalCodes();
        newCode = gCodes.get(translatedCode);
        
        return newCode;
        
	}
	
	
}
