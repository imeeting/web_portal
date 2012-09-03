package com.imeeting.mvc.model.addressbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imeeting.constants.AddressBookConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.richitec.util.ValidatePattern;

public class AddressBookDAO {
	private static Log log = LogFactory.getLog(AddressBookDAO.class);

	private static final String COLL_CONTACTS = "contacts";
	private static final String COLL_GROUPS = "groups";

	private DB db;

	public AddressBookDAO() {

	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void saveAddressBook(String owner, List<DBObject> contacts,
			List<DBObject> groups) {
		if (db != null) {
			log.info("save AddressBook");
			log.debug("contacts: " + contacts);
			log.debug("groups: " + groups);

			DBCollection contactsCollection = db.getCollection(COLL_CONTACTS);
			DBObject query = new BasicDBObject();
			query.put(AddressBookConstants.owner.name(), owner);
			contactsCollection.remove(query);
			contactsCollection.insert(contacts);

			if (groups != null) {
				DBCollection groupsCollection = db.getCollection(COLL_GROUPS);
				groupsCollection.remove(query);
				groupsCollection.insert(groups);
			}
		}
	}

	/**
	 * get the count number of contacts
	 * 
	 * @param owner
	 * @param groupId
	 *            -
	 * @return the number of contacts according to groupId, if groupId is null,
	 *         it will return all
	 */
	public long getContactsCount(String owner, String groupId) {
		long count = 0;
		if (db != null) {
			DBCollection contactsColl = db.getCollection(COLL_CONTACTS);
			DBObject query = new BasicDBObject();
			query.put(AddressBookConstants.owner.name(), owner);
			if (groupId != null) {
				query.put(AddressBookConstants.group_id.name(), groupId);
			}
			count = contactsColl.count(query);
		}

		return count;
	}

	/**
	 * get the contacts by group id
	 * @param owner
	 * @param groupId
	 * @param offset
	 * @param pageSize
	 * @return return the contact list, if groupId is null, it will return all
	 */
	public List<DBObject> getContacts(String owner, String groupId, int offset, int pageSize) {
		List<DBObject> contactList = new ArrayList<DBObject>();

		if (db != null) {
			DBCollection contactsColl = db.getCollection(COLL_CONTACTS);
			DBObject query = new BasicDBObject();
			query.put(AddressBookConstants.owner.name(), owner);
			if (groupId != null) {
				query.put(AddressBookConstants.group_id.name(), groupId);
			}
			
			int start = (offset - 1) * pageSize;
			contactList = contactsColl.find(query).skip(start).limit(pageSize).toArray();
		}

		return contactList;
	}
	
	/**
	 * search contact by name
	 * @param owner
	 * @param groupId
	 * @param searchWord
	 * @return return the search result, if groupId is null, it will search in all contacts
	 */
	public List<DBObject> searchContactsByName(String owner, String groupId, String searchWord) {
		log.info("searchContactsByName");
		List<DBObject> contactList = new ArrayList<DBObject>();
		if (db != null) {
			DBCollection contactsColl = db.getCollection(COLL_CONTACTS);
			DBObject query = new BasicDBObject();
			query.put(AddressBookConstants.owner.name(), owner);
			BasicDBObject reg = new BasicDBObject();
			reg.put("$regex", "^" + searchWord + ".*");
			reg.put("$options", "i");
			query.put(AddressBookConstants.contact_name.name(), reg);
			if (groupId != null) {
				query.put(AddressBookConstants.group_id.name(), groupId);
			}
			contactList = contactsColl.find(query).toArray();
		}
		return contactList;
	}
	
	/**
	 * search contact by phone number
	 */
	public List<DBObject> searchContactsByPhoneNumber(String owner, String groupId, String searchWord) {
		log.info("searchContactsByPhoneNumber");
		List<DBObject> contactList = new ArrayList<DBObject>();
		if (db != null) {
			DBCollection contactsColl = db.getCollection(COLL_CONTACTS);
			DBObject query = new BasicDBObject();
			query.put(AddressBookConstants.owner.name(), owner);
			BasicDBObject reg = new BasicDBObject();
			reg.put("$regex", "^" + searchWord + ".*");
			reg.put("$options", "i");
			query.put(AddressBookConstants.phone_array.name(),reg);
			if (groupId != null) {
				query.put(AddressBookConstants.group_id.name(), groupId);
			}
			contactList = contactsColl.find(query).toArray();
		}
		return contactList;
	} 
	
	/**
	 * search contact, it will search name or phone number according to search word
	 * @param owner
	 * @param groupId
	 * @param searchWord
	 * @return
	 */
	public List<DBObject> searchContact(String owner, String groupId, String searchWord) {
		List<DBObject> list = null;
		try {
			Integer.parseInt(searchWord);
			list = searchContactsByPhoneNumber(owner, groupId, searchWord);
		} catch (Exception e) {
			list = searchContactsByName(owner, groupId, searchWord);
		}
		return list;
	}
}
