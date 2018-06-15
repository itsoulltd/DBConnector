package com.it.soul.lab.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailService implements Serializable{
	
	public enum ContentType{
		HTML("text/html"),
		PLAIN("text/plain");
		
		private String contentValue;
		private ContentType(String str) {
			contentValue = str;
		}
		
		@Override
		public String toString() {
			return contentValue;
		}
	}
	
	public enum MailServiceType{
		TLS,
		SSL
	}

	private static final long serialVersionUID = 7405779117813575373L;
	private String subject;
	private String[] receivers;
	private Authenticator auth;
	private Properties props;
	private MailServiceType type;
	private Session session;
	
	public static class Builder {
		private MailService temp;
		
		public Builder(MailServiceType type) {
			temp = new MailService();
			temp.type = type;
			Properties props = new Properties();
			if(type == MailServiceType.TLS) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");
				//props.put("mail.smtp.debug", "true");
			}else {
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");
				//props.put("mail.smtp.debug", "true");
			}
			temp.setProps(props);
		}
		public MailService build() {
			return temp;
		}
		public Builder createAuth(String email, String password) {
			GMailAuthenticator auth = new GMailAuthenticator(email, password);
			temp.setAuth(auth);
			return this;
		}
		public Builder subject(String subject) {
			temp.setSubject(subject);
			return this;
		}
		public Builder receivers(String...vers) {
			temp.setReceivers(vers);
			return this;
		}
	}
	
	@SuppressWarnings("static-access")
	public synchronized void sendMail(String message, ContentType contentType){
		try {
			MimeMessage msg = createMessage();
			if(contentType == null) {
				msg.setText(message);
			}else {
				msg.setContent(message, contentType.toString());
			}
			if(type == MailServiceType.TLS) {
				Transport tps = session.getTransport("smtps");
				tps.send(msg);
				tps.close();
			}else {
				Transport.send(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	public synchronized void sendMail(File html){
		try {
			MimeMessage msg = createMessage();
			BufferedReader br = new BufferedReader(new FileReader(html));
			try {
			    StringBuilder sb = new StringBuilder();
			    String line;
			    while ((line = br.readLine()) != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			    }
			    String text = sb.toString();
			    msg.setContent(text, ContentType.HTML.toString());
			} finally {
			    br.close();
			}
			if(type == MailServiceType.TLS) {
				Transport tps = session.getTransport("smtps");
				tps.send(msg);
				tps.close();
			}else {
				Transport.send(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private MimeMessage createMessage() throws AddressException, MessagingException {
		session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(((GMailAuthenticator)auth).getUserEmail()));
		int c = 0;
		for (String address : receivers) {
			if(c++ == 0){
				msg.addRecipients(RecipientType.TO, address);
				continue;
			}
			msg.addRecipients(RecipientType.CC, address);
		}
		msg.setSubject(subject);
		return msg;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setReceivers(String[] receivers) {
		this.receivers = receivers;
	}

	public void setAuth(GMailAuthenticator auth) {
		this.auth = auth;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	private static class GMailAuthenticator extends Authenticator {
		private String userEmail;
		private String password;
		public GMailAuthenticator (String email, String password){
			super();
			this.userEmail = email;
			this.password = password;
		}
		public PasswordAuthentication getPasswordAuthentication(){
			return new PasswordAuthentication(getUserEmail(), getPassword());
		}
		public String getUserEmail() {
			return userEmail;
		}
		public String getPassword() {
			return password;
		}
	}


}
