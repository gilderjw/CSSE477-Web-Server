package server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GMTConversion {
	public static final String GMT_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
	
	static final Logger log = LogManager.getLogger(GMTConversion.class);
	
	public static String toGMTString(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		DateFormat gmtFormat = new SimpleDateFormat(GMT_FORMAT);
		gmtFormat.setCalendar(calendar);
		
		return gmtFormat.format(date);
	}
	
	public static Date fromGMTString(String dateString) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		DateFormat gmtFormat = new SimpleDateFormat(GMT_FORMAT);
		gmtFormat.setCalendar(calendar);
	
		try {
			return gmtFormat.parse(dateString);
		} catch (ParseException e) {
			log.error("Couldn't parse date", e);
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		Date date = new Date();
		log.info("From Any Timezone Date:\t" + date);
		String newTime = toGMTString(date);
		log.info("To GMT Timezone Date:\t" + newTime);
	}
}
