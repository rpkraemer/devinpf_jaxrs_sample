package org.devinpf.jaxrs.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.devinpf.jaxrs.util.Validatable;

@XmlRootElement(name = "rating")
public class Rating implements Validatable {

	@XmlElement
	private final byte value;
	private int id;

	/*
	 * Required for JAX-B :(
	 */
	public Rating() {
		this.value = -1;
	}

	public Rating(byte value) {
		this.value = value;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlElement
	public int getId() {
		return id;
	}

	public byte getValue() {
		return value;
	}

	@Override
	public boolean isValid() {
		return (getValue() >= 1 && getValue() <= 5);
	}

}
