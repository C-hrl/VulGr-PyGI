

package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


@StrutsTag(name="date", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.DateTag", description="Render a formatted date.")
public class Date extends ContextBean {

    private static final Logger LOG = LogManager.getLogger(Date.class);
    
    public static final String DATETAG_PROPERTY = "struts.date.format";
    
    public static final String DATETAG_PROPERTY_PAST = "struts.date.format.past";
    private static final String DATETAG_DEFAULT_PAST = "{0} ago";
    
    public static final String DATETAG_PROPERTY_FUTURE = "struts.date.format.future";
    private static final String DATETAG_DEFAULT_FUTURE = "in {0}";
    
    public static final String DATETAG_PROPERTY_SECONDS = "struts.date.format.seconds";
    private static final String DATETAG_DEFAULT_SECONDS = "an instant";
    
    public static final String DATETAG_PROPERTY_MINUTES = "struts.date.format.minutes";
    private static final String DATETAG_DEFAULT_MINUTES = "{0,choice,1#one minute|1<{0} minutes}";
    
    public static final String DATETAG_PROPERTY_HOURS = "struts.date.format.hours";
    private static final String DATETAG_DEFAULT_HOURS = "{0,choice,1#one hour|1<{0} hours}{1,choice,0#|1#, one minute|1<, {1} minutes}";
    
    public static final String DATETAG_PROPERTY_DAYS = "struts.date.format.days";
    private static final String DATETAG_DEFAULT_DAYS = "{0,choice,1#one day|1<{0} days}{1,choice,0#|1#, one hour|1<, {1} hours}";
    
    public static final String DATETAG_PROPERTY_YEARS = "struts.date.format.years";
    private static final String DATETAG_DEFAULT_YEARS = "{0,choice,1#one year|1<{0} years}{1,choice,0#|1#, one day|1<, {1} days}";

    private String name;

    private String format;

    private boolean nice;

    private String timezone;

    public Date(ValueStack stack) {
        super(stack);
    }

    private TextProvider findProviderInStack() {
        for (Object o : getStack().getRoot()) {
            if (o instanceof TextProvider) {
                return (TextProvider) o;
            }
        }
        return null;
    }

    
    public String formatTime(TextProvider tp, java.util.Date date) {
        java.util.Date now = new java.util.Date();
        StringBuilder sb = new StringBuilder();
        List args = new ArrayList();
        long secs = Math.abs((now.getTime() - date.getTime()) / 1000);
        long mins = secs / 60;
        long sec = secs % 60;
        int min = (int) mins % 60;
        long hours = mins / 60;
        int hour = (int) hours % 24;
        int days = (int) hours / 24;
        int day = days % 365;
        int years = days / 365;

        if (years > 0) {
            args.add(years);
            args.add(day);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_YEARS, DATETAG_DEFAULT_YEARS, args));
        } else if (day > 0) {
            args.add(day);
            args.add(hour);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_DAYS, DATETAG_DEFAULT_DAYS, args));
        } else if (hour > 0) {
            args.add(hour);
            args.add(min);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_HOURS, DATETAG_DEFAULT_HOURS, args));
        } else if (min > 0) {
            args.add(min);
            args.add(sec);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_MINUTES, DATETAG_DEFAULT_MINUTES, args));
        } else {
            args.add(sec);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_SECONDS, DATETAG_DEFAULT_SECONDS, args));
        }

        args.clear();
        args.add(sb.toString());
        if (date.before(now)) {
            
            return tp.getText(DATETAG_PROPERTY_PAST, DATETAG_DEFAULT_PAST, args);
        } else {
            return tp.getText(DATETAG_PROPERTY_FUTURE, DATETAG_DEFAULT_FUTURE, args);
        }
    }

    public boolean end(Writer writer, String body) {
        String msg;
        java.util.Date date = null;
        
        try {
            
            Object dateObject = findValue(name);
            if (dateObject instanceof java.util.Date) {
                date = (java.util.Date) dateObject;
            } else if(dateObject instanceof Calendar){
                date = ((Calendar) dateObject).getTime();
            } else {
                if (devMode) {
                    LOG.error("Expression [{}] passed to <s:date/> tag which was evaluated to [{}]({}) isn't instance of java.util.Date nor java.util.Calendar!",
                            name, dateObject, (dateObject != null ? dateObject.getClass() : "null"));
                } else {
                    LOG.debug("Expression [{}] passed to <s:date/> tag which was evaluated to [{}]({}) isn't instance of java.util.Date nor java.util.Calendar!",
                            name, dateObject, (dateObject != null ? dateObject.getClass() : "null"));
                }
            }
        } catch (Exception e) {
            LOG.error("Could not convert object with key '{}' to a java.util.Date instance", name);
        }

        
        if (format != null) {
            format = findString(format);
        }
        if (date != null) {
            TextProvider tp = findProviderInStack();
            if (tp != null) {
                if (nice) {
                    msg = formatTime(tp, date);
                } else {
                    TimeZone tz = getTimeZone();
                    if (format == null) {
                        String globalFormat = null;

                        
                        
                        globalFormat = tp.getText(DATETAG_PROPERTY);

                        
                        
                        
                        if (globalFormat != null
                                && !DATETAG_PROPERTY.equals(globalFormat)) {
                            SimpleDateFormat sdf = new SimpleDateFormat(globalFormat,
                                    ActionContext.getContext().getLocale());
                            sdf.setTimeZone(tz);
                            msg = sdf.format(date);
                        } else {
                            DateFormat df = DateFormat.getDateTimeInstance(
                                    DateFormat.MEDIUM, DateFormat.MEDIUM,
                                    ActionContext.getContext().getLocale());
                            df.setTimeZone(tz);
                            msg = df.format(date);
                        }
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat(format, ActionContext
                                .getContext().getLocale());
                        sdf.setTimeZone(tz);
                        msg = sdf.format(date);
                    }
                }
                if (msg != null) {
                    try {
                        if (getVar() == null) {
                            writer.write(msg);
                        } else {
                            putInContext(msg);
                        }
                    } catch (IOException e) {
                        LOG.error("Could not write out Date tag", e);
                    }
                }
            }
        }
        return super.end(writer, "");
    }

    private TimeZone getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        if (timezone != null) {
            timezone = stripExpressionIfAltSyntax(timezone);
            String actualTimezone = (String) getStack().findValue(timezone, String.class);
            if (actualTimezone != null) {
                timezone = actualTimezone;
            }
            tz = TimeZone.getTimeZone(timezone);
        }
        return tz;
    }

    @StrutsTagAttribute(description="Date or DateTime format pattern", rtexprvalue=false)
    public void setFormat(String format) {
        this.format = format;
    }

    @StrutsTagAttribute(description="Whether to print out the date nicely", type="Boolean", defaultValue="false")
    public void setNice(boolean nice) {
        this.nice = nice;
    }

    @StrutsTagAttribute(description = "The specific timezone in which to format the date", required = false)
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    
    public String getName() {
        return name;
    }

    @StrutsTagAttribute(description="The date value to format", required=true)
    public void setName(String name) {
        this.name = name;
    }

    
    public String getFormat() {
        return format;
    }

    
    public boolean isNice() {
        return nice;
    }

    
    public String getTimezone() {
        return timezone;
    }

}
