

package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.util.ContentTypeMatcher;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.util.UrlHelper;
import org.apache.struts2.views.velocity.VelocityManager;

import java.util.StringTokenizer;


public class DefaultBeanSelectionProvider extends AbstractBeanSelectionProvider {

    private static final Logger LOG = LogManager.getLogger(DefaultBeanSelectionProvider.class);

    public void register(ContainerBuilder builder, LocatableProperties props) {
        alias(ObjectFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY, builder, props);
        alias(ActionFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_ACTIONFACTORY, builder, props);
        alias(ResultFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_RESULTFACTORY, builder, props);
        alias(ConverterFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_CONVERTERFACTORY, builder, props);
        alias(InterceptorFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_INTERCEPTORFACTORY, builder, props);
        alias(ValidatorFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_VALIDATORFACTORY, builder, props);
        alias(UnknownHandlerFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_UNKNOWNHANDLERFACTORY, builder, props);

        alias(FileManagerFactory.class, StrutsConstants.STRUTS_FILE_MANAGER_FACTORY, builder, props, Scope.SINGLETON);

        alias(XWorkConverter.class, StrutsConstants.STRUTS_XWORKCONVERTER, builder, props);
        alias(CollectionConverter.class, StrutsConstants.STRUTS_CONVERTER_COLLECTION, builder, props);
        alias(ArrayConverter.class, StrutsConstants.STRUTS_CONVERTER_ARRAY, builder, props);
        alias(DateConverter.class, StrutsConstants.STRUTS_CONVERTER_DATE, builder, props);
        alias(NumberConverter.class, StrutsConstants.STRUTS_CONVERTER_NUMBER, builder, props);
        alias(StringConverter.class, StrutsConstants.STRUTS_CONVERTER_STRING, builder, props);

        alias(ConversionPropertiesProcessor.class, StrutsConstants.STRUTS_CONVERTER_PROPERTIES_PROCESSOR, builder, props);
        alias(ConversionFileProcessor.class, StrutsConstants.STRUTS_CONVERTER_FILE_PROCESSOR, builder, props);
        alias(ConversionAnnotationProcessor.class, StrutsConstants.STRUTS_CONVERTER_ANNOTATION_PROCESSOR, builder, props);
        alias(TypeConverterCreator.class, StrutsConstants.STRUTS_CONVERTER_CREATOR, builder, props);
        alias(TypeConverterHolder.class, StrutsConstants.STRUTS_CONVERTER_HOLDER, builder, props);

        alias(TextProvider.class, StrutsConstants.STRUTS_XWORKTEXTPROVIDER, builder, props, Scope.PROTOTYPE);

        alias(LocaleProvider.class, StrutsConstants.STRUTS_LOCALE_PROVIDER, builder, props);
        alias(ActionProxyFactory.class, StrutsConstants.STRUTS_ACTIONPROXYFACTORY, builder, props);
        alias(ObjectTypeDeterminer.class, StrutsConstants.STRUTS_OBJECTTYPEDETERMINER, builder, props);
        alias(ActionMapper.class, StrutsConstants.STRUTS_MAPPER_CLASS, builder, props);
        alias(MultiPartRequest.class, StrutsConstants.STRUTS_MULTIPART_PARSER, builder, props, Scope.PROTOTYPE);
        alias(FreemarkerManager.class, StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME, builder, props);
        alias(VelocityManager.class, StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, builder, props);
        alias(UrlRenderer.class, StrutsConstants.STRUTS_URL_RENDERER, builder, props);
        alias(ActionValidatorManager.class, StrutsConstants.STRUTS_ACTIONVALIDATORMANAGER, builder, props);
        alias(ValueStackFactory.class, StrutsConstants.STRUTS_VALUESTACKFACTORY, builder, props);
        alias(ReflectionProvider.class, StrutsConstants.STRUTS_REFLECTIONPROVIDER, builder, props);
        alias(ReflectionContextFactory.class, StrutsConstants.STRUTS_REFLECTIONCONTEXTFACTORY, builder, props);
        alias(PatternMatcher.class, StrutsConstants.STRUTS_PATTERNMATCHER, builder, props);
        alias(ContentTypeMatcher.class, StrutsConstants.STRUTS_CONTENT_TYPE_MATCHER, builder, props);
        alias(StaticContentLoader.class, StrutsConstants.STRUTS_STATIC_CONTENT_LOADER, builder, props);
        alias(UnknownHandlerManager.class, StrutsConstants.STRUTS_UNKNOWN_HANDLER_MANAGER, builder, props);
        alias(UrlHelper.class, StrutsConstants.STRUTS_URL_HELPER, builder, props);

        alias(TextParser.class, StrutsConstants.STRUTS_EXPRESSION_PARSER, builder, props);

        alias(DispatcherErrorHandler.class, StrutsConstants.STRUTS_DISPATCHER_ERROR_HANDLER, builder, props);

        
        alias(ExcludedPatternsChecker.class, StrutsConstants.STRUTS_EXCLUDED_PATTERNS_CHECKER, builder, props, Scope.PROTOTYPE);
        alias(AcceptedPatternsChecker.class, StrutsConstants.STRUTS_ACCEPTED_PATTERNS_CHECKER, builder, props, Scope.PROTOTYPE);

        switchDevMode(props);

        
        convertIfExist(props, StrutsConstants.STRUTS_LOG_MISSING_PROPERTIES, XWorkConstants.LOG_MISSING_PROPERTIES);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EXPRESSION_CACHE, XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EVAL_EXPRESSION, XWorkConstants.ENABLE_OGNL_EVAL_EXPRESSION);
        convertIfExist(props, StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS);
        convertIfExist(props, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, XWorkConstants.RELOAD_XML_CONFIGURATION);

        convertIfExist(props, StrutsConstants.STRUTS_EXCLUDED_CLASSES, XWorkConstants.OGNL_EXCLUDED_CLASSES);
        convertIfExist(props, StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, XWorkConstants.OGNL_EXCLUDED_PACKAGE_NAME_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, XWorkConstants.OGNL_EXCLUDED_PACKAGE_NAMES);

        convertIfExist(props, StrutsConstants.STRUTS_ADDITIONAL_EXCLUDED_PATTERNS, XWorkConstants.ADDITIONAL_EXCLUDED_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_ADDITIONAL_ACCEPTED_PATTERNS, XWorkConstants.ADDITIONAL_ACCEPTED_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_OVERRIDE_EXCLUDED_PATTERNS, XWorkConstants.OVERRIDE_EXCLUDED_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_OVERRIDE_ACCEPTED_PATTERNS, XWorkConstants.OVERRIDE_ACCEPTED_PATTERNS);

        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        loadCustomResourceBundles(props);
    }

    
    private void switchDevMode(LocatableProperties props) {
        if ("true".equalsIgnoreCase(props.getProperty(StrutsConstants.STRUTS_DEVMODE))) {
            if (props.getProperty(StrutsConstants.STRUTS_I18N_RELOAD) == null) {
                props.setProperty(StrutsConstants.STRUTS_I18N_RELOAD, "true");
            }
            if (props.getProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD) == null) {
                props.setProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
            }
            if (props.getProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY) == null) {
                props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY, "0");
            }
            
            props.setProperty(XWorkConstants.DEV_MODE, "true");
        } else {
            props.setProperty(XWorkConstants.DEV_MODE, "false");
        }
    }

    private void loadCustomResourceBundles(LocatableProperties props) {
        String bundles = props.getProperty(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES);
        if (bundles != null && bundles.length() > 0) {
            StringTokenizer customBundles = new StringTokenizer(bundles, ", ");

            while (customBundles.hasMoreTokens()) {
                String name = customBundles.nextToken();
                try {
              	    LOG.info("Loading global messages from [{}]", name);
                    LocalizedTextUtil.addDefaultResourceBundle(name);
                } catch (Exception e) {
                    LOG.error("Could not find messages file {}.properties. Skipping", name);
                }
            }
        }
    }

}
