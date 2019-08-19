package com.github.windbender.chpcadscraper;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailAlertListener implements AlertListener{
	Logger logger = LoggerFactory.getLogger(EmailAlertListener.class);

	String from;
	String to;
	String mailHost;
	final String username;
	final String password;
	public EmailAlertListener(String from, String to, String mailHost, String username,String password) {
		if(mailHost == null) throw new IllegalArgumentException("mailhost can't be null");
		this.from = from;
		this.to = to;
		this.mailHost = mailHost;
		this.username = username;
		this.password = password;
	}

	public void alertAdded(List<CHPEvent> events) {
		CHPEvent first = events.get(0);
		String subject =""+first.type+" at "+first.location;
		String url = "http://cad.chp.ca.gov";

		String msg = "link to detail: <a href=\""+url+"\" >Location: "+first.location+ " :  "+first.locationDesc+"</a>";
		msg = msg + "<p>map: <a href=\""+"https://www.google.com/maps/place/"+first.getLat()+" "+first.getLon()+"\">google map</a>";
		msg = msg + "<p>More details:<p>";
		for(CHPEvent e: events) {
			msg = msg + e.type+" at "+e.location +"<p>";
			for(CHPLine l: e.lines){ 
				msg = msg +"&nbsp;&nbsp;"+l.time+" "+l.detail+"<p>";
			}
		}
		String[] parts = this.to.split(",");
		for(String part: parts) {
			sendMessage(part,msg,subject,from);
		}
	}

	public void alertGone(List<CHPEvent> events) {
		// TODO Auto-generated method stub

	}

	public void alertChanged(List<CHPEvent> events) {
		// TODO Auto-generated method stub

	}

	public void emailAdmin(String msg) {
		String subject ="cad status";

		String[] parts = this.to.split(",");
		String adminTo = parts[0];
		sendMessage(adminTo,msg,subject,from);
	}

	@Override
	public void alertNws(Feature feature) {
		Map<String, Object> props = feature.getProperties();

		String sent = (String)props.get("sent");
		String start = (String)props.get("onset");
		String end = (String)props.get("ends");
		String desc = (String)props.get("description");
		desc = desc.replaceAll("\n","<p>");
		String subject =(String)props.get("event") +" at "+start;

		String msg = "Issued: "+sent+"<p>";
		msg = msg + "Starts: "+start+"<p>";
		msg = msg + "Ends: "+end+"<p>";

		// Perhaps add a forecast link:
        //TODO  https://forecast.weather.gov/MapClick.php?w0=t&w1=td&w2=hi&w3=sfcwind&w3u=1&w4=sky&w6=rh&w11u=1&w12u=1&Submit=Submit&FcstType=graphical&textField1=38.5175&textField2=-122.6175&site=all&unit=0&dd=&bw=
		msg = msg + "Desc: "+desc+"<p>";

//		String[] parts = this.to.split(",");
//		for(String part: parts) {
//			sendMessage(part,msg,subject,from);
//		}
		String[] parts = this.to.split(",");
		String adminTo = parts[0];
		sendMessage(adminTo,msg,subject,from);

	}

	@Override
	public void alertNwsRemoved(Feature feature) {

	}


	protected boolean sendMessage(String toEmail, String textBody, String subject, String fromAddress) {
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
			message.setSubject("CHP:"+subject);
			// Send the actual HTML message, as big as you like
			message.setContent(
					textBody,
					"text/html");

			// Send message
			Transport.send(message);
			return true;
		} catch (AddressException e) {
			logger.error(" can't send email to "+toEmail+ " because",e);
		} catch (MessagingException e) {
			logger.error(" can't send email to "+toEmail+ " because",e);
		}
		return false;

	}

}
