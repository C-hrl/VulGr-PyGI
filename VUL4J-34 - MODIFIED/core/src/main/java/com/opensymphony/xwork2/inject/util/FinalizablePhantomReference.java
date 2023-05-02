

package com.opensymphony.xwork2.inject.util;

import java.lang.ref.PhantomReference;


public abstract class FinalizablePhantomReference<T>
    extends PhantomReference<T> implements FinalizableReference {

  protected FinalizablePhantomReference(T referent) {
    super(referent, FinalizableReferenceQueue.getInstance());
  }
}
