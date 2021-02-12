package net.c5h8no4na.e621.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.ApiClient;
import net.c5h8no4na.common.network.ErrorType;
import net.c5h8no4na.e621.api.response.E621ApiType;

abstract class E621ClientBase extends ApiClient<JsonElement> {

	private String base = "https://e621.net";

	private String username;
	private String apiKey;
	private String useragent;

	protected E621ClientBase(String useragent) {
		super();
		this.useragent = useragent;
	}

	protected E621ClientBase(String useragent, String username, String apiKey) {
		this(useragent);
		authenticate(username, apiKey);
	}

	public void authenticate(String username, String apiKey) {
		this.useragent = username;
		this.apiKey = apiKey;
	}

	public void setApiBase(String base) {
		this.base = base;
	}

	protected HttpClient getHttpClient() {
		return HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
	}

	protected <T extends E621ApiType> E621Response<T> extractOneFromList(E621Response<List<T>> list) {
		if (list.getSuccess()) {
			List<T> elements = list.unwrap();
			Assert.isTrue(elements.size() <= 1, "There should be at max 1 element returned here");
			// element not found
			if (elements.size() == 0) {
				return E621Response.createNotFoundError();
			} else {
				return E621Response.createSuccess(elements.get(0), list.getResponseCode());
			}
		} else {
			return list.reinterpretCast();
		}
	}

	protected <T extends E621ApiType> E621Request<List<T>> getList(String url) {
		return get(url);
	}

	protected <T extends E621ApiType> E621Request<T> getSingle(String url) {
		return get(url);
	}

	protected <T> E621Request<T> get(String url) {
		HttpRequest request = getBuilderBase().GET().uri(URI.create(base + url)).build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return E621Request.create(response);

		} catch (IOException | InterruptedException e) {
			return E621Request.create(ErrorType.NETWORK_REQUEST_FAILED);
		}
	}

	protected Builder getBuilderBase() {
		Assert.notNull(useragent, "useragent must be set");
		Builder b = HttpRequest.newBuilder();
		if (apiKey != null && useragent != null) {
			b.header("Authorization", basicAuth(username, apiKey));
		}
		b.header("User-Agent", useragent);
		return b;
	}

	protected <T extends E621ApiType> Type getListType(Class<T> clazz) {
		return TypeToken.getParameterized(List.class, clazz).getType();
	}
}