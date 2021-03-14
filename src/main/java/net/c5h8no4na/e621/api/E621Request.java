package net.c5h8no4na.e621.api;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.Map.Entry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.c5h8no4na.common.assertion.Assert;

class E621Request<T> {
	private static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	protected JsonElement data;
	protected Integer responseCode;

	private E621Request() {}

	public JsonElement getData() {
		Assert.notNull(data, "check for error first");
		return data;
	}

	private void setData(JsonElement data) {
		this.data = data;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	private void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	private boolean responseCodeOk() {
		return responseCode >= 200 && responseCode < 300;
	}

	public String getErrorMessage() {
		Assert.isFalse(isSuccess(), "only get errormessage if request not successfull");
		if (data.isJsonNull()) {
			return "not found";
		} else {
			return data.getAsJsonObject().getAsJsonPrimitive("reason").getAsString();
		}
	}

	public boolean isSuccess() {
		if (!responseCodeOk()) {
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

	E621Response<T> wrapIntoError(Class<T> clazz) {
		return wrap(clazz);
	}

	E621Response<T> wrapIntoErrorWithType(Type type) {
		return wrap(type);
	}

	private E621Response<T> wrap(Type type) {
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

	static <T> E621Request<T> create(HttpResponse<String> response) {
		E621Request<T> result = new E621Request<>();
		result.setResponseCode(response.statusCode());
		result.setData(gson.fromJson(response.body(), JsonElement.class));
		return result;
	}
}
