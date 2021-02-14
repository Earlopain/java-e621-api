package net.c5h8no4na.e621.api.response.partial;

import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class Relationships {
	private Integer parentId;
	private Boolean hasChildren;
	private Boolean hasActiveChildren;
	private List<Integer> children;

	public Optional<Integer> getParentId() {
		return Optional.ofNullable(parentId);
	}
}