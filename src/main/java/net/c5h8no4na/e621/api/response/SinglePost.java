package net.c5h8no4na.e621.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.c5h8no4na.e621.api.response.partial.Post;

@Data
@EqualsAndHashCode(callSuper = true)
public class SinglePost extends ApiResponse<SinglePost> {
	private Post post;
}
