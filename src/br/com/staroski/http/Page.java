package br.com.staroski.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class represents a web page provided by the {@link Server HTTP server}
 *
 * @author ricardo.staroski
 */
public class Page {

	public final String context;
	public final String resource;

	final HttpHandler handler = httpExchange -> process(httpExchange);

	private Map<String, String> attributes = new HashMap<>();

	public Page(String resource) {
		this(resource, resource);
	}

	public Page(String context, String resource) {
		this.context = context;
		this.resource = resource;
	}

	private byte[] applyAttributes(String content) {
		for (Entry<String, String> attribute : attributes.entrySet()) {
			content = content.replaceAll("\\Q${" + attribute.getKey() + "}\\E", attribute.getValue());
		}
		return content.getBytes();
	}

	private String getContent() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = getClass().getResourceAsStream(resource);
		byte[] buffer = new byte[8192];
		for (int read = -1; (read = in.read(buffer)) != -1; out.write(buffer, 0, read)) {}
		out.flush();
		byte[] bytes = out.toByteArray();
		return new String(bytes);
	}

	private void process(HttpExchange exchange) throws IOException {
		onLoad(ParamsFilter.getParams(exchange));
		byte[] bytes = applyAttributes(getContent());
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
		OutputStream out = exchange.getResponseBody();
		out.write(bytes);
		out.close();
	}

	/**
	 * Called when this page is loading
	 *
	 * @param params
	 *            The parameters received by the request
	 */
	protected void onLoad(Params params) {}

	protected final void setAttribute(String attribute, String value) {
		attributes.put(attribute, value);
	}
}
