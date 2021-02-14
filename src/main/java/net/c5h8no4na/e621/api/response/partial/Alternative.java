package net.c5h8no4na.e621.api.response.partial;

import java.util.List;

import lombok.Data;

@Data
public class Alternative {
	private String type;
	private Integer height;
	private Integer width;
	private List<String> urls;
}
