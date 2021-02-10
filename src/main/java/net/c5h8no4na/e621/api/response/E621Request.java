package net.c5h8no4na.e621.api.response;

import java.util.Objects;

import com.google.gson.JsonElement;

public class E621Request extends NetworkRequest<JsonElement> {
    public String getErrorMessage() {
	if (errorType != null) {
	    return errorType.toString();
	}
	Objects.requireNonNull(data, "only get errormessage if request not successfull");
	return data.getAsJsonObject().getAsJsonPrimitive("reason").getAsString();
    }

    public Boolean isSuccess() {
	if (data == null) {
	    return false;
	}
	return data.getAsJsonObject().getAsJsonPrimitive("success") == null;
    }
}
