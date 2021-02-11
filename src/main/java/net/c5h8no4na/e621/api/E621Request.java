package net.c5h8no4na.e621.api;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.Map.Entry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.ErrorType;
import net.c5h8no4na.common.network.NetworkRequest;

public class E621Request<T> extends NetworkRequest<JsonElement> {

	private static Gson gson;

	static {
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	private E621Request() {}

	public String getErrorMessage() {
		Assert.isFalse(isSuccess(), "only get errormessage if request not successfull");
		if (data == null) {
			return errorType.toString();
		} else if (data.isJsonNull()) {
			return "not found";
		} else {
			return data.getAsJsonObject().getAsJsonPrimitive("reason").getAsString();
		}
	}

	public Boolean isSuccess() {
		if (data == null) {
			// Request did not go through
			return false;
		} else if (!responseCodeOk()) {
			// Response code is not in the expected 200 range
			return false;
		} else if (data.isJsonObject()) {
			// If json contains member success the request failed
			// Should be caught by the resonseCodeOk check above
			return data.getAsJsonObject().getAsJsonPrimitive("success") == null;
		} else if (data.isJsonNull()) {
			// Some endpoints return json null with status code 200, even though it should
			// be 404
			return false;
		} else {
			return true;
		}
	}

	E621Response<T> wrapIntoError(Type type) {
		JsonElement json = gson.fromJson(data, JsonElement.class);
		if (json.isJsonObject() && json.getAsJsonObject().entrySet().size() == 1) {
			Entry<String, JsonElement> a = json.getAsJsonObject().entrySet().iterator().next();
			T value = gson.fromJson(a.getValue(), type);
			return E621Response.fromValue(this, value);
		} else {
			T value = gson.fromJson(data, type);
			return E621Response.fromValue(this, value);
		}
	}

	public static <T> E621Request<T> create(HttpResponse<String> response) {
		Integer responseCode = response.statusCode();
		// Cloudflare error, not valid json and don't try to parse
		if (responseCode >= 500 && responseCode < 600) {
			return E621Request.create(ErrorType.SERVICE_UNAVAILABLE, responseCode);
		} else {
			try {
				return E621Request.create(response.body(), responseCode);
			} catch (JsonSyntaxException e) {
				return E621Request.create(ErrorType.INVALID_JSON, responseCode);
			}
		}
	}

	public static <T> E621Request<T> create(ErrorType type) {
		E621Request<T> result = new E621Request<>();
		result.setErrorType(type);
		return result;
	}

	private static <T> E621Request<T> create(ErrorType type, Integer responseCode) {
		E621Request<T> result = new E621Request<>();
		result.setErrorType(type);
		result.setResponseCode(responseCode);
		return result;
	}

	private static <T> E621Request<T> create(String json, Integer responseCode) throws JsonSyntaxException {
		E621Request<T> result = new E621Request<>();
		result.setResponseCode(responseCode);
		result.setData(gson.fromJson(json, JsonElement.class));
		return result;
	}
}
