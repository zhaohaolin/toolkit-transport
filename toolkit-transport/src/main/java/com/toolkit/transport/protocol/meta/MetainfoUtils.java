/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    MetainfoUtils.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 上午11:04:32
 *******************************************************************************/
package com.toolkit.transport.protocol.meta;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.lang.PackageUtil;
import com.toolkit.transport.protocol.annotation.SignalCode;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: MetainfoUtils.java 283 2011-06-07 05:16:40Z qiaofeng $
 */
public class MetainfoUtils {
	
	private final static Logger			LOG		= LoggerFactory
														.getLogger(MetainfoUtils.class);
	private final static MetainfoUtils	UTIL	= new MetainfoUtils();
	
	public static MetainfoUtils getUtil() {
		return UTIL;
	}
	
	private MetainfoUtils() {
		//
	}
	
	static public DefaultMsgCode2TypeMetainfo createTypeMetainfo(
			Collection<String> packages) {
		
		DefaultMsgCode2TypeMetainfo typeMetainfo = new DefaultMsgCode2TypeMetainfo();
		
		if (null != packages) {
			for (String pkgName : packages) {
				try {
					String[] clsNames = PackageUtil.findClassesInPackage(
							pkgName, null, null);
					for (String clsName : clsNames) {
						try {
							// get currentThread context classloader
							ClassLoader cl = Thread.currentThread()
									.getContextClassLoader();
							LOG.debug(
									"using ClassLoader [{}] to load Class [{}]",
									new Object[] { cl, clsName });
							Class<?> cls = cl.loadClass(clsName);
							SignalCode attr = cls
									.getAnnotation(SignalCode.class);
							if (null != attr) {
								int value = attr.messageCode();
								typeMetainfo.add(value, cls);
								LOG.info("metainfo: add [{}]:=>[{}]",
										new Object[] { value, cls });
							}
						} catch (ClassNotFoundException e) {
							LOG.error("createTypeMetainfo: ", e);
						}
					}
				} catch (IOException e) {
					LOG.error("createTypeMetainfo: ", e);
				}
			}
		}
		
		return typeMetainfo;
	}
}
