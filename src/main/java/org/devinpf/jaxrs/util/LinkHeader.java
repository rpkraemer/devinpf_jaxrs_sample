package org.devinpf.jaxrs.util;

public class LinkHeader {

	private String relation;
	private String uri;

	public LinkHeader() {

	}

	public LinkHeader(String relation, String uri) {
		this.relation = relation;
		this.uri = uri;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LinkHeader) {
			LinkHeader that = (LinkHeader) obj;
			return this.getRelation().equals(that.getRelation());
		}
		return false;
	}
}
