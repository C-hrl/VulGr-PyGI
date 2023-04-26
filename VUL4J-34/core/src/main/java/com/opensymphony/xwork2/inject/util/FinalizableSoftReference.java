

package com.opensymphony.xwork2.inject.util;

import java.lang.ref.SoftReference;


public abstract class FinalizableSoftReference<T> extends SoftReference<T>
    implements FinalizableReference {

  protected FinalizableSoftReference(T referent) {
    super(referent, FinalizableReferenceQueue.getInstance());
  }
}
