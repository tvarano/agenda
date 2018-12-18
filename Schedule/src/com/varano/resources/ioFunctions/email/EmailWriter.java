//Thomas Varano
//Dec 2, 2018

package com.varano.resources.ioFunctions.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailWriter {
	public static MimeMessage createEmail(String to, String subject,
			String bodyText) throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

//		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO,
				new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
	}
	
	public static void writeEmail(MimeMessage m, File f) throws FileNotFoundException, IOException, MessagingException {
		m.writeTo(new FileOutputStream(f));
	}
}
