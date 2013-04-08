/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    CachedHolder.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 上午09:28:42
 *******************************************************************************/
package com.toolkit.transport.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: CachedHolder.java 381 2011-06-15 02:31:55Z qiaofeng $
 */
public class CachedHolder implements Holder {
	
	private final static Logger	log	= LoggerFactory
											.getLogger(CachedHolder.class);
	private Cache				cache;
	
	/**
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(Cache newCache) {
		this.cache = newCache;
		
		this.cache.getCacheEventNotificationService().registerListener(
				new CacheEventListener() {
					
					@Override
					public void dispose() {
						//
					}
					
					@Override
					public void notifyElementEvicted(Ehcache cache,
							Element element) {
						log.trace("notifyElementEvicted:"
								+ element.getObjectValue());
					}
					
					@Override
					public void notifyElementExpired(Ehcache cache,
							Element element) {
						log.trace("notifyElementExpired:"
								+ element.getObjectValue());
					}
					
					@Override
					public void notifyElementPut(Ehcache cache, Element element)
							throws CacheException {
						log.trace("notifyElementPut:"
								+ element.getObjectValue());
					}
					
					@Override
					public void notifyElementRemoved(Ehcache cache,
							Element element) throws CacheException {
						log.trace("notifyElementRemoved:"
								+ element.getObjectValue());
					}
					
					@Override
					public void notifyElementUpdated(Ehcache cache,
							Element element) throws CacheException {
						log.trace("notifyElementUpdated:"
								+ element.getObjectValue());
					}
					
					@Override
					public void notifyRemoveAll(Ehcache cache) {
						log.trace("notifyRemoveAll.");
					}
					
					@Override
					public Object clone() throws CloneNotSupportedException {
						throw new CloneNotSupportedException();
					}
				});
	}
	
	@Override
	public void put(Object key, Object value) {
		cache.put(new Element(key, value));
	}
	
	@Override
	public Object get(Object key) {
		Element element = cache.get(key);
		if (null != element) {
			return element.getObjectValue();
		}
		return null;
	}
	
	@Override
	public Object getAndRemove(Object key) {
		Object ret = get(key);
		remove(key);
		return ret;
	}
	
	@Override
	public void remove(Object key) {
		cache.remove(key);
	}
	
}
