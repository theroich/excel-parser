package es.thrch.parser;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelParserResult<T> {
	List<T> result;
	List<ErrorObject<T>> errors;
}
