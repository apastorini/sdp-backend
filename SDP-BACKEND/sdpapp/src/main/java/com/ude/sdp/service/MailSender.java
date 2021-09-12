package com.ude.sdp.service;

import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simpleemail.AWSJavaMailTransport;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Contact;
import com.mailjet.client.resource.Email;

@Component("javaMailSender")
public class MailSender {

	@Autowired
	JavaMailSender javaMailSender;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	
	
	public void sendMail2(String to, String subject, String body) {

		try {
			SimpleMailMessage mail = new SimpleMailMessage();

			// mail.setFrom(from);
			/*
			 * mail.setTo(to); mail.setSubject(subject); mail.setText(body);
			 * 
			 * logger.info("Enviando...");
			 * 
			 * javaMailSender.send(mail);
			 */

			/////////////////
			/*
			 * Setup JavaMail to use Amazon SES by specifying the "aws" protocol and our AWS
			 * credentials.
			 */
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "aws");
			props.setProperty("mail.aws.user", "");
			props.setProperty("mail.aws.password", "");

			Session session = Session.getInstance(props);

			// Create a new Message
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("apastorini@sdp.awsapps.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			msg.setSubject(subject);
			msg.setText(body);
			msg.saveChanges();

			// Reuse one Transport object for sending all your messages
			// for better performance
			Transport t = new AWSJavaMailTransport(session, null);
			t.connect();
			t.sendMessage(msg, null);

			// Close your transport when you're completely done sending
			// all your messages.
			t.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMail(String to, String subject, String body) {

		try {
			//https://app.mailjet.com/stats
			MailjetRequest email;
			MailjetResponse response;

			// Note how we set the version to v3.1 using ClientOptions
			MailjetClient client = new MailjetClient("92c37f670f187dc47ae174d608367ffb", "da1be64d6326a935ae1e3fffdc64d0bd");

			JSONArray recipients;
			

			recipients = new JSONArray()
			                .put(new JSONObject().put(Contact.EMAIL,to));
			                
			email = new MailjetRequest(Email.resource)
			                    .property(Email.FROMNAME, "Admin SDP")
			                    .property(Email.FROMEMAIL,  "apastorini@gmail.com")
			                    .property(Email.SUBJECT, subject)
			                    .property(Email.TEXTPART, body)
			                    .property(Email.RECIPIENTS, recipients)
			                    .property(Email.MJCUSTOMID, "JAVA-Email");
				
			response = client.post(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public void sendMailWithAtach(String to, String subject, String body, InputStream reporte, int largoBytes) {
		//sendMail(to, subject, body);
		try {
			//https://app.mailjet.com/stats
			  int byteLength=largoBytes; //bytecount of the file-content
			  //byte[] filecontent = new byte[byteLength];
			  //reporte.read(filecontent,0,byteLength);
			  byte[] filecontent = IOUtils.toByteArray(reporte);
			  byte[] encoded = Base64.getEncoder().encode(filecontent);
			
			//byte[] bytes = IOUtils.toByteArray(reporte);
			//byte[] encoded = Base64.encode(bytes);
			//.getEncoder().encode(reporte);
			MailjetRequest email;
			MailjetResponse response;

			// Note how we set the version to v3.1 using ClientOptions
			MailjetClient client = new MailjetClient("92c37f670f187dc47ae174d608367ffb", "da1be64d6326a935ae1e3fffdc64d0bd");

			JSONArray recipients;
			
			
			recipients = new JSONArray()
			                .put(new JSONObject().put(Contact.EMAIL,to));
			                
			email = new MailjetRequest(Email.resource)
			                    .property(Email.FROMNAME, "Admin SDP")
			                    .property(Email.FROMEMAIL,  "apastorini@gmail.com")
			                    .property(Email.SUBJECT, subject)
			                    .property(Email.TEXTPART, body)
			                    .property(Email.RECIPIENTS, recipients)
			                    .property(Email.MJCUSTOMID, "JAVA-Email")
								.property(Email.ATTACHMENTS, new JSONArray().put(new JSONObject()
										.put("Content-type", "application/pdf")
										.put("Filename", "Resultado.pdf")
										.put("content", encoded)));
	      
			response = client.post(email);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
	}*/
	
	public void sendMailWithAtach2(String to, String subject, String body, InputStream reporte) {

		// MimeMessage message = javaMailSender.createMimeMessage();

		try {
			/*
			 * MimeMessageHelper helper = new MimeMessageHelper(message, true);
			 * 
			 * 
			 * helper.setTo(to); helper.setSubject(subject); helper.setText(body);
			 * 
			 * 
			 * helper.addAttachment("Reporte", new InputStreamResource(reporte));
			 */

			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "aws");
			props.setProperty("mail.aws.user", "AKIAISL3JIDS24E6BDXQ");
			props.setProperty("mail.aws.password", "i66UC8d+yG93y1F9jt4ncYxaROZYenNfJMSadeg3");

			Session session = Session.getInstance(props);

			// Create a new Message
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("apastorini@sdp.awsapps.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			msg.setSubject(subject);
			msg.setText(body);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			// Now set the actual message
			messageBodyPart.setText("Please find the attachment below");
			// Create a multipar message
			Multipart multipart = new MimeMultipart();
			// Set text message part
			multipart.addBodyPart(messageBodyPart);
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();

			DataSource source = new ByteArrayDataSource(reporte, "application/pdf");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("Reporte.pdf");
			multipart.addBodyPart(messageBodyPart);
			// Send the complete message parts
			msg.setContent(multipart);

			msg.saveChanges();

			// Reuse one Transport object for sending all your messages
			// for better performance
			Transport t = new AWSJavaMailTransport(session, null);
			t.connect();
			t.sendMessage(msg, null);

			// Close your transport when you're completely done sending
			// all your messages.
			t.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
