package net.c5h8no4na.e621.api.response;

import java.util.Objects;

import lombok.ToString;

@ToString
public class ApiResponse<T> {
	private Boolean success;
	private String errorMessage;
	private Integer responseCode;
	private T response;

	public Boolean isSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public T unwrap() {
		Objects.requireNonNull(response, "response was null, check error before unwrapping");
		return response;
	}
}
