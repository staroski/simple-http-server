package br.com.staroski.http;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * A simple HTTP Server to embbed in Java SE applications
 *
 * @author ricardo.staroski
 */
public final class Server {

	private HttpServer server;
	private final PageProvider provider;

	public Server(PageProvider provider) {
		this.provider = provider;
	}

	public void start(int port) throws Throwable {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		for (Page page : provider.getPages()) {
			HttpContext context = server.createContext(page.context, page.handler);
			context.getFilters().add(new ParamsFilter());
		}
		server.start();
	}

	public void stop() {
		if (server != null) {
			server.stop(1);
		}
	}
}
