package microgram.impl.mongo;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.*;
import static microgram.impl.mongo.MongoPosts.Posts;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;

import microgram.api.Following;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class MongoProfiles implements Profiles {
	
	final String DB_NAME = "profilesDB";
	final String DB_TABLE = "profilesTable";
	final String USERID = "userId";
	final String FOL_TABLE = "followersTable";

	static MongoProfiles Profiles;
	MongoClient mongo;
	CodecRegistry pojoCodecRegistry;
	MongoDatabase dbProfiles;
	MongoCollection<Profile> dbCol;
	MongoCollection<Following> dbFollowing;
	
	public MongoProfiles() {
		Profiles = this;
		mongo = new MongoClient("localhost");
		pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		dbProfiles = mongo.getDatabase(DB_NAME).withCodecRegistry(pojoCodecRegistry);
		dbCol = dbProfiles.getCollection(DB_TABLE, Profile.class);
		dbFollowing = dbProfiles.getCollection(FOL_TABLE, Following.class);
		
		dbCol.createIndex(Indexes.ascending(USERID), new IndexOptions().unique(true));
		dbFollowing.createIndex(Indexes.ascending("userId1", "userId2"), new IndexOptions().unique(true));
		
	}
	
	@Override
	public Result<Profile> getProfile(String userId) {
		Profile res = dbCol.find(Filters.eq(USERID, userId)).first();
		if (res != null) {
			res.setPosts((int) Posts.dbCol.countDocuments(Filters.eq("ownerId", userId)));
			res.setFollowers((int) dbFollowing.countDocuments(Filters.eq("userId2", userId)));
			res.setFollowing((int) dbFollowing.countDocuments(Filters.eq("userId1", userId)));
			return ok(res);
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		try {
			dbCol.insertOne(profile);
			return ok();
		} catch (MongoWriteException x) {
			return error(CONFLICT);
		}
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		DeleteResult res = dbCol.deleteOne(Filters.eq(USERID, userId));
		
		//apagar likes, posts e follows
		if (res.getDeletedCount() == 1) {
			Posts.dbLikes.deleteMany(Filters.eq("userId", userId));
			Posts.dbCol.deleteMany(Filters.eq("userId", userId));
			dbFollowing.deleteMany(Filters.eq("userId1", userId));
			dbFollowing.deleteMany(Filters.eq("userId2", userId));
			return ok();
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		List<Profile> res = new ArrayList<>();

		String regex = "^" + prefix + ".*";

		MongoCursor<Profile> cursor = dbCol.find(Filters.regex(USERID, regex)).iterator();
		while (cursor.hasNext()) {
			res.add(cursor.next());
		}
		return ok(res);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		if (dbCol.find(Filters.eq("userId", userId1)).first() == null
				|| dbCol.find(Filters.eq("userId", userId2)).first() == null)
			return error(NOT_FOUND);
		else {
			if (isFollowing) {
				if(isFollowing(userId1, userId2).value()) {
					return error(CONFLICT);
				} else
				dbFollowing.insertOne(new Following(userId1, userId2));
			} else {
				if(!isFollowing(userId1, userId2).value()) {
					return error(NOT_FOUND);
				} else
				dbFollowing.deleteOne(Filters.and(Filters.eq("userId1", userId1), Filters.eq("userId2", userId2)));
			}
		}
		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		if (dbCol.find(Filters.eq("userId", userId1)).first() == null
				|| dbCol.find(Filters.eq("userId", userId2)).first() == null)
			return error(NOT_FOUND);
		else {
			Following res = dbFollowing
					.find(Filters.and(Filters.eq("userId1", userId1), Filters.eq("userId2", userId2))).first();
			return res != null ? ok(true) : ok(false);
		}
	}
}
