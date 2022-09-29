/*
 * Created on Mar 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.util.*;
import java.io.*;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EMailSender {
	
	private Session m_mailSession = null;
	private Transport m_transport = null;
	private String m_FromMailUserName = "";
	public EMailSender(String mailServerName, String mailUserName, String mailPassword, String mailTransportProtocol, String returnEmailAddress)	{
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
	
	public void sendMessage(String subject, String msg, String recipients) throws AddressException, MessagingException	{
		System.out.println("From Mail user = " + m_FromMailUserName + ", recipients = " + recipients);
System.out.println(subject);
		MimeMessage message = new MimeMessage(m_mailSession);
		message.setFrom(new InternetAddress(m_FromMailUserName));
		// create a list of recipients for parsing
		String recipentList = "";
		//recipentList = recipentList.substring(0, recipentList.length() - 1);
		InternetAddress [] ia = InternetAddress.parse(recipients);
		message.setRecipients(Message.RecipientType.TO,	ia);

		message.setSubject(subject);
		message.setText(msg);	
		Multipart mp = new MimeMultipart();
		
		// create and fill the first message part, the body 
		// create text anl url link in email body
		MimeBodyPart mbp2 = new MimeBodyPart();
		mbp2.setText(msg);//set body
	 
		// create the Multipart and its parts to it 
		mp.addBodyPart(mbp2);
	 
		
		
		/*Iterator it = attachments.iterator();
		while (it.hasNext())	{
			Object o = it.next();
			DataSource ds = null;
			if (o instanceof byte[])	{
				ds = new ByteArrayDataSource((byte [])o, mimeType, fileName);
			} else if (o instanceof File)	{
				ds = new FileDataSource((File)o); 
				
			} else if (o instanceof String)	{
				ds = new FileDataSource((String)o); 
				
			}
			MimeBodyPart mbp1 = new MimeBodyPart();
			DataHandler dh = new DataHandler(ds);
			mbp1.setDataHandler(dh); 
			mbp1.setFileName(ds.getName());
			mp.addBodyPart(mbp1);
		}*/
		
		// create and fill the first message part, the body 
//	   MimeBodyPart mbp2 = new MimeBodyPart();
//	   mbp2.setText(msg);//set body
 
	   // create the Multipart and its parts to it 
//	   mp.addBodyPart(mbp2);
 
	   // add the Multipart to the message 
	   message.setContent(mp); 
 
	   Transport.send(message);
		
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
