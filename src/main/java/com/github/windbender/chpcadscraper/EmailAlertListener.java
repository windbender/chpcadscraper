package com.github.windbender.chpcadscraper;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailAlertListener implements AlertListener {
	String from;
	String to;
	String mailHost;
	final String username;
	final String password;
	public EmailAlertListener(String from, String to, String mailHost, String username,String password) {
		this.from = from;
		this.to = to;
		this.mailHost = mailHost;
		this.username = username;
		this.password = password;
	}

	public void alertAdded(List<CHPEvent> events) {
		CHPEvent first = events.get(0);
		String subject =""+first.type+" at "+first.location;
		String msg = "More details:<p>";
		for(CHPEvent e: events) {
			msg = msg + e.type+" at "+e.location +"<p>";
			for(CHPLine l: e.lines){ 
				msg = msg +"&nbsp;&nbsp;"+l.time+" "+l.detail+"<p>";
			}
		}
		sendMessage(this.to,msg,subject,from);
	}

	public void alertGone(List<CHPEvent> events) {
		// TODO Auto-generated method stub

	}

	public void alertChanged(List<CHPEvent> events) {
		// TODO Auto-generated method stub

	}

	protected void sendMessage(String toEmail, String textBody, String subject, String fromAddress) {
		try {
			// Get system properties

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", this.mailHost);
			props.put("mail.smtp.port", "587");

			// Setup mail server
			final String u = this.username;
			final String p = this.password;
			// Get the default Session object.
			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(
									u,p);
						}
					});

			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(fromAddress));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

			// Set Subject: header field
			message.setSubject("CADCHP:"+subject);
			// Send the actual HTML message, as big as you like
			message.setContent(
					textBody,
					"text/html");

			// Send message
			Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
