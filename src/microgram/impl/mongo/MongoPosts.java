package microgram.impl.mongo;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.impl.mongo.MongoProfiles.*;

import java.util.Collections;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Filters;

import microgram.api.*;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class MongoPosts implements Posts {

	MongoClient mongo = new MongoClient("localhost");

	CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	MongoDatabase dbPosts = mongo.getDatabase("postsDB").withCodecRegistry(pojoCodecRegistry);	

	MongoCollection<Post> dbCol = dbPosts.getCollection("postsCol", Post.class);
	
	String postIndex = dbCol.createIndex(Indexes.ascending("postId"), new IndexOptions().unique(true));
	
	//NOT SURE
	MongoCollection<Likes> dbLikes = dbPosts.getCollection("likesCol", Likes.class);
	MongoCollection<UserPosts> dbUserPosts = dbPosts.getCollection("userPostsCol", UserPosts.class);
	
	
	@Override
	public Result<Post> getPost(String postId) {
		// TODO Auto-generated method stub
		Post res = dbCol.find(Filters.eq("postId", postId)).first();
		if (res != null) {
			return ok(res);
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<String> createPost(Post post) {
		// TODO Auto-generated method stub
		String ownerId = post.getOwnerId();
		
		//if (!Profiles.getProfile(ownerId).isOK())
			//return error(NOT_FOUND);
		
		return null;
	}

	@Override
	public Result<Void> deletePost(String postId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
