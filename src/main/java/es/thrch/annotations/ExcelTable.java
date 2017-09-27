package es.thrch.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable {

	public int workSheetIndex();
	public int firstRow();
	public int lastRow();
	public int titleRow() default 1;
}
