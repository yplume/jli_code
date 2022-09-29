import java.util.ResourceBundle;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigDecimal.*;
import java.util.Formatter;

public class test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("\\$T\\^M\\$o\\#\\$\\@\\;fFLJcPAe\\-39842e");
		//String test = "J-[,\_";
		// create a string
		System.out.println("333="+java.math.BigDecimal.valueOf(3563432000.10));
		System.out.println(java.math.BigDecimal.valueOf(java.lang.Double.valueOf("35634310.00")).toPlainString());
		System.out.println("35634320.00 = " + java.math.BigDecimal.valueOf(java.lang.Double.valueOf((String)"19000000.01")).toPlainString());
		//System.out.println("35634320.00 = " + java.math.BigDecimal.valueOf(java.lang.Double.valueOf(Long.parseLong("35634320.00",2))));
		System.out.println("11.01 = " + java.math.BigDecimal.valueOf(java.lang.Double.valueOf((String)"11.01").longValue()));
		String message = "everyone_loves_java";

	    // stores each characters to a char array
	    char[] charArray = message.toCharArray();
	    boolean foundSpace = true;

	    for(int i = 0; i < charArray.length; i++) {

	      // if the array element is a letter
	      if(Character.isLetter(charArray[i])) {

	        // check space is present before the letter
	        if(foundSpace) {

	          // change the letter into uppercase
	          charArray[i] = Character.toUpperCase(charArray[i]);
	          foundSpace = false;
	        }
	      }

	      else {
	        // if the new character is not character
	        foundSpace = true;
	      }
	    }

	    // convert the char array to the string
	    message = String.valueOf(charArray);
	    System.out.println("Message: " + message);
	}

}
