package net.c5h8no4na.e621.api.response.partial;

import lombok.Data;

@Data
public class File {
    private Integer width;
    private Integer height;
    private String ext;
    private Integer size;
    private String md5;
}
