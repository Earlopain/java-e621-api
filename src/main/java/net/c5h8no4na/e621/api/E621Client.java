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
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.c5h8no4na.common.assertion.Assert;
import net.c5h8no4na.common.network.ApiClient;
import net.c5h8no4na.common.network.ErrorType;
import net.c5h8no4na.e621.api.response.FullUser;
import net.c5h8no4na.e621.api.response.Pool;
import net.c5h8no4na.e621.api.response.Post;
import net.c5h8no4na.e621.api.response.Tag;
import net.c5h8no4na.e621.api.response.User;

public class E621Client extends ApiClient<JsonElement> {

	private String base = "https://e621.net";

	private String username;
	private String apiKey;
	private String useragent;

	public E621Client(String useragent) {
		super();
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

	public void setApiBase(String base) {
		this.base = base;
	}

	protected HttpClient getHttpClient() {
		return HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
	}

	public E621Response<Post> getPost(Integer id) {
		E621Request<Post> json = get(Endpoint.POSTS.getById(id));
		return json.wrapIntoError(Post.class);
	}

	public E621Response<List<Post>> getPosts(Integer... ids) {
		return getPosts(Arrays.asList(ids));
	}

	public E621Response<List<Post>> getPosts(List<Integer> ids) {
		String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
		Map<String, String> queryParams = Map.of("tags", String.format("id:%s", idString));
		E621Request<List<Post>> json = get(Endpoint.POSTS.getWithParams(queryParams));
		Type type = new TypeToken<ArrayList<Post>>() {}.getType();
		return json.wrapIntoError(type);
	}

	public E621Response<Tag> getTagById(Integer id) {
		E621Request<Tag> json = get(Endpoint.TAGS.getById(id));
		return json.wrapIntoError(Tag.class);
	}

	public E621Response<Tag> getTagByName(String tag) {
		E621Response<List<Tag>> json = getTagsByName(tag);
		return json.extractOneFromList();
	}

	public E621Response<List<Tag>> getTagsByName(String... tags) {
		return getTagsByName(Arrays.asList(tags));
	}

	public E621Response<List<Tag>> getTagsByName(List<String> tags) {
		String tagString = String.join(",", tags);
		Map<String, String> queryParams = Map.of("search[name]", tagString, "search[hide_empty]", "no");
		E621Request<List<Tag>> json = get(Endpoint.TAGS.getWithParams(queryParams));
		Type type = new TypeToken<ArrayList<Tag>>() {}.getType();
		return json.wrapIntoError(type);
	}

	public E621Response<FullUser> getUserById(Integer id) {
		E621Request<FullUser> json = get(Endpoint.USERS.getById(id));
		return json.wrapIntoError(FullUser.class);
	}

	public E621Response<FullUser> getUserByName(String name) {
		try {
			Integer.parseInt(name);
			// Name is numeric getting it would tread it as id
			// Get the id from the name
			Map<String, String> queryParams = Map.of("search[name_matches]", name);
			E621Request<List<User>> jsonByName = get(Endpoint.USERS.getWithParams(queryParams));
			Type type = new TypeToken<ArrayList<User>>() {}.getType();
			E621Response<List<User>> response = jsonByName.wrapIntoError(type);
			if (response.getSuccess()) {
				E621Response<User> user = response.extractOneFromList();
				if (user.getSuccess()) {
					return getUserById(user.unwrap().getId());
				} else {
					return response.reinterpretCast();
				}
			} else {
				return response.reinterpretCast();
			}

		} catch (Exception e) {
			E621Request<FullUser> json = get(Endpoint.USERS.getByString(name));
			return json.wrapIntoError(FullUser.class);
		}
	}

	public E621Response<Pool> getPoolById(Integer id) {
		E621Request<Pool> json = get(Endpoint.POOLS.getById(id));
		return json.wrapIntoError(Pool.class);
	}

	public <T> E621Request<T> get(String url) {
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
}
