package net.c5h8no4na.e621.api.response;

import java.util.Date;

import lombok.Data;

@Data
public class Tag {
	private Integer id;
	private String name;
	private Integer post_count;
	private String related_tags;
	private Date related_tags_updated_at;
	private Integer category;
	private Boolean is_locked;
	private Date created_at;
	private Date updated_at;
}
