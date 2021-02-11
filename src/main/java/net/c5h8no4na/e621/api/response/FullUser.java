package net.c5h8no4na.e621.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FullUser extends User {
	private Integer wikiPageVersionCount;
	private Integer artistVersionCount;
	private Integer poolVersionCount;
	private Integer forumPostCount;
	private Integer commentCount;
	private Integer appealCount;
	private Integer flagCount;
	private Integer positiveFeedbackCount;
	private Integer neutralFfeedbackCount;
	private Integer negativeFeedbackCount;
	private Integer uploadLimit;
}
