import java.util.Calendar;
import java.text.*;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * Created on Nov 15, 2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DateTest {

	public static void main(String[] args) {

		int x = -3;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add( Calendar.DAY_OF_YEAR, x);
		Date xDaysAgo = cal.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
		String dateF = df.format(xDaysAgo);
		String dateAgo = df.format( xDaysAgo);
		String dateNow = df.format( new Date());
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");  
		System.out.println(dateAgo);
		System.out.println(dateNow);
		
	}
}
