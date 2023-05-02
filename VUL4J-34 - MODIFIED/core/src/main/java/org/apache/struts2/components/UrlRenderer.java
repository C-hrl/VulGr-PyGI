

package org.apache.struts2.components;

import org.apache.struts2.dispatcher.mapper.ActionMapper;

import java.io.Writer;


public interface UrlRenderer {
	
	
	void beforeRenderUrl(UrlProvider provider);
	
	
	void renderUrl(Writer writer, UrlProvider provider);
	
	
	void renderFormUrl(Form formComponent);

      void setActionMapper(ActionMapper actionMapper);

}
