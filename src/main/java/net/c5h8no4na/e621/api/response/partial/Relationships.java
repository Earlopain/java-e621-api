package net.c5h8no4na.e621.api.response.partial;

import java.util.List;

import lombok.Data;

@Data
public class Relationships {
	private Integer parentId;
	private List<Integer> children;
}
