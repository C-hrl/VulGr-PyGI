
package org.apache.struts2.showcase.dao;

import org.apache.struts2.showcase.application.Storage;
import org.apache.struts2.showcase.exception.CreateException;
import org.apache.struts2.showcase.exception.StorageException;
import org.apache.struts2.showcase.exception.UpdateException;
import org.apache.struts2.showcase.model.IdEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;



public abstract class AbstractDao implements Serializable, Dao {

	@Autowired
	private Storage storage;

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public IdEntity get(Serializable id) {
		return getStorage().get(getFeaturedClass(), id);
	}

	public Serializable create(IdEntity object) throws CreateException {
		return getStorage().create(object);
	}

	public IdEntity update(IdEntity object) throws UpdateException {
		return getStorage().update(object);
	}

	public Serializable merge(IdEntity object) throws StorageException {
		return getStorage().merge(object);
	}

	public int delete(Serializable id) throws CreateException {
		return getStorage().delete(getFeaturedClass(), id);
	}

	public int delete(IdEntity object) throws CreateException {
		return getStorage().delete(object);
	}

	public Collection findAll() {
		return getStorage().findAll(getFeaturedClass());
	}

}
