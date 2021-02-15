package net.c5h8no4na.e621.api.response.partial;

import lombok.Data;

@Data
public class Flags {
	private boolean pending;
	private boolean flagged;
	private boolean noteLocked;
	private boolean statusLocked;
	private boolean ratingLocked;
	private boolean deleted;
}
