
package org.apache.struts2.showcase.dao;

import org.apache.struts2.showcase.exception.CreateException;
import org.apache.struts2.showcase.exception.StorageException;
import org.apache.struts2.showcase.exception.UpdateException;
import org.apache.struts2.showcase.model.IdEntity;

import java.io.Serializable;
import java.util.Collection;



public interface Dao {

	Class getFeaturedClass();

	IdEntity get(Serializable id);

	Serializable create(IdEntity object) throws CreateException;

	IdEntity update(IdEntity object) throws UpdateException;

	Serializable merge(IdEntity object) throws StorageException;

	int delete(Serializable id) throws CreateException;

	int delete(IdEntity object) throws CreateException;

	Collection findAll();
}
