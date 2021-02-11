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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import net.c5h8no4na.e621.api.response.FullUser;
import net.c5h8no4na.e621.api.response.Post;
import net.c5h8no4na.e621.api.response.Tag;
import net.c5h8no4na.e621.api.response.User;

public class E621Client extends ApiClient<JsonElement> {
	private Gson gson;

	private String username;
	private String apiKey;
	private String useragent;

	public E621Client(String useragent) {
		super();
		gson = getGsonInstance();
		this.useragent = useragent;
	}

	public E621Client(String useragent, String username, String apiKey) {
		this(useragent);
		authenticate(username, apiKey);
	}

	public void authenticate(String username, String apiKey) {
		this.useragent = username;
		this.apiKey = apiKey;
	}

	protected HttpClient getHttpClient() {
		return HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
	}

	private Gson getGsonInstance() {
		return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public ApiResponse<Post> getPost(Integer id) {
		E621Request json = get(Endpoint.POSTS.getById(id));
		return wrapIntoError(json, Post.class);
	}

	public ApiResponse<List<Post>> getPosts(Integer... ids) {
		return getPosts(Arrays.asList(ids));
	}

	public ApiResponse<List<Post>> getPosts(List<Integer> ids) {
		String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
		Map<String, String> queryParams = Map.of("tags", String.format("id:%s", idString));
		E621Request json = get(Endpoint.POSTS.getWithParams(queryParams));
		Type type = new TypeToken<ArrayList<Post>>() {}.getType();
		return wrapIntoError(json, type);
	}

	public ApiResponse<Tag> getTagById(Integer id) {
		E621Request json = get(Endpoint.TAGS.getById(id));
		return wrapIntoError(json, Tag.class);
	}

	public ApiResponse<Tag> getTagByName(String tag) {
		E621Request json = get(Endpoint.TAGS.getByString(tag));
		return wrapIntoError(json, Tag.class);
	}

	public ApiResponse<List<Tag>> getTagsByName(String... tags) {
		return getTagsByName(Arrays.asList(tags));
	}

	public ApiResponse<List<Tag>> getTagsByName(List<String> tags) {
		String tagString = String.join(",", tags);
		Map<String, String> queryParams = Map.of("search[name]", tagString);
		E621Request json = get(Endpoint.TAGS.getWithParams(queryParams));
		Type type = new TypeToken<ArrayList<Tag>>() {}.getType();
		return wrapIntoError(json, type);
	}

	public ApiResponse<FullUser> getUserById(Integer id) {
		E621Request json = get(Endpoint.USERS.getById(id));
		return wrapIntoError(json, FullUser.class);
	}

	public ApiResponse<FullUser> getUserByName(String name) {
		try {
			Integer.parseInt(name);
			// Name is numeric getting it would tread it as id
			// Get the id from the name
			Map<String, String> queryParams = Map.of("search[name_matches]", name);
			E621Request jsonByName = get(Endpoint.USERS.getWithParams(queryParams));
			Type type = new TypeToken<ArrayList<User>>() {}.getType();
			ApiResponse<List<User>> response = wrapIntoError(jsonByName, type);
			// If the first request fails we don't need to check further
			if (!jsonByName.isSuccess()) {
				return reinterpretCast(response);
			} else {
				List<User> users = response.unwrap();
				// No user with this name
				if (users.size() == 0) {
					return createNotFoundError();
				} else {
					return getUserById(users.get(0).getId());
				}

			}
		} catch (Exception e) {
			E621Request json = get(Endpoint.USERS.getByString(name));
			return wrapIntoError(json, FullUser.class);
		}
	}

	public E621Request get(URI url) {
		HttpRequest request = getBuilderBase().GET().uri(url).build();
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

	private <T> ApiResponse<T> wrapIntoError(E621Request request, Type type) {
		JsonElement json = gson.fromJson(request.getData(), JsonElement.class);
		if (json.isJsonObject() && json.getAsJsonObject().entrySet().size() == 1) {
			Entry<String, JsonElement> a = json.getAsJsonObject().entrySet().iterator().next();
			T value = gson.fromJson(a.getValue(), type);
			return wrapIntoError(request, value);
		} else {
			T value = gson.fromJson(request.getData(), type);
			return wrapIntoError(request, value);
		}
	}

	private <T> ApiResponse<T> wrapIntoError(E621Request request, T value) {
		ApiResponse<T> result = new ApiResponse<>();
		result.setResponseCode(request.getResponseCode());

		if (!request.isSuccess()) {
			result.setSuccess(false);
			result.setErrorType(request.getErrorType());
			result.setErrorMessage(request.getErrorMessage());
			return result;
		} else {
			result.setSuccess(true);
			result.setResponse(value);
			return result;
		}
	}

	private <T> ApiResponse<T> createNotFoundError() {
		ApiResponse<T> result = new ApiResponse<>();
		result.setSuccess(false);
		result.setErrorMessage("not found");
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> ApiResponse<T> reinterpretCast(ApiResponse t) {
		Assert.isFalse(t.getSuccess());
		return t;
	}
}
