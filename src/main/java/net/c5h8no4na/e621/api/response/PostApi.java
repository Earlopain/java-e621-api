package net.c5h8no4na.e621.api.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import net.c5h8no4na.e621.api.response.partial.File;
import net.c5h8no4na.e621.api.response.partial.Relationships;
import net.c5h8no4na.e621.api.response.partial.Score;
import net.c5h8no4na.e621.api.response.partial.Tags;

@Data
public class PostApi implements E621ApiType {
	private Integer id;
	private Date createdAt;
	private Date updatedAt;
	private File file;
	private Score score;
	private Tags tags;
	private String rating;
	private Integer favCount;
	private List<String> sources;
	private List<Integer> pools;
	private Relationships relationships;
	private Integer approverId;
	private Integer uploaderId;
	private String description;
	private Integer commentCount;
	private Boolean isFavorited;
	private Boolean hasNotes;
	private Float duration;
}
