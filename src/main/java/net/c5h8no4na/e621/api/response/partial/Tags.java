package net.c5h8no4na.e621.api.response.partial;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public List<String> getAll() {
		return Stream.of(general, species, character, copyright, artist, invalid, lore, meta).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
