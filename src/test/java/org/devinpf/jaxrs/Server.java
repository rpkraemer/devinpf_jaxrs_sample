package org.devinpf.jaxrs;

import org.devinpf.jaxrs.resource.TalkResource;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

public class Server {

	public static short PORTA = 4444;
	public static final String BASE_URI = "http://127.0.0.1:" + PORTA;
	private static Server server;

	private final TJWSEmbeddedJaxrsServer tjws;

	public Server() {
		this(PORTA);
	}

	public Server(short porta) {
		PORTA = porta;
		tjws = new TJWSEmbeddedJaxrsServer();
		tjws.setPort(porta);
		tjws.start();
		tjws.getDeployment()
			.getRegistry()
			.addSingletonResource(new TalkResource());
	}

	public void stop() {
		tjws.stop();
	}

	public static void iniciar() {
		server = new Server();
	}

	public static void parar() {
		server.stop();
	}
	
	public static void main(String[] args) {
		Server.iniciar();
	}
}
