# Use of externally-controlled input to select classes or code ('unsafe reflection')
Allowing users to freely choose the name of a class to instantiate could provide means to attack a vulnerable application.


## Recommendation
Create a list of classes that are allowed to load reflectively and strictly verify the input to ensure that users can only instantiate classes or execute methods that ought to be allowed.


## Example
The `bad` method shown below illustrate class loading with `Class.forName` without any check on the particular class being instantiated. The `good` methods illustrate some different ways to restrict which classes can be instantiated.


```java
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UnsafeReflection {

    @RequestMapping(value = {"/service/{beanIdOrClassName}/{methodName}"}, method = {RequestMethod.POST}, consumes = {"application/json"}, produces = {"application/json"})
    public Object bad1(@PathVariable("beanIdOrClassName") String beanIdOrClassName, @PathVariable("methodName") String methodName, @RequestBody Map<String, Object> body) throws Exception {
        List<Object> rawData = null;
        try {
            rawData = (List<Object>)body.get("methodInput");
        } catch (Exception e) {
            return e;
        }
        return invokeService(beanIdOrClassName, methodName, null, rawData);
    }

    @GetMapping(value = "uf1")
    public void good1(HttpServletRequest request) throws Exception {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("com.example.test1");
        hashSet.add("com.example.test2");
        String className = request.getParameter("className");
        String parameterValue = request.getParameter("parameterValue");
        if (!hashSet.contains(className)){ 
            throw new Exception("Class not valid: "  + className);
        }
        try {
            Class clazz = Class.forName(className);
            Object object = clazz.getDeclaredConstructors()[0].newInstance(parameterValue); //good
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "uf2")
    public void good2(HttpServletRequest request) throws Exception {
        String className = request.getParameter("className");
        String parameterValue = request.getParameter("parameterValue");
        if (!"com.example.test1".equals(className)){
            throw new Exception("Class not valid: "  + className);
        }
        try {
            Class clazz = Class.forName(className);
            Object object = clazz.getDeclaredConstructors()[0].newInstance(parameterValue); //good
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object invokeService(String beanIdOrClassName, String methodName, MultipartFile[] files, List<Object> data) throws Exception {
        BeanFactory beanFactory = new BeanFactory();
		try {
			Object bean = null;
			Class<?> beanClass = Class.forName(beanIdOrClassName);
			bean = beanFactory.getBean(beanClass);
			byte b;
			int i;
			Method[] arrayOfMethod;
			for (i = (arrayOfMethod = bean.getClass().getMethods()).length, b = 0; b < i; ) {
				Method method = arrayOfMethod[b];
				if (!method.getName().equals(methodName)) {
					b++;
					continue;
				}
				Object result = method.invoke(bean, data);
				Map<String, Object> map = new HashMap<>();
				return map;
			}
		} catch (Exception e) {
			return e;
		}
		return null;
    }
}

class BeanFactory {

	private static HashMap<String, Object> classNameMap = new HashMap<>();

	private static HashMap<Class<?>, Object> classMap = new HashMap<>();

	static {
		classNameMap.put("xxxx", Runtime.getRuntime());
		classMap.put(Runtime.class, Runtime.getRuntime());
	}

	public Object getBean(Class<?> clzz) {
		return classMap.get(clzz);
	}
}

```

## References
* Unsafe use of Reflection | OWASP: [Unsafe use of Reflection](https://owasp.org/www-community/vulnerabilities/Unsafe_use_of_Reflection).
* Java owasp: Classes should not be loaded dynamically: [Classes should not be loaded dynamically](https://rules.sonarsource.com/java/tag/owasp/RSPEC-2658).
* Common Weakness Enumeration: [CWE-470](https://cwe.mitre.org/data/definitions/470.html).
