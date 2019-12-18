package uniandes.insightd.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Util {
	/**
	 * 
	 * @param country BR, US, CO, CH
	 * @return
	 */
	public static String getExcelFile(String country) {
		String file = "../processinginsightd_datasource/InsighTD-"+country+"(Respuestas).xlsx";		
		return file;
	}
	
	/**
	 * 
	 * @param country BR, US, CO, CH
	 * @return
	 */
	public static String getCodesFile(String country) {
		String file = "codetranslation/"+country.toLowerCase()+"_codes.csv";		
		return file;
	}
	
	/**
	 * BR y US usan columna 17, y CH y CO usan columna 36
	 * @param country BR, US, CO, CH
	 * @return
	 */
	public static int getColumnForPayment(String country) {
		int column = 0;
		if(country.equals("BR") || country.equals("US"))
			column = 17;
		else if(country.equals("CO") || country.equals("CH"))
			column = 34;
		return column;
	}
	
	/**
	 * This method take a country and load a list of new codes based on the original code of the answer
	 * @param country BR, US, CO, CH
	 * @return
	 */
	public static HashMap<String, String> loadTransformationsOfCodes(String country) {
		HashMap<String, String> codesToTranslate = new HashMap<String, String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getCodesFile(country)));
		    String line;
		    while ((line = reader.readLine()) != null){
		    	
		    	String[] codes = line.split("\\;");
		    	
		    	codesToTranslate.put(codes[0].toUpperCase(), codes[1].toUpperCase());
//		    	System.out.println(codes[0].toUpperCase()+".");
		    }
		    reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return codesToTranslate;
	}
	
	/**
	 * This method load a list of final codes based on the original codes
	 * @return
	 */
	public static HashMap<String, String> loadTransformationsOfGlobalCodes() {
		HashMap<String, String> codesToTranslate = new HashMap<String, String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("codetranslation/global_codes.csv"));
		    String line;
		    while ((line = reader.readLine()) != null){
		    	String[] codes = line.split("\\;");
		    	
		    	codesToTranslate.put(codes[0].toUpperCase(), codes[1].toUpperCase());
		    }
		    reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return codesToTranslate;
	}
	
	/**
	 * Prints the list of repayment strategies
	 * @param codes
	 * @param withCategories Also print the category of the repayment strategy
	 */
	@SuppressWarnings("rawtypes")
	public static void print(HashMap<String, Registro> codes) {
		
		Iterator hmIterator = codes.entrySet().iterator();		
		
		while (hmIterator.hasNext()) { 
			
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
            
            String llave = mapElement.getKey().toString();
	        Registro rec = (Registro) mapElement.getValue();
	            
	        System.out.println(rec.getRepaymentCategory()+"|"+llave+"|"+rec.getTotal());
            
        } 
				
	}
	
	/**
	 * Prints the list of repayment practices using a predefined list of
	 * codes used to group answers from all the countries.
	 * @param codes
	 * @param withCategories Also print the category of the repayment strategy
	 */
	@SuppressWarnings("rawtypes")
	public static void printGlobalCodes(HashMap<String, Registro> codes) {
		
		Iterator hmIterator = codes.entrySet().iterator();		
		
		while (hmIterator.hasNext()) { 
			
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
            
            String llave = mapElement.getKey().toString();
	        Registro rec = (Registro) mapElement.getValue();
	        
	        HashMap<String, String> gCodes = loadTransformationsOfGlobalCodes();
	        String newKey = gCodes.get(llave);
	            
	        System.out.println(rec.getRepaymentCategory()+"|"+newKey+"|"+rec.getTotal());
            
        } 
				
	}
	
	/**
	 * Prints the list of repayment strategies
	 * @param codes
	 * @param withCategories Also print the category of the repayment strategy
	 */
	@SuppressWarnings("rawtypes")
	public static void printCriterion(HashMap<String, Registro> codes) {
		
		Iterator hmIterator = codes.entrySet().iterator(); 
		
		while (hmIterator.hasNext()) { 
				
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
            Registro rec = (Registro) mapElement.getValue();
	            
	        System.out.println(rec.getCriterion()+"|"+rec.getRepaymentCategory()+"|"+rec.getRepayment()+"|"+rec.getTotal());
            
        }		
	}
	
	@SuppressWarnings("rawtypes")
	public static void printCounts(HashMap<String, Integer> counts) {
		
		Iterator hmIterator = counts.entrySet().iterator(); 
		
		while (hmIterator.hasNext()) { 
				
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
            
            String llave = mapElement.getKey().toString();
	        int count = (Integer)mapElement.getValue();
	            
	        System.out.println(llave+"|"+count);
            
        }		
	}
	
	public static String getListOfCausesPerRow(Row row) {
		String cell = "";
		
		Cell celda19 = row.getCell(17);
		Cell celda21 = row.getCell(19);
		Cell celda23 = row.getCell(21);
		
		Set<String> causasSet = new HashSet<String>();
		causasSet.add(celda19.toString().toUpperCase());
		causasSet.add(celda21.toString().toUpperCase());
		causasSet.add(celda23.toString().toUpperCase());
		
		ArrayList<String> causas = new ArrayList<>(causasSet);
	    
		for(int g=0;g<causas.size();g++) {
	    	cell += causas.get(g) + "\n";
	    }
		
		return cell;
	}
	
	/**
	 * 
	 * @param type C: Causes; R: Repayment; M: Monitoring; P: Prevention
	 * @return
	 */
	public static HashMap<String, String> loadCategories(char type) {
		HashMap<String, String> categoriasCausas = new HashMap<String, String>();
		
		String file = "";
		switch(type) {
		case 'R': 
			file = "categories/repaymentcategories.csv";
			break;
		case 'C': 
			file = "categories/causescategories.csv";
			break;
		case 'M': 
			file = "categories/monitoringcategories.csv";
			break;
		case 'P': 
			file = "categories/preventioncategories.csv";
			break;
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null){
//		    	System.out.println(line);
		    	String[] causaCategoria = line.split("\\;");
		    	
		    	categoriasCausas.put(causaCategoria[1].toUpperCase(), causaCategoria[0].toUpperCase());
		    }
		    reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return categoriasCausas;
	}
	
	/**
	 * Obtiene los valores para los campos regulares, es decir, aquellos que no
	 * requirieron codificacion de las respuestas.
	 * @param firstSheet
	 * @param colPregunta
	 * @return
	 */
	public static Set<String> obtenerCamposPregunta(Sheet firstSheet, int colPregunta){
		Set<String> names = new HashSet<String>();
		for(int t=1;t<firstSheet.getLastRowNum();t++) {	    	
	    	if(firstSheet.getRow(t)!=null) {	    		
	    		Row row = firstSheet.getRow(t);
				Cell celda = row.getCell(colPregunta);
				String cell = celda.toString();
				names.add(cell);	    		
	    	}
	    }
		return names;
	}
	
	// ------------------------------------------------------
	// ------------------------------------------------------
	// ------------------------------------------------------
	// ------------------------------------------------------
	
	
	public static void main(String[] args) {
		Util u = new Util();
		u.getTotal();
	}
	
	public void getTotal() {
		
		HashMap<String, String> gCodes = new HashMap<String, String>();
		String vals = "";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("analysis_files/Data_RQ3_Causes.csv"));
		    String line;
		   		    
		    while ((line = reader.readLine()) != null){
		    	
		    	String[] codes = line.split("\\;");
		    	
		    	if(gCodes.get(codes[0])==null) {
		    		gCodes.put(codes[0], codes[1]);
		    	}else {
		    		vals = gCodes.get(codes[0]);
		    		int val = Integer.parseInt(vals); 
		    		val += Integer.parseInt(codes[1]);
		    		
		    		gCodes.put(codes[0], String.valueOf(val));
		    	}
		    	
		    }
		    reader.close();
		}catch (Exception e) {
			System.out.println(vals);
			e.printStackTrace();
		}
		
		Iterator hmIterator = gCodes.entrySet().iterator(); 
		
		while (hmIterator.hasNext()) { 
				
			Map.Entry mapElement = (Map.Entry)hmIterator.next(); 
            
            String llave = mapElement.getKey().toString();
            String count = mapElement.getValue().toString();
	            
	        System.out.println(llave+"|"+count);
            
        }	
		
	}
}
