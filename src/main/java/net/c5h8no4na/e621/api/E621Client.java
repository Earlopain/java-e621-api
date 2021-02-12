package net.c5h8no4na.e621.api;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.c5h8no4na.e621.api.response.FullUser;
import net.c5h8no4na.e621.api.response.Pool;
import net.c5h8no4na.e621.api.response.Post;
import net.c5h8no4na.e621.api.response.Tag;
import net.c5h8no4na.e621.api.response.User;

public class E621Client extends E621ClientBase {

	public E621Client(String useragent) {
		super(useragent);
	}

	public E621Client(String useragent, String username, String apiKey) {
		super(useragent, username, apiKey);
	}

	public E621Response<Post> getPost(Integer id) {
		E621Request<Post> json = getSingle(Endpoint.POSTS.getById(id));
		return json.wrapIntoError(Post.class);
	}

	public E621Response<List<Post>> getPosts(Integer... ids) {
		return getPosts(Arrays.asList(ids));
	}

	public E621Response<List<Post>> getPosts(List<Integer> ids) {
		String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
		Map<String, String> queryParams = Map.of("tags", String.format("id:%s", idString));
		E621Request<List<Post>> json = getList(Endpoint.POSTS.getWithParams(queryParams));
		Type type = getListType(Post.class);
		return json.wrapIntoErrorWithType(type);
	}

	public E621Response<Tag> getTagById(Integer id) {
		E621Request<Tag> json = getSingle(Endpoint.TAGS.getById(id));
		return json.wrapIntoError(Tag.class);
	}

	public E621Response<Tag> getTagByName(String tag) {
		E621Response<List<Tag>> json = getTagsByName(tag);
		return extractOneFromList(json);
	}

	public E621Response<List<Tag>> getTagsByName(String... tags) {
		return getTagsByName(Arrays.asList(tags));
	}

	public E621Response<List<Tag>> getTagsByName(List<String> tags) {
		String tagString = String.join(",", tags);
		Map<String, String> queryParams = Map.of("search[name]", tagString, "search[hide_empty]", "no");
		E621Request<List<Tag>> json = getList(Endpoint.TAGS.getWithParams(queryParams));
		Type type = getListType(Tag.class);
		return json.wrapIntoErrorWithType(type);
	}

	public E621Response<FullUser> getUserById(Integer id) {
		E621Request<FullUser> json = getSingle(Endpoint.USERS.getById(id));
		return json.wrapIntoError(FullUser.class);
	}

	public E621Response<FullUser> getUserByName(String name) {
		try {
			Integer.parseInt(name);
			// Name is numeric getting it would tread it as id
			// Get the id from the name
			Map<String, String> queryParams = Map.of("search[name_matches]", name);
			E621Request<List<User>> jsonByName = getList(Endpoint.USERS.getWithParams(queryParams));
			Type type = getListType(User.class);
			E621Response<List<User>> response = jsonByName.wrapIntoErrorWithType(type);
			if (response.getSuccess()) {
				E621Response<User> user = extractOneFromList(response);
				if (user.getSuccess()) {
					return getUserById(user.unwrap().getId());
				} else {
					return response.reinterpretCast();
				}
			} else {
				return response.reinterpretCast();
			}

		} catch (Exception e) {
			E621Request<FullUser> json = getSingle(Endpoint.USERS.getByString(name));
			return json.wrapIntoError(FullUser.class);
		}
	}

	public E621Response<Pool> getPoolById(Integer id) {
		E621Request<Pool> json = getSingle(Endpoint.POOLS.getById(id));
		return json.wrapIntoError(Pool.class);
	}
}
