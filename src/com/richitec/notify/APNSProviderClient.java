/**
 * 
 */
package com.richitec.notify;

import java.util.ArrayList;
import java.util.List;

import com.imeeting.framework.ContextLoader;

import javapns.communication.exceptions.KeystoreException;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.NotificationProgressListener;
import javapns.notification.transmission.NotificationThread;
import javapns.notification.transmission.NotificationThreads;

/**
 * @author chelsea zhai
 * 
 */
public class APNSProviderClient {

	// apple push notification server
	private AppleNotificationServer _apns = null;

	private String notifyThreadsMaxNumber;
	private String privateKeyPwd;
	private String cerFilePath;
	private boolean production;

	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	public String getPrivateKeyPwd() {
		return privateKeyPwd;
	}

	public void setPrivateKeyPwd(String privateKeyPwd) {
		this.privateKeyPwd = privateKeyPwd;
	}

	public String getCerFilePath() {
		return cerFilePath;
	}

	public void setCerFilePath(String cerFilePath) {
		this.cerFilePath = cerFilePath;
	}

	public String getNotifyThreadsMaxNumber() {
		return notifyThreadsMaxNumber;
	}

	public void setNotifyThreadsMaxNumber(String notifyThreadsMaxNumber) {
		this.notifyThreadsMaxNumber = notifyThreadsMaxNumber;
	}

	/**
	 * private constructor
	 */
	public APNSProviderClient() {
	}

	public void initAPNS() {
		if (_apns == null) {
			try {
				_apns = new AppleNotificationServerBasicImpl(
						ContextLoader.appAbsolutePath + this.cerFilePath,
						this.privateKeyPwd, this.production);
			} catch (KeystoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * push notification to prescript device with payload
	 * 
	 * @param notifyDeviceTokens
	 * @param payload
	 */
	public void pushNotification(List<String> notifyDeviceTokens,
			PushNotificationPayload payload) {
		// device payloads list
		List<PayloadPerDevice> devicesPayloadList = new ArrayList<PayloadPerDevice>();

		// add per device payload to list
		for (String deviceToken : notifyDeviceTokens) {
			// generate per device payload
			PayloadPerDevice devicePayload = null;
			try {
				devicePayload = new PayloadPerDevice(payload, deviceToken);
			} catch (InvalidDeviceTokenFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			devicesPayloadList.add(devicePayload);
		}

		// get notification threads max number
		Integer notifyThreadsMaxNumber = Integer
				.parseInt(this.notifyThreadsMaxNumber);

		// create and init push notification threads pool
		NotificationThreads pushNotificationThreadsPool = new NotificationThreads(
				_apns,
				devicesPayloadList,
				devicesPayloadList.size() <= notifyThreadsMaxNumber ? devicesPayloadList
						.size() : notifyThreadsMaxNumber);
		// add listener
		pushNotificationThreadsPool
				.setListener(new NotificationProgressListener() {

					@Override
					public void eventThreadStarted(
							NotificationThread notificationThread) {
						System.out.println("[EVENT]: thread #"
								+ notificationThread.getThreadNumber()
								+ " started with "
								+ " devices beginning at message id #"
								+ notificationThread
										.getFirstMessageIdentifier());
					}

					@Override
					public void eventThreadFinished(
							NotificationThread notificationThread) {
						System.out.println("[EVENT]: thread #"
								+ notificationThread.getThreadNumber()
								+ " finished: pushed messages #"
								+ notificationThread
										.getFirstMessageIdentifier() + " to "
								+ notificationThread.getLastMessageIdentifier()
								+ " toward " + " devices");
					}

					@Override
					public void eventCriticalException(
							NotificationThread notificationThread,
							Exception exception) {
						System.out
								.println("[EVENT]: critical exception occurred: "
										+ exception);
					}

					@Override
					public void eventConnectionRestarted(
							NotificationThread notificationThread) {
						System.out.println("[EVENT]: connection restarted in thread #"
								+ notificationThread.getThreadNumber()
								+ " because it reached "
								+ notificationThread
										.getMaxNotificationsPerConnection()
								+ " notifications per connection");
					}

					@Override
					public void eventAllThreadsStarted(
							NotificationThreads notificationThreads) {
						System.out.println("[EVENT]: all threads started: "
								+ notificationThreads.getThreads().size());
					}

					@Override
					public void eventAllThreadsFinished(
							NotificationThreads notificationThreads) {
						System.out.println("[EVENT]: all threads finished: "
								+ notificationThreads.getThreads().size());
					}
				});
		// start push notification threads pool
		pushNotificationThreadsPool.start();
		// wait for all threads
		try {
			pushNotificationThreadsPool.waitForAllThreads();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
