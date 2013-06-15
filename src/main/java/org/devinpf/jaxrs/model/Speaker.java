package org.devinpf.jaxrs.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "speaker")
public class Speaker {

	private int id;
	private String name;

	public Speaker() {
	}

	public Speaker(int id, String name) {
		super();
		this.id = id;
		this.name = name;
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
}
