package net.c5h8no4na.e621.api.response;

import java.util.List;

import lombok.Data;
import net.c5h8no4na.e621.api.response.partial.Post;

@Data
public class MultiplePosts {
    List<Post> posts;
}
