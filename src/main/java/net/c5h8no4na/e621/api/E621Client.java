package net.c5h8no4na.e621.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.ApiClient;
import net.c5h8no4na.common.network.ApiResponse;
import net.c5h8no4na.common.network.ErrorType;
import net.c5h8no4na.e621.api.response.E621Request;
import net.c5h8no4na.e621.api.response.MultiplePosts;
import net.c5h8no4na.e621.api.response.SinglePost;
import net.c5h8no4na.e621.api.response.Tag;

public class E621Client extends ApiClient<JsonElement> {

	private String base = "https://e621.net";
	private Gson gson;

	private String username;
	private String apiKey;
	private String useragent;

	public E621Client() {
		super();
		gson = getGsonInstance();
	}

	public E621Client(String username, String apiKey, String useragent) {
		super();
		gson = getGsonInstance();
		authenticate(username, apiKey, useragent);
	}

	public void authenticate(String username, String apiKey, String useragent) {
		this.username = username;
		this.apiKey = apiKey;
		this.useragent = useragent;
	}

	private Gson getGsonInstance() {
		return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public ApiResponse<SinglePost> getPost(Integer id) {
		E621Request json = get(String.format("/posts/%d.json", id));
		return wrapIntoError(json, SinglePost.class);
	}

	public ApiResponse<MultiplePosts> getPosts(Integer... ids) {
		return getPosts(Arrays.asList(ids));
	}

	public ApiResponse<MultiplePosts> getPosts(List<Integer> ids) {
		String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
		E621Request json = get(String.format("/posts.json?tags=id:%s", idString));
		return wrapIntoError(json, MultiplePosts.class);
	}

	public ApiResponse<Tag> getTagById(Integer id) {
		E621Request json = get(String.format("/tags/%d.json", id));
		return wrapIntoError(json, Tag.class);
	}

	public ApiResponse<Tag> getTagByName(String tag) {
		E621Request json = get(String.format("/tags/%s.json", tag));
		return wrapIntoError(json, Tag.class);
	}

	public ApiResponse<List<Tag>> getTagsByName(String... tags) {
		return getTagsByName(Arrays.asList(tags));
	}

	public ApiResponse<List<Tag>> getTagsByName(List<String> tags) {
		String tagString = String.join(",", tags);
		E621Request json = get(String.format("/tags.json?search[name]=%s", tagString));
		Type type = new TypeToken<ArrayList<Tag>>() {}.getType();
		return wrapIntoError(json, type);
	}

	public E621Request get(String endpoint) {
		HttpRequest request = getBuilderBase().GET().uri(buildURI(endpoint)).build();
		E621Request result = new E621Request();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			Integer statusCode = response.statusCode();
			result.setResponseCode(statusCode);
			// Cloudflare error, not valid json and don't try to parse
			if (statusCode >= 500 && statusCode < 600) {
				result.setErrorType(ErrorType.SERVICE_UNAVAILABLE);
			} else {
				result.setData(gson.fromJson(response.body(), JsonElement.class));
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

	private <T> ApiResponse<T> wrapIntoError(E621Request request, Type type) {
		ApiResponse<T> result = new ApiResponse<>();
		result.setResponseCode(request.getResponseCode());

		if (!request.isSuccess()) {
			result.setSuccess(false);
			result.setErrorType(request.getErrorType());
			result.setErrorMessage(request.getErrorMessage());
			return result;
		} else {
			JsonElement json = request.getData();
			result.setSuccess(true);
			result.setResponse(gson.fromJson(json, type));
			return result;
		}
	}
}
