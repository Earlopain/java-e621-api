package net.c5h8no4na.e621.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Endpoint {
	TAGS,
	POSTS,
	USERS;

	public String getById(Integer id) {
		return String.format("/%s/%d.json", endpointString(), id);
	}

	public String getByString(String name) {
		String sanitized = URLEncoder.encode(name, StandardCharsets.UTF_8);
		return String.format("/%s/%s.json", endpointString(), sanitized);
	}

	public String get() {
		return getWithParams(Collections.emptyMap());
	}

	public String getWithParams(Map<String, String> queryParams) {
		if (queryParams.size() == 0) {
			return String.format("/%s.json", endpointString());
		} else {
			List<String> queryParts = queryParams.entrySet().stream().map(entry -> {
				String sanitized = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
				return String.format("%s=%s", entry.getKey(), sanitized);
			}).collect(Collectors.toList());
			return String.format("/%s.json?%s", endpointString(), String.join("&", queryParts));
		}
	}

	private String endpointString() {
		return this.name().toLowerCase();
	}
}
