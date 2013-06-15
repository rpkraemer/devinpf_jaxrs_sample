package org.devinpf.jaxrs.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.devinpf.jaxrs.Server;
import org.devinpf.jaxrs.dao.TalksDao;
import org.devinpf.jaxrs.model.Rating;
import org.devinpf.jaxrs.model.Talk;
import org.devinpf.jaxrs.util.LinkHeader;
import org.devinpf.jaxrs.util.RestLinkBuilder;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

@Path("/talks")
public class TalkResource {

	private TalksDao talkDao = new TalksDao();

	@GET
	@Produces({ APPLICATION_JSON, APPLICATION_XML })
	@Wrapped(element = "talks")
	public Response getTalks() {
		List<Talk> talks = talkDao.getAll();
		ResponseBuilder resBuilder = Response.ok();
		for (Talk talk : talks) {
			new RestLinkBuilder().buildLinksFor(talk).excluding("ratings", "talks").at(resBuilder);
		}
		return resBuilder.entity(new GenericEntity<List<Talk>>(talks){}).build();
	}

	@GET
	@Path("/{talkId}")
	@Produces({ APPLICATION_JSON, APPLICATION_XML })
	public Response getTalk(@PathParam("talkId") int talkId) throws NotFoundException {
		Talk talk = talkDao.getById(talkId);
		if (talk == null) {
			throw new NotFoundException(String.format("Talk with id %s not found", talkId));
		}
		
		/* cache directive example - 10 seconds */
		CacheControl cc = new CacheControl();
		cc.setMaxAge(10);
		
		ResponseBuilder resBuilder = Response.ok();
		resBuilder.cacheControl(cc);
		new RestLinkBuilder().buildLinksFor(talk).at(resBuilder);
		return resBuilder.entity(talk).build();
	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML })
	public Response createTalk(Talk talk) throws URISyntaxException {
		if (talk.isValid()) {
			talkDao.create(talk);
			List<LinkHeader> links = new ArrayList<LinkHeader>();
			new RestLinkBuilder().buildLinksFor(talk).excluding("ratings", "talks").at(links);
			return Response.created(new URI(links.get(0).getUri())).build();
		} else {
			throw new BadRequestException("Cannot create talk because it's not valid");
		}
	}

	@PUT
	@Path("/{talkId}")
	@Consumes({ APPLICATION_JSON, APPLICATION_XML })
	public Response updateTalk(@PathParam("talkId") int talkId, Talk talk) {
		getTalk(talkId); // if talk doesn't exist, returns error to client
		
		if (talk.isValid()) {
			talkDao.update(talk);
			ResponseBuilder resBuilder = Response.ok();
			new RestLinkBuilder().buildLinksFor(talk).at(resBuilder);
			return resBuilder.entity(talk).build();
		} else {
			throw new BadRequestException("Cannot update talk because it's not valid");
		}
	}
	
	@DELETE
	@Path("/{talkId}")
	public Response deleteTalk(@PathParam("talkId") int talkId) {
		Talk talk = (Talk) getTalk(talkId).getEntity();
		talkDao.delete(talk);
		
		LinkHeader linkTalks = new LinkHeader("talks", Server.BASE_URI + "/talks");
		ResponseBuilder resBuider = Response.noContent();
		new RestLinkBuilder().buildLinks(linkTalks).at(resBuider);
		
		return resBuider.build();
	}
	
	/*
	 * ************ Ratings logic ************
	 */
	@GET
	@Path("/{talkId}/ratings")
	@Wrapped(element = "ratings")
	public Response getTalkRatings(@PathParam("talkId") int talkId) {
		Talk talk = (Talk) getTalk(talkId).getEntity();
		ResponseBuilder resBuilder = Response.ok();
		for (Rating rating : talk.getRatings()) {
			new RestLinkBuilder()
				.buildLinks(new LinkHeader("rating" + rating.getId(), Server.BASE_URI + "/talks/" + talkId + "/ratings/" + rating.getId()))
				.at(resBuilder);
		}
		new RestLinkBuilder()
			.buildLinks(new LinkHeader("talks", Server.BASE_URI + "/talks"))
			.at(resBuilder);
		
		return resBuilder
			.entity(new GenericEntity<List<Rating>>(talk.getRatings()){})
			.build();
	}
	
	@GET
	@Path("/{talkId}/ratings/{ratingId}")
	public Response getTalkRating(@PathParam("talkId") int talkId, @PathParam("ratingId") int ratingId) {
		Talk talk = (Talk) getTalk(talkId).getEntity();
		Rating rating = talkDao.getRating(talk, ratingId);
		if (rating == null) {
			throw new NotFoundException(String.format("Rating with id %s not found", ratingId));
		}
		
		ResponseBuilder resBuilder = Response.ok(rating);
		new RestLinkBuilder()
			.buildLinks(new LinkHeader("rating" + ratingId, Server.BASE_URI + "/talks/" + talkId + "/ratings/" + rating.getId()))
			.at(resBuilder);
		new RestLinkBuilder().buildLinksFor(talk).excluding("ratings", "talks").at(resBuilder); // talk self link
		return resBuilder.build();
	}
	
	@POST
	@Path("/{talkId}/ratings")
	public Response createTalkRating(@PathParam("talkId") int talkId, Rating rating) throws URISyntaxException {
		Talk talk = (Talk) getTalk(talkId).getEntity();
		if (rating.isValid()) {
			talkDao.createRating(talk, rating);
			List<LinkHeader> links = new ArrayList<LinkHeader>();
			LinkHeader ratingLink = new LinkHeader("self", Server.BASE_URI + "/talks/" + talkId + "/ratings/" + rating.getId());
			new RestLinkBuilder().buildLinks(ratingLink).at(links);
			return Response.created(new URI(links.get(0).getUri())).build();
		} else {
			throw new BadRequestException("Cannot create rating because it's not valid");
		}
	}

}
