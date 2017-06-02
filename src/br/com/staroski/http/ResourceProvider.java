package br.com.staroski.http;

/**
 * Interface that provides {@link Resource resources} to the {@link Server HTTP server}
 *
 * @author ricardo.staroski
 */
public interface ResourceProvider {

	Resource[] getResources();
}
