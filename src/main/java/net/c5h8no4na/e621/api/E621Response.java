package net.c5h8no4na.e621.api;

import java.util.List;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.ApiResponse;

public class E621Response<T> extends ApiResponse<T> {

	private E621Response() {}

	public <U> E621Response<U> extractOneFromList() {
		if (getSuccess()) {
			@SuppressWarnings("unchecked")
			List<U> elements = (List<U>) unwrap();
			Assert.isTrue(elements.size() <= 1, "There should be at max 1 element returned here");
			// element not found
			if (elements.size() == 0) {
				return E621Response.createNotFoundError();
			} else {
				return E621Response.createSuccess(elements.get(0), getResponseCode());
			}
		} else {
			return reinterpretCast();
		}
	}

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
		Assert.isFalse(getSuccess());
		return reinterpretCast(this);
	}

	@SuppressWarnings({ "rawtypes" })
	private static <T> E621Response reinterpretCast(E621Response r) {
		return r;
	}
}
