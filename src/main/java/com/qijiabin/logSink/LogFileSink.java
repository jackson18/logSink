package com.qijiabin.logSink;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ========================================================
 * 日 期：2016年10月31日 下午3:38:41
 * 版 本：1.0.0
 * 类说明：自定义flume sink
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class LogFileSink extends AbstractSink implements Configurable {
	
	private static final Logger logger = LoggerFactory.getLogger("SINK");
	
	private static final String	PROP_KEY_ROOTPATH = "rootPath";
	private String rootPath;
	
	
	public void configure(Context context) {
		this.rootPath = context.getString(PROP_KEY_ROOTPATH );
	}
	
	@Override
	public Status process() throws EventDeliveryException {
		Status result = Status.READY;
		Channel ch = getChannel();
		Transaction tx = ch.getTransaction();
		try {
			tx.begin();
			Event event = ch.take();
			
			if (event != null) {
				byte[] data = event.getBody();
				if (data != null) {
					String body = new String(data, "utf-8");
					logger.info("rootPath:" + rootPath +",body: " + body);
				}
			} else {
				result = Status.BACKOFF;
			}

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			tx.close();
		}
		return result;
	}
	
}

