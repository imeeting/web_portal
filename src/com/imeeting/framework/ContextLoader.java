package com.imeeting.framework;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.imeeting.mvc.model.group.GroupManager;
import com.richitec.db.DBHelper;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.notify.Notifier;
import com.richitec.sms.client.SMSClient;
import com.richitec.util.ConfigManager;


public class ContextLoader extends ContextLoaderListener {
	
	public static ApplicationContext appContext;

	public void contextDestroyed(ServletContextEvent event) {
		DBHelper.getInstance().closeAll();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ServletContext context = event.getServletContext();

		// load configuration file
		InputStream configStream = context
				.getResourceAsStream("/WEB-INF/config/Configuration.properties");
		ConfigManager.getInstance().loadConfig(configStream);

		appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		
		DBHelper.getInstance().setAppContext(appContext);
	}
	
	public static SMSClient getSMSClient(){
		return (SMSClient)appContext.getBean("sms_client");
	}
	
	public static DonkeyClient getDonkeyClient(){
		return (DonkeyClient)appContext.getBean("donkey_client");
	}
	
	public static GroupManager getGroupManager(){
		return (GroupManager)appContext.getBean("group_manager");
	}

	public static Notifier getNotifier() {
		return (Notifier)appContext.getBean("notifier");
	}
}
