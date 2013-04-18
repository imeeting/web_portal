package com.imeeting.framework;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.imeeting.mvc.model.conference.ConferenceDB;
import com.imeeting.mvc.model.conference.ConferenceManager;
import com.imeeting.mvc.model.contact.ContactDAO;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.notify.Notifier;
import com.richitec.sms.client.SMSClient;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.MailSender;

public class ContextLoader extends ContextLoaderListener {

	public static ApplicationContext appContext;
	public static String appAbsolutePath;

	public void contextDestroyed(ServletContextEvent event) {
		try {
			ComboPooledDataSource ds = (ComboPooledDataSource) appContext
					.getBean("dataSource_mysql_c3p0");
			if (null != ds) {
				ds.close();
			}
		} finally {
			super.contextDestroyed(event);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ServletContext context = event.getServletContext();
		appAbsolutePath = context.getRealPath("/");
		appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(context);
	}

	public static Configuration getConfiguration() {
		return (Configuration) appContext.getBean("imeeting_config");
	}

	public static SMSClient getSMSClient() {
		return (SMSClient) appContext.getBean("sms_client");
	}

	public static DonkeyClient getDonkeyClient() {
		return (DonkeyClient) appContext.getBean("donkey_client");
	}

	public static ConferenceManager getConferenceManager() {
		return (ConferenceManager) appContext.getBean("conference_manager");
	}

	public static Notifier getNotifier() {
		return (Notifier) appContext.getBean("notifier");
	}

	public static ConferenceDB getConferenceDAO() {
		return (ConferenceDB) appContext.getBean("conference_dao");
	}

	public static UserDAO getUserDAO() {
		return (UserDAO) appContext.getBean("user_dao");
	}

	public static ContactDAO getContactDAO() {
		return (ContactDAO) appContext.getBean("contact_dao");
	}

	public static MailSender getMailSender() {
		return (MailSender) appContext.getBean("mail_sender");
	}
}
