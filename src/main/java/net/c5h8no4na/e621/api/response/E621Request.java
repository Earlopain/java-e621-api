package net.c5h8no4na.e621.api.response;

import com.google.gson.JsonElement;

import net.c5h8no4na.common.assertion.Assert;

public class E621Request extends NetworkRequest<JsonElement> {
	public String getErrorMessage() {
		Assert.isFalse(isSuccess(), "only get errormessage if request not successfull");
		if (errorType != null) {
			return errorType.toString();
		}
		return data.getAsJsonObject().getAsJsonPrimitive("reason").getAsString();
	}

	public Boolean isSuccess() {
		if (data == null) {
			return false;
		}
		return data.getAsJsonObject().getAsJsonPrimitive("success") == null;
	}
}
