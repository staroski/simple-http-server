package br.com.staroski.http;

/**
 * Interface that provides {@link Page pages} to the {@link Server HTTP server}
 *
 * @author ricardo.staroski
 */
public interface PageProvider {

	Page[] getPages();
}
