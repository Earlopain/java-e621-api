package net.c5h8no4na.e621.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.c5h8no4na.e621.api.response.FullUserApi;
import net.c5h8no4na.e621.api.response.PoolApi;
import net.c5h8no4na.e621.api.response.PostApi;
import net.c5h8no4na.e621.api.response.TagApi;
import net.c5h8no4na.e621.api.response.UserApi;

public class E621Client extends E621ClientBase {

	public E621Client(String useragent) {
		super(useragent);
	}

	public E621Client(String useragent, String username, String apiKey) {
		super(useragent, username, apiKey);
	}

	public E621Response<PostApi> getPost(Integer id) {
		E621Request<PostApi> json = getSingle(Endpoint.POSTS.getById(id));
		return json.wrapIntoError(PostApi.class);
	}

	public E621Response<List<PostApi>> getPosts(Integer... ids) {
		return getPosts(Arrays.asList(ids));
	}

	public E621Response<List<PostApi>> getPosts(List<Integer> ids) {
		String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
		Map<String, String> queryParams = Map.of("tags", String.format("id:%s", idString));
		E621Request<List<PostApi>> json = getList(Endpoint.POSTS.getWithParams(queryParams));
		Type type = getListType(PostApi.class);
		return json.wrapIntoErrorWithType(type);
	}

	public E621Response<TagApi> getTagById(Integer id) {
		E621Request<TagApi> json = getSingle(Endpoint.TAGS.getById(id));
		return json.wrapIntoError(TagApi.class);
	}

	public E621Response<TagApi> getTagByName(String tag) {
		E621Response<List<TagApi>> json = getTagsByName(tag);
		return extractOneFromList(json);
	}

	public E621Response<List<TagApi>> getTagsByName(String... tags) {
		return getTagsByName(Arrays.asList(tags));
	}

	public E621Response<List<TagApi>> getTagsByName(List<String> tags) {
		String tagString = String.join(",", tags);
		Map<String, String> queryParams = Map.of("search[name]", tagString, "search[hide_empty]", "no");
		E621Request<List<TagApi>> json = getList(Endpoint.TAGS.getWithParams(queryParams));
		Type type = getListType(TagApi.class);
		return json.wrapIntoErrorWithType(type);
	}

	public E621Response<FullUserApi> getUserById(Integer id) {
		E621Request<FullUserApi> json = getSingle(Endpoint.USERS.getById(id));
		return json.wrapIntoError(FullUserApi.class);
	}

	public E621Response<FullUserApi> getUserByName(String name) {
		try {
			Integer.parseInt(name);
			// Name is numeric getting it would tread it as id
			// Get the id from the name
			Map<String, String> queryParams = Map.of("search[name_matches]", name);
			E621Request<List<UserApi>> jsonByName = getList(Endpoint.USERS.getWithParams(queryParams));
			Type type = getListType(UserApi.class);
			E621Response<List<UserApi>> response = jsonByName.wrapIntoErrorWithType(type);
			if (response.isSuccess()) {
				E621Response<UserApi> user = extractOneFromList(response);
				if (user.isSuccess()) {
					return getUserById(user.unwrap().getId());
				} else {
					return response.reinterpretCast();
				}
			} else {
				return response.reinterpretCast();
			}

		} catch (Exception e) {
			E621Request<FullUserApi> json = getSingle(Endpoint.USERS.getByString(name));
			return json.wrapIntoError(FullUserApi.class);
		}
	}

	public E621Response<PoolApi> getPoolById(Integer id) {
		E621Request<PoolApi> json = getSingle(Endpoint.POOLS.getById(id));
		return json.wrapIntoError(PoolApi.class);
	}

	public Optional<byte[]> getFile(String md5, String extension) {
		LOG.warning(() -> String.format("Downloading file %s.%s", md5, extension));
		String url = produceImageUrl.apply(md5, extension);
		HttpRequest request = getBuilderBase().GET().uri(URI.create(url)).build();
		try {
			HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
			if (response.statusCode() == 200) {
				return Optional.of(response.body());
			} else {
				return Optional.empty();
			}
		} catch (IOException | InterruptedException e) {
			return Optional.empty();
		}
	}
}
