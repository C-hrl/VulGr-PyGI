

package org.apache.struts2.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class DateFormatter {

    Date date;
    DateFormat format;

    
    DateFormat parser;


    
    public DateFormatter() {
        this.parser = new SimpleDateFormat();
        this.format = new SimpleDateFormat();
        this.date = new Date();
    }


    public void setDate(String date) {
        try {
            this.date = parser.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void setDate(Date date) {
        this.date = (date == null) ? null : (Date)date.clone();
    }

    public void setDate(int date) {
        setDate(Integer.toString(date));
    }

    public Date getDate() {
        return this.date;
    }

    public void setFormat(String format) {
        this.format = new SimpleDateFormat(format);
    }

    public void setFormat(DateFormat format) {
        this.format = format;
    }

    public String getFormattedDate() {
        return format.format(date);
    }

    public void setParseFormat(String format) {
        this.parser = new SimpleDateFormat(format);
    }

    public void setParser(DateFormat parser) {
        this.parser = parser;
    }

    public void setTime(long time) {
        date.setTime(time);
    }
}
