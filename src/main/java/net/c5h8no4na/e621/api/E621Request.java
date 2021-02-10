package net.c5h8no4na.e621.api;

import com.google.gson.JsonElement;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.NetworkRequest;

public class E621Request extends NetworkRequest<JsonElement> {
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
}
