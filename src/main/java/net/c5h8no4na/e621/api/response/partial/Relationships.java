package net.c5h8no4na.e621.api.response.partial;

import java.util.List;

import lombok.Data;

@Data
public class Relationships {
	public Integer parent_id;
	public List<Integer> children;
}
