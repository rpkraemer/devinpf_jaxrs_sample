package org.devinpf.jaxrs.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.Response.ResponseBuilder;

public class RestLinkBuilder {
	
	private List<LinkHeader> resourceLinks;

	public RestLinkBuilder buildLinksFor(HypermediaResource hypermediaResource) {
		LinkDefiner definer = new LinkDefiner();
		hypermediaResource.linkDefinitions(definer);
		this.resourceLinks = definer.getLinks();
		return this;
	}
	
	public RestLinkBuilder buildLinks(LinkHeader... links) {
		resourceLinks = Arrays.asList(links);
		return this;
	}
	
	public void at(ResponseBuilder responseBuilder) {
		for (LinkHeader link : resourceLinks)
			responseBuilder.link(link.getUri(), link.getRelation());
		resourceLinks = null;
	}
	
	public void at(List<LinkHeader> linkHeaders) {
		for (LinkHeader link : resourceLinks)		
			linkHeaders.add(link);
		resourceLinks = null;
	}
	
	public RestLinkBuilder excluding(String... links) {
		for (String linkName : links) {
			Iterator<LinkHeader> it = resourceLinks.iterator();
			while (it.hasNext()) {
				LinkHeader linkHeader = it.next();
				if (linkHeader.getRelation().equals(linkName)) {
					it.remove();
				}
			}
		}
		return this;
	}
}