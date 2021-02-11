package net.c5h8no4na.e621.api.response;

import java.util.Date;

import lombok.Data;

@Data
public class User {
	private Integer id;
	private Date createdAt;
	private String name;
	private Integer baseUploadLimit;
	private Integer postUploadCount;
	private Integer postUpdateCount;
	private Boolean isBanned;
	private Boolean canApprovePosts;
	private Boolean canUploadFree;
	private String levelString;
	private Integer avatarId;
}
