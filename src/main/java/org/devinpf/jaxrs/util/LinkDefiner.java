package org.devinpf.jaxrs.util;

import java.util.ArrayList;
import java.util.List;


public class LinkDefiner {
	
	private List<LinkHeader> resourceLinkHeaders;
	private LinkHeader currentLinkHeader;
	
	public LinkDefiner() {
		resourceLinkHeaders = new ArrayList<LinkHeader>();
	}
	
	public LinkDefiner relation(String relation) {
		currentLinkHeader = new LinkHeader();
		currentLinkHeader.setRelation(relation);
		return this;
	}
	
	public void as(String uri) {
		currentLinkHeader.setUri(uri);
		resourceLinkHeaders.add(currentLinkHeader);
	}
	
	public List<LinkHeader> getLinks() {
		return resourceLinkHeaders;
	}
}
