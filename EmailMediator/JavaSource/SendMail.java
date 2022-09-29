
import java.net.*;
import java.io.*;
import java.util.*;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.common.dkResultSetCursor;
/*import com.ibm.mm.sdk.common.DKDocRoutingServiceICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKLobICM;
import com.ibm.mm.sdk.common.DKParts;
import com.ibm.mm.sdk.common.dkIterator;
*/
import com.ibm.mm.sdk.server.DKDatastoreICM;

import javax.mail.*;
import javax.mail.internet.*;

import java.sql.*;

import javax.naming.*;
import javax.naming.directory.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SendMail   {
	private String m_BaseURL = "";
	private String m_PID = "";
	private String m_WorkPackagePID = "";
	private DKDatastoreICM m_dsICM = null;
	private HashMap m_Attributes = null;
	//private Logger logger = null;
	private EMailSender m_ems = null;
	private DKDocRoutingServiceICM m_RoutingService;
	private Connection m_conn = null;
	private String[] pidArr = null;
	private int m_waitingDays;
	private int m_emailAttempts;
	private int m_waitingDaysCoding;
	private int m_emailAttemptsCoding;
	private String m_serverName;
	private String m_userName;
	private String m_password;
	private String m_mailMessage;
	private String m_dbServerName;
	private String m_ReturnEmailAddress;
	private String m_dbName;
	private String m_Next;
	private String m_Previous;
	private String m_errorAlertEmailAddress;
	private ErrorEmail m_erremail;
	
	
	public SendMail( String mailServerName, String mailUserName, String mailPassword, String mailTransportProtocol, String returnEmailAddress, String errorAlertEmailAddress,
		Connection conn,int waitingDays, int emailAttempts, String cmServerName, String cmUserName, String cmPassword, String mailMessage, String dbServerName, String dbName)	{
		this( mailServerName, mailUserName,mailPassword, mailTransportProtocol, "", returnEmailAddress, errorAlertEmailAddress, conn, waitingDays, emailAttempts, cmServerName,
				cmUserName, cmPassword, mailMessage, dbServerName, dbName);
	}

	public SendMail( String mailServerName, String mailUserName, String mailPassword, String mailTransportProtocol, String pdfPassword, String returnEmailAddress, String errorAlertEmailAddress, 
			Connection conn, int waitingDays, int emailAttempts, String cmServerName, String cmUserName, String cmPassword, String mailMessage, String dbServerName, String dbName)	{
		m_ems = new EMailSender(mailServerName, mailUserName, mailPassword, mailTransportProtocol, returnEmailAddress);
		m_erremail = new ErrorEmail(mailServerName, mailUserName, mailPassword, mailTransportProtocol, returnEmailAddress);
		m_conn = conn;
		m_waitingDays = waitingDays;
		m_emailAttempts = emailAttempts;
		m_serverName = cmServerName;
		m_userName = cmUserName;
		m_password = cmPassword;
		m_mailMessage = mailMessage;
		m_dbServerName = dbServerName;
		m_dbName = dbName;
		m_ReturnEmailAddress = returnEmailAddress;
		m_errorAlertEmailAddress = errorAlertEmailAddress;		
	}
	/**
	 * @return Returns the m_BaseURL.
	 */
	public String getBaseURL() {
		return m_BaseURL;
	}
	/**
	 * @param baseURL The m_BaseURL to set.
	 */
	public void setBaseURL(String baseURL) {
		m_BaseURL = baseURL;
	}

	public void sendMail1() {
		tableMonitor();
	}
	
	
	
	//Read new invoice and update Invoice table
	private String emailLookup(String userID)	{
		
		String apEmail = null;		
		try {
			
			System.out.println("[EmailMediator]LANID in emailLookup = " +userID);					
	        Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); 
	        env.put(Context.PROVIDER_URL, "LDAP://poloralphlauren.com:3268/DC=poloralphlauren,DC=com"); 
	        // Specify LDAPS URL. the LDAP server is offering SSL at port 636. To run this program, you must enable SSL on port 636 on your LDAP server.
	        //env.put(Context.PROVIDER_URL, "ldaps://localhost:636/o=JNDITutorial");
	        env.put(Context.REFERRAL, "throw"); 
	        env.put(Context.SECURITY_AUTHENTICATION, "simple");
	        env.put(Context.SECURITY_CREDENTIALS, "Bravo@9876");
	        //env.put(Context.SECURITY_CREDENTIALS, "$T^M$o#$@;fFLJcPAe-39842e");
	        //env.put(Context.SECURITY_CREDENTIALS, PASSWORD_PATTERN.matcher("$T^M$o#$@;fFLJcPAe-39842e"));
	        //env.put(Context.SECURITY_CREDENTIALS, "\\$T\\^M\\$o\\#\\$\\@\\;fFLJcPAe\\-39842e");
	        env.put(Context.SECURITY_PRINCIPAL, "prlus01\\jli1");
	        //env.put(Context.SECURITY_PRINCIPAL, "app-apimage");
	        
	        System.out.println("[EmailMediator]After LANID in emailLookup .....");				
	        
	        DirContext dirContext = new InitialDirContext(env);
	        SearchControls sc = new SearchControls();
	        String[] attributeFilter = { "mail" };
	        sc.setReturningAttributes(attributeFilter);
	        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        Attributes attrs = null;
	        Attribute attr = null;
	        NamingEnumeration results = dirContext.search("", "(sAMAccountName="+ userID +")", sc);
			System.out.println("[EmailMediator]results.hasMore in readNewInvoices");
			
			while (results.hasMore()) {
				
	          SearchResult sr = (SearchResult) results.next();
	          attrs = sr.getAttributes();
	          attr = attrs.get("mail");
	          apEmail = attr.get().toString().trim();
	        }
			
			if (apEmail.indexOf("'") != -1)
				apEmail = apEmail.replaceAll("'", "''");
			
			System.out.print("[EmailMediator]ldap email = "+apEmail);
			
			//return apEmail;
			
			dirContext.close();
		
		} catch (Exception exc) {
			
	    	System.out.println("[EmailMediator]Error in Exception " + exc);
	        
		} 
		return apEmail;
	}
	
	public void tableMonitor(){
		
		String transNum 		= null;
		//DKDatastoreICM dsICM 	= null;
		Statement stmt 			= null;
		ResultSet rs 			= null;
		ResultSet rs1 			= null;
		int invoiceID 			= 0;
		String query 			= null;
		String type 			= "";
		String company 			= "";
		Connection conn			= null;
		String sql				= null;
		String sql1				= null;
		
		try {
			
				//try {
				  // Load the DB2(R) Universal JDBC Driver with DriverManager
				  //Class.forName("com.ibm.db2.jcc.DB2Driver");
				  //conn = DriverManager.getConnection("jdbc:db2://us-nc-contm2-2:50000/icmnlsdb", "icmadmin", "BigBlue1");
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
				conn = DriverManager.getConnection("jdbc:sqlserver://USNCPDKFXCAP3V;DatabaseName=ICCAApproval;user=test;password=test;integratedSecurity=false");
				//conn = DriverManager.getConnection("jdbc:sqlserver://USNCDVKTMRTR2V;DatabaseName=ICCAApproval;user=test;password=test;integratedSecurity=false");
	                
				  //conn = DriverManager.getConnection("jdbc:db2://us-nc-kweb2-1:50000/ICMNLSDB", "icmadmin", "BigBlue1");
				//}catch (ClassNotFoundException e) {
				//		   e.printStackTrace();
				//}
				System.out.println("[EmailMediator] Start query ....");
				sql = "SELECT RecordID, Status, Approver1, Approver2, SenderID FROM [dbo].[ICCAHeaderInfo] WHERE Status = 'IN PROCESS' OR Status = 'APPROVED' OR Status = 'REJECTED'";
			
				stmt = conn.createStatement();
		
		  	  	rs = stmt.executeQuery(sql);
		  	  	String status 		= "";
		  	  	int emailATTPT 		= 0;
		  	  	String pendingDate	= "";
		  	  	String rejectStatus = "";
		  	  	String senderID		= "";
		  	  	String senderEmail  = "";
		  	  	String approverEmail= "";
			    String msg		    = "";
			  	String recordID		= "";
			  	String approverI    = "";
			  	String approverII   = "";
			  	//int counter			= 1;
			  	
		  	  	while (rs.next()) {
		  	  		recordID 			= rs.getString("RecordID");
				  	status				= rs.getString("Status");
				  	approverI 			= rs.getString("Approver1");
				  	approverII  		= rs.getString("Approver2");
				  	senderID			= rs.getString("SenderID");
				  	
				  	sql = "SELECT RecordID, Status, EmailAttempt, DATEDIFF(DAY, EmailDate, GETDATE()) FROM ICCASendEmail WHERE RecordID = '" + recordID + "'";
					stmt = conn.createStatement();
					System.out.println("[EmailMediator]sql = "+sql);
					rs1 = stmt.executeQuery(sql);

					String sRecordID 	= "";
					String sStatus 		= "";
					String sPendingDate = "0";
					int pDate = 0;
					int sAttpt = 1;
					while (rs1.next()){
						sRecordID 		= rs1.getString("RecordID");
						sStatus			= rs1.getString("Status");
						sAttpt  		= rs1.getInt("EmailAttempt");
						sPendingDate  	= rs1.getString(4);
					}
				
					if (status != null) status = status.trim();
					if (senderID != null) senderID = senderID.trim();
					
					//System.out.println("[EmailMediator] status approverI approverII ePendingDate eRecordID="+status+approverI+approverII+sPendingDate+"==="+sRecordID);					
					
					//System.out.println("[EmailMediator] senderID & senderEmail = "+senderID+senderEmail);
					
					msg = "Please, click on the link below to process the item:" + "\n" + m_mailMessage;
					
					// if new item found then do insert
					if (sRecordID == "") {
						sql1 = "INSERT INTO ICCASendEmail (RecordID, SenderID, Approver1, Approver2, Status, CreatedDate, EmailAttempt) values ('"+ recordID +"','"+ senderID +"','" + approverI + "','" + approverII + "','" + status + "',GETDATE(),1)";
						System.out.println("[EmailMediator]sql1="+sql1);	
						stmt = conn.createStatement();
						System.out.println("[EmailMediator]after sql1");
						stmt.executeUpdate(sql1);
						
						System.out.println("[EmailMediator]status = "+status);				
						if (status.equalsIgnoreCase("In Process")) {
							approverEmail = emailLookup(approverI);
							//userEmail = "jli@imageaccesscorp.com";
							sql1 = "UPDATE ICCASendEmail SET EmailDate=GETDATE() where RecordID = '"+ recordID +"'";
							stmt = conn.createStatement();
							stmt.executeUpdate(sql1);
							System.out.println("[EmailMediator]in process: set email date approverEmail="+approverEmail);	
							m_ems.sendMessage("ICCA SYSTEM NOTIFICATION ", msg, approverEmail);
						}
						
					} else {
						pDate = Integer.parseInt(sPendingDate);
						System.out.println("[EmailMediator]ICCA pDate="+pDate + " m_waitingDays="+m_waitingDays +"  sStatus="+sStatus +"  pDate="+pDate);	
					
						//if (sItemID != null && !status.equalsIgnoreCase("In Process")) {
						// check if status updated in CM table
						if (!status.equalsIgnoreCase(sStatus)) {
							sql1 = "UPDATE ICCASendEmail SET EmailAttempt = " + sAttpt++ + ", Status = '" + status + "',EmailDate=GETDATE() where RecordID = '"+ recordID +"'";
							stmt = conn.createStatement();
							stmt.executeUpdate(sql1);
							senderEmail = emailLookup(senderID);
							System.out.println("[EmailMediator]senderID senderEmail org = "+senderID+senderEmail);	
							//senderEmail = "jli@imageaccesscorp.com";
							//System.out.println("senderEmail test= "+senderEmail);	
							msg = "A submission has been processed. Click the link to logon to ICCA system and view the item." + "\n" + m_mailMessage;
							m_ems.sendMessage("ICCA SYSTEM NOTIFICATION ", msg, senderEmail);
							
						}
						if (sStatus.equalsIgnoreCase("In Process") && (pDate >= m_waitingDays && sAttpt < m_emailAttempts)) {
							approverEmail = emailLookup(approverI);
							//userEmail = "jli@imageaccesscorp.com";
							
							sql1 = "UPDATE ICCASendEmail SET EmailAttempt = " + sAttpt++ + ", EmailDate=GETDATE() where RecordID = '"+ recordID +"'";
							stmt = conn.createStatement();
							stmt.executeUpdate(sql1);
							
							System.out.println("[EmailMediator]approverI approverEmail1 = "+approverI+senderEmail);	
							// send email to approver1
							m_ems.sendMessage("Reminder: ICCA SYSTEM NOTIFICATION ", msg, approverEmail);
			  	  		}
						if (sStatus.equalsIgnoreCase("In Process") && (pDate >= m_waitingDays && sAttpt < m_emailAttempts)) {
							approverEmail = emailLookup(approverII);
							//userEmail = "jli@imageaccesscorp.com";
							
							//sql1 = "UPDATE ICCASendEmail SET EmailAttempt = EmailDate=GETDATE() where RecordID = '"+ recordID +"'";
							sql1 = "UPDATE ICCASendEmail SET EmailAttempt = " + sAttpt++ + ", EmailDate=GETDATE() where RecordID = '"+ recordID +"'";
							stmt = conn.createStatement();
							stmt.executeUpdate(sql1);
							
							System.out.println("[EmailMediator]approverII senderEmail2 = "+approverII+senderEmail);	
							// send email to approver1
							m_ems.sendMessage("Reminder: ICCA SYSTEM NOTIFICATION ", msg, approverEmail);
						}
			  	  		
					}
					recordID = null;
		  	  	}
			
	    	} catch (SQLException sqle)	{
			    sqle.printStackTrace();
			 	System.out.println("[EmailMediator]tableMonitor: Error in retreiving sql = " + sqle.getMessage());
				try{
					m_erremail.sendMessage(m_errorAlertEmailAddress, "Error with tableMonitor: tableMonitor - SQLException", sqle.getMessage(), m_ReturnEmailAddress);
				}catch(Exception ex){
					System.out.println("[EmailMediator]Error Alert Email Exception ="+ex.getMessage());
				}
	    	
		    } catch (Exception exc) {
		    	System.out.println("[EmailMediator]tableMonitor: Error in Exception " + exc );
				try{
					m_erremail.sendMessage(m_errorAlertEmailAddress, "Error with tableMonitor: tableMonitor - Exception", exc.getMessage(), m_ReturnEmailAddress);
				}catch(Exception ex){
					System.out.println("[EmailMediator] Error Alert Email Exception ="+ex.getMessage());
				}
		        exc.printStackTrace();
		        
			} finally {
				
				if (rs != null)	{
					try {
						rs.close();
					} catch (SQLException ex) {
						System.out.println(" [EmailMediator]Error finally ="+ex.getMessage());
					}
					rs = null;
				}
				if (rs1 != null)	{
					try {
						rs1.close();
					} catch (SQLException ex) {
						System.out.println("[EmailMediator] Error finally ="+ex.getMessage());
					}
					rs1 = null;
				}
				if (stmt != null)	{
					try {
						stmt.close();
					} catch (SQLException ex) {
						System.out.println("[EmailMediator] Error finally ="+ex.getMessage());
					}
					stmt = null;
				}
				if (conn != null)	{
					try {
						conn.close();
					} catch (SQLException ex) {
						System.out.println("[EmailMediator] Error finally ="+ex.getMessage());
					}
					conn = null;
				}

			}
    }	
}		