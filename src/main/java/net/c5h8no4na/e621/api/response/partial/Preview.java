package net.c5h8no4na.e621.api.response.partial;

import java.util.Optional;

import lombok.Data;

@Data
public class Preview {
	private Integer width;
	private Integer height;
	private String url;

	public Optional<String> getUrl() {
		return Optional.ofNullable(url);
	}
}
