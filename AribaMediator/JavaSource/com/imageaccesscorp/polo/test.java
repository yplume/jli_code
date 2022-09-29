/*
 * Created on Jul 23, 2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.imageaccesscorp.polo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class test {

	public static void main(String[] args) {
		
		Vector v = new Vector();
		Hashtable ht = new Hashtable();
		ht.put("key1","value1");
		ht.put("key2","value2");
		Hashtable ht1 = new Hashtable();
		ht1.put("key3","value3");
		ht1.put("key4","value4");
		v.add(ht);
		v.add(ht1);
		Iterator it = v.iterator();
		System.out.println("v="+v);
		
		while(it.hasNext())
		{
			System.out.println("111");
			Hashtable childtable = (Hashtable) it.next();
			System.out.println("childtable="+childtable);
			
		
		}
		System.out.println("\\$T\\^M\\$o\\#\\$\\@\\;fFLJcPAe\\-39842e");
		
		System.out.println("OVERSEAS / OUT OF TERRITORY".replaceAll("[^\\.A-Za-z0-9_&]", "_"));
		System.out.println("0.00 is " + checkSignWithRelational("0.00"));
		System.out.println("2 is "+ checkSignWithRelational("2"));
		System.out.println("-1112.30 is "+ checkSignWithRelational("-112.30"));
		String body = "<ImportSession>\n";
		body += "\t<Batches>\n";
		body += "\t\t<Batch BatchClassName=\"batchxml\" Priority=\"1\" Processed=\"0\">\n";
		body += "\t\t\t<Documents>\n";
		body += "\t\t\t\t<Document FormTypeName=\"frmxml\">\n";
		body += "\t\t\t\t\t<IndexFields>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Assignment Number\" Value=\"12345\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Vendor Name\" Value=\"Joe\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Vendor Number\" Value=\"111\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Invoice Number\" Value=\"222\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Invoice Amount\" Value=\"100\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Invoice Date\" Value=\"2-25-2015\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"PO Number\" Value=\"PO-15\"/>\n";
		body += "\t\t\t\t\t\t<IndexField Name=\"Reject Code\" Value=\"MISSINV\"/>\n";
		body += "\t\t\t\t\t\t<IndexField>\n";
		body += "\t\t\t\t\t</IndexFields>\n";
		body += "\t\t\t\t</Document>\n";
		body += "\t\t\t</Documents>\n";
		body += "\t\t</Batch>\n";
		body += "\t</Batches>\n";
		body += "</ImportSession>";
		
		try {
			String filepath = "c:\\test\\test.xml";
			//String filepath = "c:\\test\\";
			File file = new File(filepath);
			String sSlashPath = filepath.replaceAll("\\\\\\\\", "\\");
			System.out.println("sSlashPath="+sSlashPath);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(body);
			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
 }
	public static String checkSignWithRelational(String amt){
        if( Double.parseDouble(amt) <0){
            return"negative";
        }else{
            return"positive";
        }
	}
}
