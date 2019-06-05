package microgram.impl.mongo;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

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
		if (res.getDeletedCount() == 1) {
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
		if (dbCol.find(Filters.eq("userId1", userId1)).first() == null
				|| dbCol.find(Filters.eq("userId2", userId2)).first() == null)
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
		// TODO Auto-generated method stub
		if (dbFollowing.find(Filters.eq("userId1", userId1)).first() == null
				|| dbFollowing.find(Filters.eq("userId2", userId2)).first() == null)
			return error(NOT_FOUND);
		else {
			Following res = dbFollowing
					.find(Filters.and(Filters.eq("userId1", userId1), Filters.eq("userId2", userId2))).first();
			if (res != null)
				return ok(true);
			else
				return ok(false);
		}
	}

	public Result<List<String>> following(String userId) {
		List<String> res = new ArrayList<>();
		MongoCursor<Following> cursor = dbFollowing.find(Filters.eq("userId1", userId)).iterator();
		while (cursor.hasNext()) {
			res.add(cursor.next().getUserId2());
		}
		return ok(res);
	}
}
