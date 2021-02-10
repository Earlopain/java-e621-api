package net.c5h8no4na.e621.api.response.partial;

import java.util.List;

import lombok.Data;

@Data
public class Tags {
	public List<String> general;
	public List<String> species;
	public List<String> character;
	public List<String> copyright;
	public List<String> artist;
	public List<String> invalid;
	public List<String> lore;
	public List<String> meta;
}
