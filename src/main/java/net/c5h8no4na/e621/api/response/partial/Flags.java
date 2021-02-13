package net.c5h8no4na.e621.api.response.partial;

import lombok.Data;

@Data
public class Flags {
	private Boolean pending;
	private Boolean flagged;
	private Boolean noteLocked;
	private Boolean statusLocked;
	private Boolean ratingLocked;
	private Boolean deleted;
}
