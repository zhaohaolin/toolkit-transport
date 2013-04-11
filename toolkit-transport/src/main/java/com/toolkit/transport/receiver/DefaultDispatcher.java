package com.toolkit.transport.receiver;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.lang.ClassUtil;
import com.toolkit.lang.SimpleCache;
import com.toolkit.lang.statistic.TransactionStatisticer;
import com.toolkit.transport.course.BizMethod;
import com.toolkit.transport.course.BusinessCourse;

/**
 * @author qiaofeng
 */
public class DefaultDispatcher implements Receiver {
	
	@Override
	public void messageReceived(final Object input) {
		
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				final Object msg = input;
				
				BusinessCourse course = getCourse(msg.getClass());
				if (course == null) {
					LOG.error("No course class found for ["
							+ msg.getClass().getName() + "]. Process stopped.");
					return;
				}
				try {
					if (statisticer != null) {
						statisticer.incHandledTransactionStart();
					}
					// invoke bizMethod 执行业务接口
					invokeBizMethod(course, msg);
					// 统计
					if (statisticer != null) {
						statisticer.incHandledTransactionEnd();
					}
				} catch (Exception e) {
					LOG.error("biz error.", e);
				}
			}
		};
		
		if (mainExecutor != null) {
			mainExecutor.submit(task);
		} else {
			task.run();
		}
		
	}
	
	// 注册业务接口类
	public void setCourses(Collection<BusinessCourse> courses) {
		for (BusinessCourse course : courses) {
			Method[] methods = ClassUtil.getAllMethodOf(course.getClass());
			for (Method method : methods) {
				BizMethod biz = method.getAnnotation(BizMethod.class);
				if (null != biz) {
					Class<?>[] params = method.getParameterTypes();
					if (params.length < 1) {
						continue;
					}
					courseTable.put(params[0], course);
				}
			}
		}
	}
	
	public void setThreads(int threads) {
		this.mainExecutor = Executors.newFixedThreadPool(threads);
	}
	
	public void setStatisticer(TransactionStatisticer statisticer) {
		this.statisticer = statisticer;
	}
	
	private void invokeBizMethod(BusinessCourse course, Object msg) {
		Method bizMethod = getBizMethod(course.getClass(), msg.getClass());
		if (null != bizMethod) {
			try {
				bizMethod.invoke(course, msg);
			} catch (Exception e) {
				LOG.error("Invoke biz method [" + bizMethod.getName()
						+ "] failed. " + e);
			}
		} else {
			LOG.error("No biz method found for message ["
					+ msg.getClass().getName() + "]. No process execute.");
		}
	}
	
	private BusinessCourse getCourse(Class<?> clazz) {
		return courseTable.get(clazz);
	}
	
	private final static Logger		LOG			= LoggerFactory
														.getLogger(DefaultDispatcher.class);
	private TransactionStatisticer	statisticer	= new TransactionStatisticer();
	private static final Method		EMPTY_METHOD;
	
	private static final class Key {
		
		private Class<?>	courseClass;
		private Class<?>	beanClass;
		
		public Key(Class<?> courseClass, Class<?> beanClass) {
			this.courseClass = courseClass;
			this.beanClass = beanClass;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((beanClass == null) ? 0 : beanClass.hashCode());
			result = prime * result
					+ ((courseClass == null) ? 0 : courseClass.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Key))
				return false;
			final Key other = (Key) obj;
			if (beanClass == null) {
				if (other.beanClass != null)
					return false;
			} else if (!beanClass.equals(other.beanClass))
				return false;
			if (courseClass == null) {
				if (other.courseClass != null)
					return false;
			} else if (!courseClass.equals(other.courseClass))
				return false;
			return true;
		}
	}
	
	static {
		Method tmp = null;
		try {
			tmp = Key.class.getMethod("hashCode");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		EMPTY_METHOD = tmp;
	}
	
	private SimpleCache<Key, Method>	bizMethodCache	= new SimpleCache<Key, Method>();
	
	private Method getBizMethod(final Class<?> courseClass,
			final Class<?> beanClass) {
		
		Method ret = bizMethodCache.get(new Key(courseClass, beanClass),
				new Callable<Method>() {
					
					@Override
					public Method call() throws Exception {
						Method[] methods = ClassUtil
								.getAllMethodOf(courseClass);
						
						for (Method method : methods) {
							BizMethod biz = method
									.getAnnotation(BizMethod.class);
							
							if (null != biz) {
								Class<?>[] params = method.getParameterTypes();
								if (params.length < 1) {
									LOG.warn("Method ["
											+ method.getName()
											+ "] found but only ["
											+ params.length
											+ "] parameters found, need to be 1.");
									continue;
								}
								if (params[0].isAssignableFrom(beanClass)) {
									return method;
								}
							}
						}
						
						return EMPTY_METHOD;
					}
				});
		return (ret == EMPTY_METHOD) ? null : ret;
	}
	
	private Map<Class<?>, BusinessCourse>	courseTable		= new HashMap<Class<?>, BusinessCourse>();
	private ExecutorService					mainExecutor	= Executors
																	.newSingleThreadExecutor();
	
}
