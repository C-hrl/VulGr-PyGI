
package com.opensymphony.xwork2.config.entities;

import java.util.List;


public interface InterceptorListHolder {

    InterceptorListHolder addInterceptor(InterceptorMapping interceptor);

    InterceptorListHolder addInterceptors(List<InterceptorMapping> interceptors);
}
