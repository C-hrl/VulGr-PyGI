
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class DefaultValidatorFactory implements ValidatorFactory {

    protected Map<String, String> validators = new HashMap<>();
    private static Logger LOG = LogManager.getLogger(DefaultValidatorFactory.class);
    protected ObjectFactory objectFactory;
    protected ValidatorFileParser validatorFileParser;

    @Inject
    public DefaultValidatorFactory(@Inject ObjectFactory objectFactory, @Inject ValidatorFileParser parser) {
        this.objectFactory = objectFactory;
        this.validatorFileParser = parser;
        parseValidators();
    }

    public Validator getValidator(ValidatorConfig cfg) {

        String className = lookupRegisteredValidatorType(cfg.getType());

        Validator validator;

        try {
            
            
            validator = objectFactory.buildValidator(className, cfg.getParams(), ActionContext.getContext().getContextMap());
        } catch (Exception e) {
            final String msg = "There was a problem creating a Validator of type " + className + " : caused by " + e.getMessage();
            throw new XWorkException(msg, e, cfg);
        }

        
        validator.setMessageKey(cfg.getMessageKey());
        validator.setDefaultMessage(cfg.getDefaultMessage());
        validator.setMessageParameters(cfg.getMessageParams());
        if (validator instanceof ShortCircuitableValidator) {
            ((ShortCircuitableValidator) validator).setShortCircuit(cfg.isShortCircuit());
        }

        return validator;
    }

    public void registerValidator(String name, String className) {
        LOG.debug("Registering validator of class {} with name {}", className, name);
        validators.put(name, className);
    }

    public String lookupRegisteredValidatorType(String name) {
        
        String className = validators.get(name);

        if (className == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + name);
        }

        return className;
    }

    private void parseValidators() {
        LOG.debug("Loading validator definitions.");

        List<File> files = new ArrayList<>();
        try {
            
            Iterator<URL> urls = ClassLoaderUtil.getResources("", DefaultValidatorFactory.class, false);
            while (urls.hasNext()) {
                URL u = urls.next();
                try {
                    URI uri = new URI(u.toExternalForm().replaceAll(" ", "%20"));
                    if (!uri.isOpaque() && "file".equalsIgnoreCase(uri.getScheme())) {
                        File f = new File(uri);
                        FilenameFilter filter = new FilenameFilter() {
                            public boolean accept(File file, String fileName) {
                                return fileName.contains("-validators.xml");
                            }
                        };
                        
                        
                        
                        
                        if (f.isDirectory()) {
                            try {
                                File[] ff = f.listFiles(filter);
                                if ( ff != null && ff.length > 0) {
                                    files.addAll(Arrays.asList(ff));
                                }
                            } catch (SecurityException se) {
                                LOG.error("Security Exception while accessing directory '{}'", f, se);
                            }

                        } else {
                            
                            
                            
                            
                            
                            
                            ZipInputStream zipInputStream = null;
                            try (InputStream inputStream = u.openStream()) {
                                if (inputStream instanceof ZipInputStream) {
                                    zipInputStream = (ZipInputStream) inputStream;
                                } else {
                                    zipInputStream = new ZipInputStream(inputStream);
                                }
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    if (zipEntry.getName().endsWith("-validators.xml")) {
                                        LOG.trace("Adding validator {}", zipEntry.getName());
                                        files.add(new File(zipEntry.getName()));
                                    }
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                            } finally {
                                
                                if (zipInputStream != null) {
                                    zipInputStream.close();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to load {}", u, ex);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException("Unable to parse validators", e);
        }

        
        String resourceName = "com/opensymphony/xwork2/validator/validators/default.xml";
        retrieveValidatorConfiguration(resourceName);

        
        resourceName = "validators.xml";
        retrieveValidatorConfiguration(resourceName);

        
        for (File file : files) {
            retrieveValidatorConfiguration(file.getName());
        }
    }

    private void retrieveValidatorConfiguration(String resourceName) {
        InputStream is = ClassLoaderUtil.getResourceAsStream(resourceName, DefaultValidatorFactory.class);
        if (is != null) {
            validatorFileParser.parseValidatorDefinitions(validators, is, resourceName);
        }
    }
}
