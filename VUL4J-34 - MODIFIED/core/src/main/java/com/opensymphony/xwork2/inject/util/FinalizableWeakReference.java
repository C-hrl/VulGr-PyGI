

package com.opensymphony.xwork2.inject.util;

import java.lang.ref.WeakReference;


public abstract class FinalizableWeakReference<T> extends WeakReference<T>
    implements FinalizableReference {

  protected FinalizableWeakReference(T referent) {
    super(referent, FinalizableReferenceQueue.getInstance());
  }
}
