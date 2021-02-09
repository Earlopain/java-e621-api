package net.c5h8no4na.e621.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.c5h8no4na.e621.api.response.ApiError;
import net.c5h8no4na.e621.api.response.ApiResponse;
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
	Optional<JsonElement> json = get(String.format("/posts/%d.json", id));
	return wrapIntoError(json, SinglePost.class);
    }

    public ApiResponse<MultiplePosts> getPosts(Integer... ids) {
	return getPosts(Arrays.asList(ids));
    }

    public ApiResponse<MultiplePosts> getPosts(List<Integer> ids) {
	// https://e621.net/posts.json?tags=id:1,2,3,4,5,100,101
	String idString = ids.stream().map(c -> c.toString()).collect(Collectors.joining(","));
	Optional<JsonElement> json = get(String.format("/posts.json?tags=id:%s", idString));
	return wrapIntoError(json, MultiplePosts.class);
    }

    public Optional<JsonElement> get(String endpoint) {
	HttpRequest request = getBuilderBase().GET().uri(buildURI(endpoint)).build();
	String json;
	try {
	    json = client.send(request, BodyHandlers.ofString()).body();
	    // TODO handle cloudflare error
	    return Optional.of(gson.fromJson(json, JsonElement.class));
	} catch (IOException | InterruptedException e) {
	    e.printStackTrace();
	    return Optional.empty();
	} catch (JsonSyntaxException e) {
	    e.printStackTrace();
	    // TODO return richt error type
	    return Optional.empty();
	}
    }

    protected Builder getBuilderBase() {
	Objects.requireNonNull(useragent, "useragent must be set");
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

    private <T> ApiResponse<T> wrapIntoError(Optional<JsonElement> optionalJson, Class<T> clazz) {
	ApiResponse<T> result = new ApiResponse<>();

	// Network error
	if (optionalJson.isEmpty()) {
	    ApiError error = new ApiError();
	    error.setSuccess(false);
	    error.setReason("Network error");
	    result.setError(error);
	    return result;
	}

	JsonElement json = optionalJson.get();

	// success field is only present on errors
	if (json.getAsJsonObject().getAsJsonPrimitive("success") != null) {
	    ApiError error = gson.fromJson(json, ApiError.class);
	    result.setError(error);
	} else {
	    ApiError error = new ApiError();
	    error.setSuccess(true);
	    error.setReason("");
	    result.setError(error);
	    result.setResponse(gson.fromJson(json, clazz));
	}
	return result;
    }

}
