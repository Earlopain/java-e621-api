package net.c5h8no4na.e621.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.e621.api.response.ApiResponse;
import net.c5h8no4na.e621.api.response.E621Request;
import net.c5h8no4na.e621.api.response.ErrorType;
import net.c5h8no4na.e621.api.response.MultiplePosts;
import net.c5h8no4na.e621.api.response.SinglePost;

public class E621Client extends ApiClient<JsonElement> {

	private String base = "https://e621.net";
	private Gson gson;

	private String username;
	private String apiKey;
	private String useragent;

	public E621Client() {
		super();
		gson = new Gson();
	}

	public E621Client(String username, String apiKey, String useragent) {
		super();
		gson = new Gson();
		authenticate(username, apiKey, useragent);
	}

	public void authenticate(String username, String apiKey, String useragent) {
		this.username = username;
		this.apiKey = apiKey;
		this.useragent = useragent;
	}

	public ApiResponse<SinglePost> getPost(Integer id) {
		E621Request json = get(String.format("/posts/%d.json", id));
		return wrapIntoError(json, SinglePost.class);
	}

	public ApiResponse<MultiplePosts> getPosts(Integer... ids) {
		return getPosts(Arrays.asList(ids));
	}

	public ApiResponse<MultiplePosts> getPosts(List<Integer> ids) {
		// https://e621.net/posts.json?tags=id:1,2,3,4,5,100,101
		String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
		E621Request json = get(String.format("/posts.json?tags=id:%s", idString));
		return wrapIntoError(json, MultiplePosts.class);
	}

	public E621Request get(String endpoint) {
		HttpRequest request = getBuilderBase().GET().uri(buildURI(endpoint)).build();
		E621Request result = new E621Request();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			result.setResponseCode(response.statusCode());
			result.setData(gson.fromJson(response.body(), JsonElement.class));
			if (!result.responseCodeOk()) {
				result.setErrorType(ErrorType.INVALID_API_REQUEST);
			}
		} catch (IOException | InterruptedException e) {
			result.setErrorType(ErrorType.NETWORK_REQUEST_FAILED);
		} catch (JsonSyntaxException e) {
			result.setErrorType(ErrorType.INVALID_JSON);
		}
		return result;
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

	protected URI buildURI(String endpoint) {
		return URI.create(String.format("%s/%s", base, endpoint));
	}

	private <T> ApiResponse<T> wrapIntoError(E621Request request, Class<T> clazz) {
		ApiResponse<T> result = new ApiResponse<>();
		result.setResponseCode(request.getResponseCode());

		if (!request.isSuccess()) {
			result.setSuccess(false);
			result.setErrorType(request.getErrorType());
			result.setErrorMessage(request.getErrorMessage());
			return result;
		}
		JsonElement json = request.getData();
		result.setSuccess(true);
		result.setErrorMessage("");
		result.setResponse(gson.fromJson(json, clazz));
		return result;
	}

}
