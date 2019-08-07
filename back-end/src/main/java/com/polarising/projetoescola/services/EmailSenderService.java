package com.polarising.projetoescola.services;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
	
	@Value("${polarising.app.mail.smtp.host}")
	private String smtpHost;
	
	@Value("${polarising.app.mail.smtp.port}")
	private String smtpPort;
	
	@Value("${polarising.app.mail.username}")
	private String username;

	@Value("${polarising.app.mail.password}")
	private String password;
	
	public void SendEmail(String to, String subject, String body, Map<String,String> mapInlineImages) throws MessagingException, IOException {
		Properties props = new Properties();
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.starttls.enable", "true");
		   props.put("mail.smtp.host", smtpHost);
		   props.put("mail.smtp.port", smtpPort);
		   
		   //logging into email account
		   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		      protected PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(username, password);
		      }
		   });
		   Message msg = new MimeMessage(session);
		   msg.setFrom(new InternetAddress(username, false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		   msg.setSubject(subject);
		   msg.setSentDate(new Date());

		   MimeBodyPart messageBodyPart = new MimeBodyPart();
		   messageBodyPart.setContent(body, "text/html; charset=UTF-8");

		   Multipart multipart = new MimeMultipart("related");
		   multipart.addBodyPart(messageBodyPart);
		   
		   // inline images
		   if (mapInlineImages != null && mapInlineImages.size() > 0) {
	            Set<String> setImageID = mapInlineImages.keySet();
	             
	            for (String contentId : setImageID) {
	            	MimeBodyPart imagePart = new MimeBodyPart();
	            	String imageFilePath = mapInlineImages.get(contentId);
	              
	                imagePart.attachFile(imageFilePath);
	                imagePart.addHeader("Content-ID", "<" + contentId + "_cid>");
	                imagePart.addHeader("Content-Type", "image/png");
	                imagePart.setDisposition(MimeBodyPart.INLINE);
	           
	                multipart.addBodyPart(imagePart);
	            }
	            msg.setContent(multipart);
	        }
		   
		   msg.setContent(multipart);
		   Transport.send(msg); 
	}
}
