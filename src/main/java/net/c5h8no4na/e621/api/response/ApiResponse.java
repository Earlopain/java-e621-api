package net.c5h8no4na.e621.api.response;

import java.util.Objects;

import lombok.ToString;

@ToString
public class ApiResponse<T> {
    private ApiError error = new ApiError();
    private T response;

    public void setError(ApiError error) {
	this.error = error;
    }

    public void setResponse(T response) {
	this.response = response;
    }

    public Boolean isError() {
	return !error.getSuccess();
    }

    public String getErrorMessage() {
	return error.getReason();
    }

    public T unwrap() {
	Objects.requireNonNull(response, "response was null, check error before unwrapping");
	return response;
    }
}
