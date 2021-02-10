package net.c5h8no4na.e621.api.response;

import java.util.Objects;

import lombok.Data;

@Data
public abstract class NetworkRequest<T> {
	protected T data;
	protected Integer responseCode;
	protected ErrorType errorType;

	public T getData() {
		Objects.requireNonNull(data, "check for error first");
		return data;
	}

	public abstract Boolean isSuccess();

	public abstract String getErrorMessage();
}
