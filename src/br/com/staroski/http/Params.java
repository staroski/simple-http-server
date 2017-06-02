package br.com.staroski.http;

import java.util.Map;
import java.util.Set;

/**
 * This class represents the request parameters received by the {@link Resource Page}'s {@link Resource#onLoad(Params) onLoad} method
 *
 * @author ricardo.staroski
 */
@SuppressWarnings("unchecked")
public final class Params {

	private final Map<String, Object> map;

	Params(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Params) {
			Params that = (Params) obj;
			return this.map.equals(that.map);
		}
		return false;
	}

	public <T> T get(String name) {
		return (T) map.get(name);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	public Set<String> names() {
		return map.keySet();
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
