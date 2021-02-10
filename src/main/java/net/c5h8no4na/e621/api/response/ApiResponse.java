package net.c5h8no4na.e621.api.response;

import lombok.Data;
import net.c5h8no4na.common.assertion.Assert;

@Data
public class ApiResponse<T> {
	private Boolean success;
	private String errorMessage;
	private ErrorType errorType;
	private Integer responseCode;
	private T response;

	public T unwrap() {
		Assert.notNull(response, "response was null, check error before unwrapping");
		return response;
	}
}
