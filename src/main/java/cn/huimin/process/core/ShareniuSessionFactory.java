package cn.huimin.process.core;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

public class ShareniuSessionFactory implements SessionFactory {

	@Override
	public Class<?> getSessionType() {
		return TaskEntityManager.class;

	}
	@Override
	public Session openSession() {
		return new ShareniuTaskEntityManager();
	}

}
