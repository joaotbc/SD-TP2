package microgram.impl.mongo;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import microgram.api.Followers;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class MongoProfiles implements Profiles {

	static MongoProfiles Profiles;
	
	final String DB_NAME = "profilesDB";
	final String DB_TABLE = "profilesTable";
	final String USERID = "userId";
	final String FOL_TABLE = "followersTable";

	MongoClient mongo = new MongoClient("localhost");

	CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	MongoDatabase dbProfiles = mongo.getDatabase(DB_NAME).withCodecRegistry(pojoCodecRegistry);	

	MongoCollection<Profile> dbCol = dbProfiles.getCollection(DB_TABLE, Profile.class);
	
	String indexProfiles = dbCol.createIndex(Indexes.ascending(USERID), new IndexOptions().unique(true));
	
	MongoCollection<Followers> dbFollowers = dbProfiles.getCollection(FOL_TABLE, Followers.class);
	
	@Override
	public Result<Profile> getProfile(String userId) {
		// TODO Auto-generated method stub
		Profile res = dbCol.find(Filters.eq(USERID, userId)).first();
		if (res != null) {
			return ok(res);
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		// TODO Auto-generated method stub
		try {
			dbCol.insertOne(profile);
			return ok();
		}
		catch (Exception e) {
			return error(CONFLICT);
		}
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		// TODO Auto-generated method stub
		return null;
	}

}
