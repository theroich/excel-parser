package es.thrch.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
	
	public int exclColumn();
	public String errorValue() default "#ERROR!";
}
