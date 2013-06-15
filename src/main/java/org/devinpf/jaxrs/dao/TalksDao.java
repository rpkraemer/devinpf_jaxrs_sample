package org.devinpf.jaxrs.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.devinpf.jaxrs.model.Rating;
import org.devinpf.jaxrs.model.Speaker;
import org.devinpf.jaxrs.model.Talk;
import org.devinpf.jaxrs.model.Talk.Status;

/**
 * Fake database for REST example
 * @author Robson
 *
 */
public class TalksDao {

	private List<Talk> talks = new ArrayList<Talk>();

	// control ID sequences
	private int talksSeq, ratingsSeq = 0; 
	
	public TalksDao() {
		Talk talkBruno = new Talk();
		talkBruno.setId(++talksSeq);
		talkBruno.setName("Desenvolvimento e publicação de aplicativos iOS para a App Store");
		talkBruno.setDescription("Um overview de como funciona o desenvolvimento e a publicação " +
								 "de aplicativos para iOS, tanto para o público geral quanto para " +
								 "o público corporativo, demonstrando possibilidades e restrições da plataforma.");
		talkBruno.setStatus(Status.FINISHED);
		talkBruno.setSpeaker(new Speaker(1, "Bruno Werminghoff"));
		Rating rating = new Rating((byte) 5);
		rating.setId(++ratingsSeq);
		talkBruno.getRatings().add(rating);
		
		Talk talkRobson = new Talk();
		talkRobson.setId(++talksSeq);
		talkRobson.setName("Introdução ao REST: apresentação e conceitos");
		talkRobson.setDescription("Conhecendo um pouco mais do estilo arquitetural REST e como aplicá-lo na plataforma Java");
		talkRobson.setStatus(Status.IN_PROGRESS);
		talkRobson.setSpeaker(new Speaker(2, "Robson Kraemer"));
		
		Talk talkGuilherme = new Talk();
		talkGuilherme.setId(++talksSeq);
		talkGuilherme.setName("Big Data – Apanhado sobre algumas tecnologias utilizadas na manipulação de “grandes dados”");
		talkGuilherme.setDescription("Big Data – Apanhado sobre algumas tecnologias utilizadas na manipulação de “grandes dados”");
		talkGuilherme.setStatus(Status.PENDING);
		talkGuilherme.setSpeaker(new Speaker(3, "Guilherme Henrique Piasson"));
		
		talks.add(talkBruno);
		talks.add(talkRobson);
		talks.add(talkGuilherme);
	}
	
	public Talk getById(int id) {
		for (Talk t: talks)
			if (t.getId() == id)
				return t;
		return null;
	}
	
	public void create(Talk talk) {
		talk.setId(++talksSeq);
		talks.add(talk);
	}
	
	public void createRating(Talk talk, Rating rating) {
		rating.setId(++ratingsSeq);
		getById(talk.getId()).getRatings().add(rating);
	}
	
	public Rating getRating(Talk talk, int ratingId) {
		for (Rating rating : talk.getRatings()) {
			if (rating.getId() == ratingId)
				return rating;
		}
		return null;
	}

	public void update(Talk talk) {
		Talk actualTalk = getById(talk.getId());
		actualTalk.setName(talk.getName());
		actualTalk.setDescription(talk.getDescription());
		actualTalk.setStatus(talk.getStatus());
		actualTalk.setSpeaker(talk.getSpeaker());
		actualTalk.setRatings(talk.getRatings());
	}
	
	public void delete(Talk talk) {
		talks.remove(talk);
	}

	public List<Talk> getAll() {
		return Collections.unmodifiableList(talks);
	}

	public boolean exists(int talkId) {
		return (getById(talkId) != null);
	}
}
