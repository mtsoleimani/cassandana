package io.cassandana.broker.security.cache;

public class CacheEntry<T> {
	
	public CacheEntry() {
		
	}
	
	public CacheEntry(T value) {
		this.value = value;
	}
	
	public T value;
	
	public long created;

}
