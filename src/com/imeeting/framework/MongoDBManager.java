package com.imeeting.framework;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoDBManager {
	private Mongo mongo;

	private static MongoDBManager instance;

	private MongoDBManager() {
		Configuration config = ContextLoader.getConfiguration();
		try {
			mongo = new Mongo(config.getMongoServerAddress(),
					config.getMongoServerPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static MongoDBManager getInstance() {
		if (instance == null) {
			instance = new MongoDBManager();
		}
		return instance;
	}

	public Mongo getMongo() {
		return mongo;
	}

	public DB getImeetingDB() {
		if (mongo != null) {
			DB db = mongo.getDB(ContextLoader.getConfiguration()
					.getMongoDBName());
			return db;
		} else {
			return null;
		}
	}
}
