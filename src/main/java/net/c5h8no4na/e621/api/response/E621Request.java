package net.c5h8no4na.e621.api.response;

import com.google.gson.JsonElement;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.NetworkRequest;

public class E621Request extends NetworkRequest<JsonElement> {
	public String getErrorMessage() {
		Assert.isFalse(isSuccess(), "only get errormessage if request not successfull");
		if (data == null) {
			return errorType.toString();
		} else {
			return data.getAsJsonObject().getAsJsonPrimitive("reason").getAsString();
		}
	}

	public Boolean isSuccess() {
		if (data == null || !responseCodeOk()) {
			return false;
		}
		return data.getAsJsonObject().getAsJsonPrimitive("success") == null;
	}
}
