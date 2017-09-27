package es.thrch.loader;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class ExcelFileLoader {
private static Logger log = LoggerFactory.getLogger("ExcelFileLoaderImpl");
	
	
	private String path;
	
	

	public Workbook loadWorkBook(String path) {
		Workbook myFirstWbook = null;
		try {
			WorkbookSettings ws = new WorkbookSettings();
			ws.setEncoding("Cp1252");
			myFirstWbook = Workbook.getWorkbook(new File(path),ws);
			
		} catch (IOException e) {
			log.error("IOException en loadWorkBook",e);
			
		} catch (BiffException e) {
			log.error("BiffException en loadWorkBook",e);
			
		}
		return myFirstWbook;
	}
}
