package net.c5h8no4na.e621.api.response.partial;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Post {
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
