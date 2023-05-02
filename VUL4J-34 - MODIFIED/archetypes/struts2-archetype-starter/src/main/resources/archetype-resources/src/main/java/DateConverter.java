
package ${package};

import org.apache.struts2.util.StrutsTypeConverter;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Date;
import com.opensymphony.xwork2.conversion.TypeConversionException;


public class DateConverter extends StrutsTypeConverter {
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values != null && values.length > 0 && values[0] != null && values[0].length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            try {
                return sdf.parse(values[0]);
            }
            catch(ParseException e) {
                throw new TypeConversionException(e);
            }
        }
        return null;
    }
    public String convertToString(Map context, Object o) {
        if (o instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format((Date)o);
        }
        return "";
    }
}

