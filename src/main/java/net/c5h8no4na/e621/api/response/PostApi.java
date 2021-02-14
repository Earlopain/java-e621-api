package net.c5h8no4na.e621.api.response;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import net.c5h8no4na.e621.api.response.partial.File;
import net.c5h8no4na.e621.api.response.partial.Flags;
import net.c5h8no4na.e621.api.response.partial.Preview;
import net.c5h8no4na.e621.api.response.partial.Relationships;
import net.c5h8no4na.e621.api.response.partial.Sample;
import net.c5h8no4na.e621.api.response.partial.Score;
import net.c5h8no4na.e621.api.response.partial.Tags;

@Data
public class PostApi implements E621ApiType {
	private Integer id;
	private Date createdAt;
	private Date updatedAt;
	private File file;
	private Preview preview;
	private Sample sample;
	private Score score;
	private Tags tags;
	private List<String> lockedTags;
	private Integer changeSeq;
	private Flags flags;
	private String rating;
	private Integer favCount;
	private List<String> sources;
	private List<Integer> pools;
	private Relationships relationships;
	private Integer approverId;
	private Integer uploaderId;
	private String description;
	private Integer commentCount;
	private Boolean hasNotes;
	private Float duration;

	public Date getUpdatedAt() {
		return updatedAt != null ? updatedAt : createdAt;
	}

	public Optional<Integer> getApproverId() {
		return Optional.ofNullable(approverId);
	}

	public Optional<Float> getDuration() {
		return Optional.ofNullable(duration);
	}
}
