package net.c5h8no4na.e621.api.response;

import java.util.Date;

import lombok.Data;

@Data
public class Tag {
	private Integer id;
	private String name;
	private Integer postCount;
	private String relatedTags;
	private Date relatedTagsUpdatedAt;
	private Integer category;
	private Boolean isLocked;
	private Date createdAt;
	private Date updatedAt;
}
