package bot;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.json.*;
import javax.json.stream.*;

public class QuestionCapitaleBot {

	private JsonArray questions;
	private String response;
	private Calendar end, start;
	
	final String JSON_FILE = "src\\assets\\json\\capitales_pays.json";

	public QuestionCapitaleBot() {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			InputStream fis = new FileInputStream(current+"\\"+JSON_FILE);

			JsonReader jsonReader = Json.createReader(fis);
			questions = jsonReader.readArray();
			
			jsonReader.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getRandomPays() {
		start = new GregorianCalendar();
		end = new GregorianCalendar();
		end.add(Calendar.MINUTE, 2);
		
		Double ran = Math.random()*questions.size();
		response = questions.get(ran.intValue()).asJsonObject().get("capitale").toString();
		return questions.get(ran.intValue()).asJsonObject().get("pays").toString();
	}
	
	public String getResponse () {
		response = response.replaceAll("[^\\w]","");
System.out.println(response);
		return response;
	}
	
	public boolean isTimeRemaining() {
		Calendar now = new GregorianCalendar();
		if (now.after(end)) {
			return false;
		}
		return true;
	}
	
	public long getTimeRemaining() {
		end = new GregorianCalendar();

		if (isTimeRemaining()) {
			return (end.getTime().getTime()-start.getTime().getTime()) / 1000; 
		}
		return -1;
	}
}
