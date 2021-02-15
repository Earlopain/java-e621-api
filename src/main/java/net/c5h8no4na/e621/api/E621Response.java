package net.c5h8no4na.e621.api;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.ApiResponse;

public class E621Response<T> extends ApiResponse<T> {

	private E621Response() {}

	static <T> E621Response<T> fromValue(E621Request<T> request, T value) {
		E621Response<T> result = new E621Response<>();
		result.setResponseCode(request.getResponseCode());

		if (!request.isSuccess()) {
			result.setSuccess(false);
			result.setErrorType(request.getErrorType());
			result.setErrorMessage(request.getErrorMessage());
			return result;
		} else {
			result.setSuccess(true);
			result.setResponse(value);
			return result;
		}
	}

	static <T> E621Response<T> createNotFoundError() {
		E621Response<T> result = new E621Response<>();
		result.setSuccess(false);
		result.setErrorMessage("not found");
		return result;
	}

	static <T> E621Response<T> createSuccess(T value, Integer responseCode) {
		E621Response<T> result = new E621Response<>();
		result.setSuccess(true);
		result.setResponse(value);
		result.setResponseCode(responseCode);
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	<U> E621Response<U> reinterpretCast() {
		Assert.isFalse(isSuccess());
		return reinterpretCast(this);
	}

	@SuppressWarnings({ "rawtypes" })
	private static E621Response reinterpretCast(E621Response r) {
		return r;
	}
}
