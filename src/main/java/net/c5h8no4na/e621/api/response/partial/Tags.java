package net.c5h8no4na.e621.api.response.partial;

import java.util.List;

import lombok.Data;

@Data
public class Tags {
	private List<String> general;
	private List<String> species;
	private List<String> character;
	private List<String> copyright;
	private List<String> artist;
	private List<String> invalid;
	private List<String> lore;
	private List<String> meta;
}
