package cn.huimin.process.core.session;

import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.DefaultHistoryManagerSessionFactory;

public class ShareniuHistoryManagerSessionFactory extends DefaultHistoryManagerSessionFactory {
	public java.lang.Class<?> getSessionType() {
		return HistoryManager.class; 
	}
	
	@Override
	public Session openSession() {
		return new ShareniuHistoryManager();
	}
}
