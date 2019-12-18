package uniandes.insightd;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uniandes.insightd.util.Registro;
import uniandes.insightd.util.Util;


/**
 * Esta clase analiza el fuente y obtiene la causa por cada registro. Con esta
 * causa obtiene la categoria. De la misma manera, obtiene la estrategia de pago, 
 * y su correspondiente categoria. 
 * Las categorias se obtienen de un registro externo donde estan asociada el codigo con
 * su categoria.
 * Esta clase imprime los resultados de la siguiente manera:
 * CATEGORIACAUSA|CATEGORIAPAGO|FREQ
 * PLANNING AND MANAGEMENT|PEOPLE|1
 * Por cada cruce de valores, se asigna un 1. En el Notebook se hacen la sumatoria de 
 * los cruces repetidos.
 * Este archivo se usa para el notebook InsighTD: HeatMap
 * El nombre del archivo generado es: CatCausaVsCatSiPago_Col.csv
 * Recuerde que se genera uno para el Sí y otro para el No. 
 * @author borisrainieroperezgutierrez
 *
 */
public class DataExtractionRQ3 {

	public static void main(String[] args) throws Exception {

		String country = "BR";
		
		HashMap<String, String> listOfCodesToTranslate = Util.loadTransformationsOfCodes(country);
		
		(new DataExtractionRQ3()).readBooksFromExcelFile(Util.getExcelFile(country), 
	    		listOfCodesToTranslate, Util.getColumnForPayment(country), country);
		
	}

	public ArrayList<Registro> readBooksFromExcelFile(String excelFilePath, 
			HashMap<String, String> listOfCodesToTranslate, int columna, String country) throws Exception {
		
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		
		// Loading categories for causes and repayment strategies. However, this was not used for analysis.
		HashMap<String, String> repayCategories = Util.loadCategories('R');
		HashMap<String, String> causeCategories = Util.loadCategories('C');
		
		

		ArrayList<Registro> registros = new ArrayList<Registro>();

		for (int t = 1; t < 150; t++) {
			
			if (firstSheet.getRow(t) != null) {
				
				Row row = firstSheet.getRow(t);
				
				Cell cellSiPago = row.getCell(columna-2);
				String sCellSiPago = cellSiPago.toString();
				
				// ********************************************
				// OJO CON ESTA CONFIGURACION
				// Sí | No
				// ********************************************
				
//				if(sCellSiPago.equals("Sí")) {
				
				// This was required because COLOMBIA and CHILE had these codes in three different columns. 
				// So, a joining process was required.
				// US and BR had the codes joined in one column.
				// CO and CH could have had the codes in a single column, but... 
				String cellCauses = "";
				if(country.equals("CO") || country.equals("CH")) {
					cellCauses = Util.getListOfCausesPerRow(row);
				}else {
					cellCauses = row.getCell(11).toString();
				}
									
					
				if (cellCauses != null && cellCauses.trim().length() > 0) {
					String[] lines = cellCauses.split("\r\n|\r|\n");
					for (int y = 0; y < lines.length; y++) {
						
						// A cause can be related to one or more codes, so, it is required
						// to extract each code for processing.
						String causeCode = lines[y].toUpperCase().trim();

						if (causeCode != null && causeCode.trim().length() > 0) {

							
							
							// Getting the corresponding repayment strategy. Could be one or more codes.
							Cell celdaRepayStrat = row.getCell(columna);
							String sCeldRepayStrat = celdaRepayStrat.toString();

							if (sCeldRepayStrat != null && sCeldRepayStrat.trim().length() > 0) {
								String[] linesRepay = sCeldRepayStrat.split("\r\n|\r|\n");
								for (int u = 0; u < linesRepay.length; u++) {
									
									String codeRepay = linesRepay[u];

									if (codeRepay != null && codeRepay.trim().length() > 0) {

										String translatedRepayCode = getGlobalCode(codeRepay, listOfCodesToTranslate);
										if(translatedRepayCode==null || translatedRepayCode.length()==0)
											translatedRepayCode = codeRepay;
										
										String categoriaCausa = causeCategories.get(causeCode);
										String categoriaRepayment = repayCategories.get(translatedRepayCode);
										
										Registro r = new Registro();
										r.setCauseCategory(categoriaCausa);
										r.setRepaymentCategory(categoriaRepayment);
										
										r.setCause(causeCode);
										r.setRepayment(translatedRepayCode);
										
										r.setWasPaid(sCellSiPago);
										r.setIdRow(t);
										r.setTotal(1);
										registros.add(r);

									}
								}
							}
						}
					}
				}
//				}
			}
		}
		
		for(Registro r : registros) {
			System.out.print(r.getCause()+"|"+r.getRepayment()+"|"+r.getTotal()+"|"+r.getWasPaid()+"\n");
		}

		workbook.close();
		inputStream.close();
		
		return registros;

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
