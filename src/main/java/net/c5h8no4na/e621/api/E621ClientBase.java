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
import java.util.function.BinaryOperator;
import java.util.logging.Logger;

import com.google.gson.reflect.TypeToken;

import lombok.Getter;
import lombok.Setter;
import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.NetworkUtils;
import net.c5h8no4na.e621.api.response.E621ApiType;

abstract class E621ClientBase {

	protected static final Logger LOG = Logger.getLogger(E621Client.class.getCanonicalName());

	protected HttpClient client;

	@Getter
	@Setter
	private String apiBase = "https://e621.net";

	private long lastApiCall = 0;

	private long apiCallDelay = 1000;

	@Getter
	@Setter
	protected BinaryOperator<String> produceImageUrl = (md5, extension) -> {
		return String.format("https://static1.e621.net/data/%s/%s/%s.%s", md5.substring(0, 2), md5.substring(2, 4), md5, extension);
	};

	private String username;
	private String apiKey;
	private String useragent;

	protected E621ClientBase(String useragent) {
		client = getHttpClient();
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

	/**
	 * Sets the minimum delay that must pass between api calls. E6 recommends 1
	 * request per second max, with short bursts of 2 requests per second
	 * @param delay pause in milliseconds
	 */
	public void setApiCallDelay(long delay) {
		this.apiCallDelay = delay;
	}

	protected HttpClient getHttpClient() {
		return HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
	}

	protected <T extends E621ApiType> E621Response<T> extractOneFromList(E621Response<List<T>> list) {
		if (list.isSuccess()) {
			List<T> elements = list.unwrap();
			Assert.isTrue(elements.size() <= 1, "There should be at max 1 element returned here");
			// element not found
			if (elements.isEmpty()) {
				return E621Response.createNotFoundError();
			} else {
				return E621Response.createSuccess(elements.get(0), list.getResponseCode());
			}
		} else {
			return list.reinterpretCast();
		}
	}

	protected <T extends E621ApiType> E621Request<List<T>> getList(String url) throws InterruptedException {
		return get(url);
	}

	protected <T extends E621ApiType> E621Request<T> getSingle(String url) throws InterruptedException {
		return get(url);
	}

	protected <T> E621Request<T> get(String url) throws InterruptedException {
		long diffSinceLastCall = System.currentTimeMillis() - lastApiCall;
		if (diffSinceLastCall < apiCallDelay) {
			Thread.sleep(diffSinceLastCall);
		}
		lastApiCall = System.currentTimeMillis();

		LOG.info(() -> String.format("Making request for %s", url));
		HttpRequest request = getBuilderBase().GET().uri(URI.create(apiBase + url)).build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return E621Request.create(response);

		} catch (IOException e) {
			return E621Request.create(ErrorType.NETWORK_REQUEST_FAILED);
		}
	}

	protected Builder getBuilderBase() {
		Assert.notNull(useragent, "useragent must be set");
		Builder b = HttpRequest.newBuilder();
		if (apiKey != null && useragent != null) {
			b.header("Authorization", NetworkUtils.basicAuth(username, apiKey));
		}
		b.header("User-Agent", useragent);
		return b;
	}

	protected <T extends E621ApiType> Type getListType(Class<T> clazz) {
		return TypeToken.getParameterized(List.class, clazz).getType();
	}
}
