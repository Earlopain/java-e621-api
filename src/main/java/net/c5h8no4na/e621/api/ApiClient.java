package net.c5h8no4na.e621.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest.Builder;
import java.util.Base64;

import net.c5h8no4na.e621.api.response.NetworkRequest;

public abstract class ApiClient<T> {

	protected HttpClient client;

	public ApiClient() {
		this.client = HttpClient.newHttpClient();
	}

	protected abstract Builder getBuilderBase();

	public abstract NetworkRequest<T> get(String url);

	public static String basicAuth(String username, String password) {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}
}
