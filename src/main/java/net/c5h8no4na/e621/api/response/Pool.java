package net.c5h8no4na.e621.api.response;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Pool {
	private Integer id;
	private String name;
	private Date createdAt;
	private Date updatedAt;
	private Integer creatorId;
	private String description;
	private Boolean isActive;
	private String category;
	private Boolean isDeleted;
	private List<Integer> postIds;
	private String creatorName;
	private Integer postCount;
}
