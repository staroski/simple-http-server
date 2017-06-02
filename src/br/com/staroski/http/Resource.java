package br.com.staroski.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.MimetypesFileTypeMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class represents a web page provided by the {@link Server HTTP server}
 *
 * @author ricardo.staroski
 */
public class Resource {

	public final String context;
	public final String resource;

	final HttpHandler handler = httpExchange -> process(httpExchange);

	private Map<String, String> attributes = new HashMap<>();

	public Resource(String resource) {
		this(resource, resource);
	}

	public Resource(String context, String resource) {
		this.context = context;
		this.resource = resource;
	}

	private byte[] applyAttributes(byte[] bytes) {
		String text = new String(bytes);
		for (Entry<String, String> attribute : attributes.entrySet()) {
			text = text.replaceAll("\\Q${" + attribute.getKey() + "}\\E", attribute.getValue());
		}
		return text.getBytes();
	}

	private byte[] getContent() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = getClass().getResourceAsStream(resource);
		byte[] buffer = new byte[8192];
		for (int read = -1; (read = in.read(buffer)) != -1; out.write(buffer, 0, read)) {}
		out.flush();
		return out.toByteArray();
	}

	private void process(HttpExchange exchange) throws IOException {
		onLoad(ParamsFilter.getParams(exchange));
		byte[] bytes = getContent();
		String contentType = getContentType();
		if (contentType.startsWith("text")) {
			bytes = applyAttributes(bytes);
		}
		exchange.getResponseHeaders().set("Content-Type", contentType);
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
		OutputStream out = exchange.getResponseBody();
		out.write(bytes);
		out.close();
	}

	// TODO implement a map based on official list
	// https://www.iana.org/assignments/media-types/media-types.xhtml
	protected String getContentType() {
		int index = resource.lastIndexOf('.');
		if (index > -1) {
			String extension = resource.substring(index).toLowerCase();
			if (".css".equalsIgnoreCase(extension)) {
				return "text/css";
			}
			if (".jpg".equalsIgnoreCase(extension)) {
				return "image/png";
			}
		}
		MimetypesFileTypeMap typeMap = new MimetypesFileTypeMap();
		String contentType = typeMap.getContentType(resource);
		return contentType;
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
