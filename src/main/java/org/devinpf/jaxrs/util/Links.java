package org.devinpf.jaxrs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

public class Links {

	private List<LinkHeader> links;
	
	public static Links extractFromResponse(Response response) {
		String[] links = response.getHeaderString("Link").split(",");
		Pattern linkPattern = Pattern.compile("<(.+)>; rel=\"(.+)\"");
		
		List<LinkHeader> linkHeaders = new ArrayList<LinkHeader>();
		
		for (String linkHeader : links) {
			Matcher matcher = linkPattern.matcher(linkHeader);
			if (matcher.matches()) {
				String uri = matcher.group(1);
				String rel = matcher.group(2);
				linkHeaders.add(new LinkHeader(rel, uri));
			}
		}
		
		return new Links(linkHeaders);
	}
	
	private Links(List<LinkHeader> links) {
		this.links = links;
	}
	
	public LinkHeader getLink(String relation) {
		if (relation == null) {
			throw new IllegalArgumentException("Inform relation to search");
		}
		for (LinkHeader link : links) {
			if (relation.equals(link.getRelation())) {
				return link;
			}
		}
		return null;
	}
	
	public boolean hasLink(String relation) {
		return (getLink(relation) != null);
	}
}
