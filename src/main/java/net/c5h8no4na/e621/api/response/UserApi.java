package net.c5h8no4na.e621.api.response;

import java.util.Date;
import java.util.Optional;

import lombok.Data;

@Data
public class UserApi implements E621ApiType {
	private Integer id;
	private Date createdAt;
	private String name;
	private Integer level;
	private Integer baseUploadLimit;
	private Integer postUploadCount;
	private Integer postUpdateCount;
	private Integer noteUpdateCount;
	private Boolean isBanned;
	private Boolean canApprovePosts;
	private Boolean canUploadFree;
	private String levelString;
	private Integer avatarId;

	public Optional<Integer> getAvatarId() {
		return Optional.ofNullable(avatarId);
	}
}
