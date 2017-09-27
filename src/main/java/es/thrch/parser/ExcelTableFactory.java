package es.thrch.parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.thrch.annotations.ExcelColumn;
import es.thrch.annotations.ExcelParseProperties;
import es.thrch.annotations.ExcelParsePropertiesDefaultValue;
import es.thrch.annotations.ExcelTable;
import es.thrch.annotations.ExcelTableId;
import es.thrch.exceptions.NullIdException;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelTableFactory<T> {

	private static Logger log = LoggerFactory.getLogger("ExcelTableFactory");

	private Properties properties;

	public ExcelTableFactory() {

	}

	public ExcelTableFactory(Properties properties) {
		this.properties = properties;
	}

	@SuppressWarnings("unchecked")
	public ExcelParserResult<T> createTableList(Workbook wb, Class<T> clazz) {
		List<T> toRet = new ArrayList<T>();
		List<ErrorObject<T>> error = new ArrayList<>();

		ExcelTable table = clazz.getDeclaredAnnotation(ExcelTable.class);

		int sheetIndex = table.workSheetIndex();
		Integer rowFrom = table.firstRow();
		Integer rowTo = table.lastRow();

		List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
		List<Method> methods = Arrays.asList(clazz.getDeclaredMethods());

		Sheet sheet = wb.getSheet(sheetIndex);
		sheet.getSettings();
		int maxRows = sheet.getRows();

		try {
			for (int i = rowFrom - 1; i < rowTo && i < maxRows; i++) {
				T instance = clazz.newInstance();
				try {
					for (Method method : methods) {

						Field field = fields.stream().filter(fieldObj -> method.getName().replace("set", "")
								.toUpperCase().equals(fieldObj.getName().toUpperCase())).findFirst().orElse(null);

						ExcelColumn column = field != null ? field.getDeclaredAnnotation(ExcelColumn.class) : null;
						if (field != null && column != null) {

							Cell cell = sheet.getCell(column.exclColumn(), i);
							ExcelTableId id = field.getDeclaredAnnotation(ExcelTableId.class);
							if (id != null) {
								if (cell.getContents().hashCode() == 0 || cell.getContents().equals(id.nullValue()))
									throw new NullIdException();
							}
							Boolean hasError = Boolean.FALSE;
							if (cell.getType().equals(CellType.ERROR) || hasError(cell.getContents())
									|| cell.getContents().equals(column.errorValue())) {
								log.info("Error en {} => campo: {}", i, field.getName());
								error.add(new ErrorObject<T>(i + 1, field.getName(),
										sheet.getCell(column.exclColumn(), table.titleRow() - 1).getContents(),
										instance));
								hasError = Boolean.TRUE;
							}
							try {
								Object obj = parseArg(cell, field);
								Class<?> objClazz = field.getType();
								if (obj != null && obj.getClass().equals(objClazz) && !hasError)
									method.invoke(instance, obj);

							} catch (InvocationTargetException e) {
								log.info("ENTRA EN ERROR TEMPORAL");
							}

						}
					}
					toRet.add(instance);
				} catch (NullIdException e) {
					log.error("Campo con id nulo");
				}
			}

		} catch (InstantiationException | IllegalAccessException e) {

			log.error("InstantiationException|IllegalAccessException en createTableList", e);
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException en createTableList", e);
		}

		return new ExcelParserResult<T>(toRet, error);

	}

	public Object parseArg(Cell cell, Field field) {
		Object toRet = null;
		String content = cell.getContents();

		try {

			if (field.getType().equals(Integer.class)) {
				toRet = Integer.parseInt(content);
			} else if (field.getType().equals(Double.class)) {
				toRet = Double.parseDouble(content.replaceAll(",", "."));
			} else if (field.getType().equals(String.class)) {
				toRet = content;
			} else if (field.getType().equals(Long.class)) {
				toRet = Long.parseLong(content);
			} else if (field.getType().equals(Date.class)) {
				DateFormat df = new SimpleDateFormat(
						this.properties.getProperty(ExcelParseProperties.DATE_FORMAT) != null
								? this.properties.getProperty(ExcelParseProperties.DATE_FORMAT)
								: ExcelParsePropertiesDefaultValue.DEFAULT_DATE_FORMAT_VALUE);

				toRet = df.parse(content);
			}
		} catch (Exception e) {
			log.error("NO PARSE ARG: value: {} ;;; field: {}", cell.getContents(), field.getName());
			toRet = content;
		}

		return toRet;
	}

	public Boolean hasError(String content) {
		Boolean toRet = ErrorStrings.errorStrs.stream().filter(errStr -> errStr.equals(content)).count() > 0;
		List<String> strErrors = (List<String>) this.properties.get(ExcelParseProperties.ERROR_STRINGS);
		Boolean toRet2 = strErrors != null && strErrors.stream().filter(errStr -> errStr.equals(content)).count() > 0;
		return toRet || toRet2;
	}

}
