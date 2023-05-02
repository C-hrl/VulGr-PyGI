
package org.apache.struts2.showcase.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomFreemarkerManagerUtil {

	public String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(new Date());
	}

	public String getTimeNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(new Date());
	}
}
