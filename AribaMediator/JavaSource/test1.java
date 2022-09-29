import java.util.ResourceBundle;
import java.util.Scanner;
import java.text.Normalizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.util.Formatter;

public class test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("\\$T\\^M\\$o\\#\\$\\@\\;fFLJcPAe\\-39842e");
		//String test = "J-[,\_";
		try{
		FileInputStream fileInputStream = new FileInputStream("C:\\ZipTest\\test.txt");
	    BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
		  
	    String fileName0 = "2022_04_13_AN01473386171_INV_PL900_FromUS_ToUS_attachments0_*PL900_PRL_Apr:il_13,_2022_Job#5904.pages.pdf";
	    String fileName1 = "2022_04_13_AN01473386171_INV_PL900_FromUS_ToUS_attachments0_*PL900_778935_|_Hoefler_&_13,_2022_Job#5904.pages.pdf";
	    File newFile = new File("c:\\ZipTest\\ZipOutput\\2021-12-8T13046782\\2021_12_07_AN01971828833_INV_111_FromUS_ToUS_attachments0_A.D._Invoice_#111_|_Ralph_Lauren_Corp_|_12_4_|_9000153318_|_Men's_Lookbook_Shoot_J-000009.pdf");
	    new File(newFile.getParent()).mkdirs();
	    FileOutputStream fos = new FileOutputStream(newFile);
        
	    fileName1 = fileName1.replaceAll("[\\W]&&[^_.]","_");
	    fileName1 = fileName1.replaceAll("[*:,#|&]","_");
	    fileName0 = fileName0.replace("[*:,#]","_");
	    System.out.println("fileName0="+fileName0);
	    System.out.println("fileName1="+fileName1);
	    String fileName2 = "Donatella Ianelli - NP 2421.pdf";
	    int pos1 = fileName2.lastIndexOf(" - ");
		int pos = fileName2.lastIndexOf(".");
		System.out.println("pos="+pos);
		System.out.println("pos1="+pos1);
		
		System.out.println(fileName2.substring(pos1 + 3, pos));
		System.out.println(fileName1.equals(fileName2.substring(pos1 + 3, pos)));
	    
	    
	    String line;
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line.replaceAll("[^\\.A-Za-z0-9_&\\- ]", ""));
	    }
		}catch(Exception e){
			
		}
		//System.out.println( "J-[,\_WOU {57|}’".replaceAll("[^\\.A-Za-z0-9_]", ""));
		int invIndex = "Couch White, LLP (Mu - 2-836220875".lastIndexOf(" - ");
		String fileName = "Couch White, LLP (Mu - 2-836220875".substring(invIndex+3);
		System.out.println("fileName="+fileName);
//		System.out.println("11764/21".replace("/", ""));
		System.out.println("Enter username");
		Scanner in = new Scanner(System.in);
		String userName = in.nextLine();  // Read user input
	    System.out.println("Username is: " + userName);  // Output user input
	    System.out.println(java.math.BigDecimal.valueOf(java.lang.Double.valueOf((String)("2722500000")).longValue()));
	    String nfdNormalizedString = Normalizer.normalize(userName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""); 
	    System.out.println(nfdNormalizedString);
	    
	    ResourceBundle Config = ResourceBundle.getBundle("Config2");
		System.out.println("ICMPasswd = "+Config.getObject("ICMPasswd"));
		
	}

}
