
package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.showcase.dao.Dao;
import org.apache.struts2.showcase.model.IdEntity;

import java.io.Serializable;
import java.util.Collection;



public abstract class AbstractCRUDAction extends ActionSupport {

	private static final Logger log = LogManager.getLogger(AbstractCRUDAction.class);

	private Collection availableItems;
	private String[] toDelete;

	protected abstract Dao getDao();


	public Collection getAvailableItems() {
		return availableItems;
	}

	public String[] getToDelete() {
		return toDelete;
	}

	public void setToDelete(String[] toDelete) {
		this.toDelete = toDelete;
	}

	public String list() throws Exception {
		this.availableItems = getDao().findAll();
		if (log.isDebugEnabled()) {
			log.debug("AbstractCRUDAction - [list]: " + (availableItems != null ? "" + availableItems.size() : "no") + " items found");
		}
		return execute();
	}

	public String delete() throws Exception {
		if (toDelete != null) {
			int count = 0;
			for (int i = 0, j = toDelete.length; i < j; i++) {
				count = count + getDao().delete(toDelete[i]);
			}
			if (log.isDebugEnabled()) {
				log.debug("AbstractCRUDAction - [delete]: " + count + " items deleted.");
			}
		}
		return SUCCESS;
	}

	
	protected IdEntity fetch(Serializable tryId, IdEntity tryObject) {
		IdEntity result = null;
		if (tryId != null) {
			result = getDao().get(tryId);
		} else if (tryObject != null) {
			result = getDao().get(tryObject.getId());
		}
		return result;
	}
}
