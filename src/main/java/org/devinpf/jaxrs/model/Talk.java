package org.devinpf.jaxrs.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.devinpf.jaxrs.Server;
import org.devinpf.jaxrs.util.HypermediaResource;
import org.devinpf.jaxrs.util.LinkDefiner;
import org.devinpf.jaxrs.util.Validatable;

@XmlRootElement(name = "talk")
public class Talk implements HypermediaResource, Validatable {

	public static enum Status {
		PENDING, IN_PROGRESS, FINISHED
	}

	private int id;
	private String name;
	private String description;
	private Speaker speaker;
	private Status status;
	private List<Rating> ratings = new ArrayList<Rating>();

	public Talk() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Speaker getSpeaker() {
		return speaker;
	}

	public void setSpeaker(Speaker speaker) {
		this.speaker = speaker;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	@XmlElementWrapper(name = "ratings")
	@XmlElement(name = "rating")
	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	@Override
	public void linkDefinitions(LinkDefiner def) {
		def.relation("talk" + getId()).as(Server.BASE_URI + "/talks/" + getId());
		def.relation("talks").as(Server.BASE_URI + "/talks");
		if (getStatus() == Status.FINISHED) {
			def.relation("ratings").as(Server.BASE_URI + "/talks/" + getId() + "/ratings");
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Talk) {
			Talk that = (Talk) obj;
			return this.getId() == that.getId();
		}
		return false;
	}

	@Override
	public boolean isValid() {
		if (onCreate()) {
			return (!this.getName().isEmpty() &&
				    !this.getDescription().isEmpty() &&
				     this.getStatus() != null &&
				     this.getSpeaker() != null);
		} else if (onUpdate()) {
			return (this.getId() != 0 &&
				   !this.getName().isEmpty() &&
				   !this.getDescription().isEmpty() &&
				    this.getStatus() != null &&
				    this.getSpeaker() != null);
		}
		return false;
	}

	private boolean onUpdate() {
		return !onCreate();
	}

	private boolean onCreate() {
		return getId() == 0;
	}
}
