package com.richitec.util;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	private String host = "smtp.exmail.qq.com"; // smtp服务器
	private String user = "noreply@00244dh.com"; // 用户名
	private String pwd = "uutalk123"; // 密码
	private String from = "noreply@00244dh.com"; // 发件人地址
	private List<String> to; // 收件人地址
	private String subject = ""; // 邮件标题
	private String content = ""; // 邮件内容

	public MailSender() {

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public void sendMail(List<String> to, String subject, String content) throws AddressException, MessagingException {
		setAddressAndContent(to, subject, content);
		send();
	}

	public void setAddressAndContent(List<String> to, String subject, String content) {
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	public void send() throws AddressException, MessagingException {
		Properties props = new Properties();
		// 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
		props.put("mail.smtp.host", host);
		// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
		props.put("mail.smtp.auth", "true");
		// 用刚刚设置好的props对象构建一个session
		Session session = Session.getDefaultInstance(props);
		// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
		// 用（你可以在控制台（console)上看到发送邮件的过程）
		session.setDebug(true);
		// 用session为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
		// try {
		// 加载发件人地址
		message.setFrom(new InternetAddress(from));
		// 加载收件人地址
		for(String addr : to){
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(addr));
		}
		// 加载标题
		message.setSubject(subject, "UTF-8");

		// 设置邮件的文本内容
		message.setContent(content, "text/html;charset=utf8");
		// 保存邮件
		message.saveChanges();
		// 发送邮件
		Transport transport = session.getTransport("smtp");
		// 连接服务器的邮箱
		transport.connect(host, user, pwd);
		// 把邮件发送出去
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
