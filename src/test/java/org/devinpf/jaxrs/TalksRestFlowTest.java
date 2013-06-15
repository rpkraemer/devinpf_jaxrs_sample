package org.devinpf.jaxrs;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.devinpf.jaxrs.model.Speaker;
import org.devinpf.jaxrs.model.Talk;
import org.devinpf.jaxrs.util.LinkHeader;
import org.devinpf.jaxrs.util.Links;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TalksRestFlowTest {

	private Client client;
	private WebTarget app;
	
	@Before
	public void setUp() {
		client = ClientBuilder.newClient();
		app = client.target(Server.BASE_URI);
		Server.iniciar();
	}
	
	@After
	public void tearDown() {
		Server.parar();
	}
	
	@Test
	public void shouldGetAllTalks() {
		Response res = app.path("/talks").request(APPLICATION_JSON).get();
		List<Talk> talks = res.readEntity(new GenericType<List<Talk>>(){});
		Assert.assertEquals(Status.OK.getStatusCode(), res.getStatus());
		Assert.assertEquals(3, talks.size());
		
		Links links = Links.extractFromResponse(res);
		Assert.assertEquals(Server.BASE_URI + "/talks/1", links.getLink("talk1").getUri());
		Assert.assertEquals(Server.BASE_URI + "/talks/2", links.getLink("talk2").getUri());
		Assert.assertEquals(Server.BASE_URI + "/talks/3", links.getLink("talk3").getUri());
		Assert.assertFalse(links.hasLink("talk?"));
	}
	
	@Test
	public void shouldGetBrunoTalk() {
		Response res = app.path("/talks").request(APPLICATION_JSON).get();
		Links links = Links.extractFromResponse(res);
		res.close();
		
		if (links.hasLink("talk1")) {
			LinkHeader linkTalkBruno = links.getLink("talk1");
			Talk talkBruno = client.target(linkTalkBruno.getUri())
				.request(APPLICATION_JSON)
				.get(Talk.class);
			Assert.assertEquals("Bruno Werminghoff", talkBruno.getSpeaker().getName());
			Assert.assertEquals(5, talkBruno.getRatings().get(0).getValue());
		} else {
			Assert.fail("Response did not contain required link");
		}
	}
	
	@Test
	public void shouldCreateNewTalk() {
		Talk talk = createTalk();
		Response res = app.path("/talks")
			.request(APPLICATION_JSON)
			.post(Entity.entity(talk, APPLICATION_JSON));
		
		Assert.assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
	}
	
	@Test
	public void shouldUpdateExistingTalk() {
		Talk talk = createTalk();
		String talkName = talk.getName();
		Response res = app.path("/talks")
			.request(APPLICATION_JSON)
			.post(Entity.entity(talk, APPLICATION_JSON));
		
		String createdTalkUri = res.getHeaderString("Location"); 
		res.close();
		talk = null;
		
		res = client.target(createdTalkUri).request(APPLICATION_JSON).get();
		talk = res.readEntity(Talk.class);
		Assert.assertEquals(talkName, talk.getName());
		Assert.assertTrue(talk.getId() == 4);
		
		Links links = Links.extractFromResponse(res);
		res.close();
		
		if (links.hasLink("talk4")) {
			LinkHeader linkTalk4 = links.getLink("talk4");
			talk.setDescription("Other Description");
			res = client.target(linkTalk4.getUri())
				.request(APPLICATION_JSON)
				.put(Entity.entity(talk, APPLICATION_JSON));
			
			Assert.assertEquals(Status.OK.getStatusCode(), res.getStatus());
			res.close();
			
			talk = client.target(linkTalk4.getUri()).request(APPLICATION_JSON).get(Talk.class);
			Assert.assertEquals("Other Description", talk.getDescription());
		} else {
			Assert.fail("Response did not contain required link");
		}
	}
	
	@Test
	public void shouldDeleteTalk() {
		Response res = app.path("/talks").request(APPLICATION_JSON).get();
		Links links = Links.extractFromResponse(res);
		res.close();
		
		if (links.hasLink("talk2")) {
			LinkHeader talk2Link = links.getLink("talk2");
			res = client.target(talk2Link.getUri()).request(APPLICATION_JSON).delete();
			Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
		} else {
			Assert.fail("Response did not contain required link");
		}
	}

	private Talk createTalk() {
		Talk talk = new Talk();
		talk.setName("Talk Test");
		talk.setDescription("Description for talk test");
		talk.setSpeaker(new Speaker(4, "Some speaker"));
		talk.setStatus(org.devinpf.jaxrs.model.Talk.Status.PENDING);
		return talk;
	}
}
