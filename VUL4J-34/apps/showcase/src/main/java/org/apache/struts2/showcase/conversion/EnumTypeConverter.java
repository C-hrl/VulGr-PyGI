
package org.apache.struts2.showcase.conversion;

import org.apache.struts2.util.StrutsTypeConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class EnumTypeConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		List<Enum> result = new ArrayList<Enum>();
		for (int a = 0; a < values.length; a++) {
			Enum e = Enum.valueOf(OperationsEnum.class, values[a]);
			if (e != null)
				result.add(e);
		}
		return result;
	}

	@Override
	public String convertToString(Map context, Object o) {
		List l = (List) o;
		String result = "<";
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			result = result + "[" + i.next() + "]";
		}
		result = result + ">";
		return result;
	}


}
