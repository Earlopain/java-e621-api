package net.c5h8no4na.e621.api.response.partial;

import java.util.Map;
import java.util.Optional;

import lombok.Data;

@Data
public class Sample {
	private boolean has;
	private int height;
	private int width;
	private String url;
	private Map<String, Alternative> alternates;

	public Optional<String> getUrl() {
		return Optional.ofNullable(url);
	}

}
