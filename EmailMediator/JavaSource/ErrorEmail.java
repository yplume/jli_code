/*
 * Created on Mar 5, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

//import com.imageaccesscorp.polo.EMailSender.SMTPAuthenticator;

import java.util.*;
import java.io.*;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ErrorEmail {
	
	private Session m_mailSession = null;
	private Transport m_transport = null;
	private String m_FromMailUserName = "";
	public ErrorEmail(String mailServerName, String mailUserName, String mailPassword, String mailTransportProtocol, String returnEmailAddress)	{
		m_FromMailUserName = returnEmailAddress;
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", mailTransportProtocol);
		props.put("mail.host", mailServerName);
		props.put("mail.user", mailUserName);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "25");
		Authenticator auth = new SMTPAuthenticator(mailUserName, mailPassword);
		m_mailSession = Session.getInstance(props, auth);
		try {
			m_transport = m_mailSession.getTransport();			
		} catch ( NoSuchProviderException nspe)	{
			System.out.println("Mail init failed.");
		}
	}
	
	public void sendMessage(String recipients, String subject, String message , String from) throws AddressException, MessagingException	{
		Message msg = new MimeMessage(m_mailSession);
		msg.setFrom(new InternetAddress(m_FromMailUserName));
		InternetAddress [] ia = InternetAddress.parse(recipients);
		msg.setRecipients(Message.RecipientType.TO,	ia);
	    msg.setSubject(subject);
	    msg.setContent(message, "text/plain");
	    Transport.send(msg);
	    System.out.println("Finishing sending.....");
	}
	class SMTPAuthenticator extends Authenticator {
		private String username;
		private String password;
		
		public SMTPAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}
	
}

