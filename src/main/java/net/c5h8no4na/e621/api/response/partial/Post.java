package net.c5h8no4na.e621.api.response.partial;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Post {
    private Integer id;
    private Date created_at;
    private Date updated_at;
    private File file;
    private Score score;
    private Tags tags;
    private String rating;
    private Integer fav_count;
    private List<String> sources;
    private List<Integer> pools;
    private Relationships relationships;
    private Integer approver_id;
    private Integer uploader_id;
    private String description;
    private Integer comment_count;
    private Boolean is_favorited;
    private Boolean has_notes;
    private Float duration;
}
