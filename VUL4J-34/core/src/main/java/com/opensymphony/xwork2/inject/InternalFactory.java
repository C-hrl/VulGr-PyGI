

package com.opensymphony.xwork2.inject;

import java.io.Serializable;


interface InternalFactory<T> extends Serializable {

  
  T create(InternalContext context);
}
