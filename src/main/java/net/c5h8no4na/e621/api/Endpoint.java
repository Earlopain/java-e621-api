package net.c5h8no4na.e621.api;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Endpoint {
	TAGS,
	POSTS;

	private static final String base = "https://e621.net";

	public URI getById(Integer id) {
		String url = String.format(base + "/%s/%d.json", endpointString(), id);
		return URI.create(url);
	}

	public URI getByString(String name) {
		String sanitized = URLEncoder.encode(name, StandardCharsets.UTF_8);
		String url = String.format(base + "/%s/%s.json", endpointString(), sanitized);
		return URI.create(url);
	}

	public URI get() {
		return getWithParams(Collections.emptyMap());
	}

	public URI getWithParams(Map<String, String> queryParams) {
		List<String> queryParts = queryParams.entrySet().stream().map(entry -> {
			String sanitized = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
			return String.format("%s=%s", entry.getKey(), sanitized);
		}).collect(Collectors.toList());
		String url = String.format(base + "/%s.json?%s", endpointString(), String.join("&", queryParts));
		return URI.create(url);
	}

	private String endpointString() {
		return this.name().toLowerCase();
	}
}
