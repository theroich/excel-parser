package es.thrch.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorObject<T> {

	private Integer row;
	private String field;
	private String fieldName;
	private T object;
}
