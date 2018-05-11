package com.it.soul.lab.sql;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7405779117813575373L;
	
	public synchronized void sendEmail(GMailAuthenticator auth, String subject, String message, String[] receivers){
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		try {
			Session session = Session.getDefaultInstance(props 
					, auth);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(auth.getUserEmail()));
			
			int c = 0;
			for (String address : receivers) {
				if(c++ == 0){
					msg.addRecipients(RecipientType.TO, address);
					continue;
				}
				msg.addRecipients(RecipientType.CC, address);
			}
			
			msg.setSubject(subject);
			msg.setText(message);
			
			Transport.send(msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class GMailAuthenticator extends Authenticator {
		
		String userEmail;
		String password;
		public GMailAuthenticator (String email, String password)
		{
			super();
			this.userEmail = email;
			this.password = password;
		}
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(userEmail, password);
		}
		public String getUserEmail() {
			return userEmail;
		}
		public String getPassword() {
			return password;
		}
		
	}


}
