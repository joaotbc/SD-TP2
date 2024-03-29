package microgram.impl.mongo;

import static microgram.api.java.Result.*;
import static microgram.api.java.Result.ErrorCode.*;
import static microgram.impl.mongo.MongoProfiles.Profiles;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Filters;

import microgram.api.*;
import microgram.api.java.Posts;
import microgram.api.java.Result;

public class MongoPosts implements Posts {

	static MongoPosts Posts;

	MongoClient mongo;
	CodecRegistry pojoCodecRegistry;
	MongoDatabase dbPosts;
	MongoCollection<Post> dbCol;
	MongoCollection<Likes> dbLikes;

	public MongoPosts() {
		Posts = this;
		mongo = new MongoClient("localhost");
		pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		dbPosts = mongo.getDatabase("postsDB").withCodecRegistry(pojoCodecRegistry);
		dbCol = dbPosts.getCollection("postsCol", Post.class);
		dbLikes = dbPosts.getCollection("likesCol", Likes.class);
		dbCol.createIndex(Indexes.ascending("postId", "ownerId"), new IndexOptions().unique(true));
	}

	@Override
	public Result<Post> getPost(String postId) {
		Post res = dbCol.find(Filters.eq("postId", postId)).first();
		res.setLikes((int) dbLikes.countDocuments(Filters.eq("postId", postId)));
		return res != null ? ok(res) : error(NOT_FOUND);
	}

	@Override
	public Result<String> createPost(Post post) {
		String ownerId = post.getOwnerId();
		if (!Profiles.getProfile(ownerId).isOK())
			return error(NOT_FOUND);
		try {
			dbCol.insertOne(post);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(post.getPostId());
	}

	@Override
	public Result<Void> deletePost(String postId) {
		Post post = dbCol.findOneAndDelete(Filters.eq("postId", postId));
		if (post != null) {
			dbLikes.deleteMany(Filters.eq("postId", postId));
			return ok();
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		Post res = dbCol.find(Filters.eq("postId", postId)).first();
		if (res != null) {
			if (isLiked) {
				if (dbLikes.find(Filters.and(Filters.eq("postId", postId), Filters.eq("userId", userId)))
						.first() != null)
					return error(CONFLICT);
				dbLikes.insertOne(new Likes(postId, userId));
			} else {
				if (dbLikes.find(Filters.and(Filters.eq("postId", postId), Filters.eq("userId", userId)))
						.first() == null)
					return error(NOT_FOUND);
				dbLikes.deleteOne(Filters.and(Filters.eq("postId", postId), Filters.eq("userId", userId)));
			}
			return ok();
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Post p = dbCol.find(Filters.eq("postId", postId)).first();
		if (p != null) {
			return dbLikes.find(Filters.and(Filters.eq("postId", postId), Filters.eq("userId", userId))).first() != null
					? ok(true)
					: ok(false);
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		if (!Profiles.getProfile(userId).isOK()) {
			return error(NOT_FOUND);
		}
		List<String> res = new ArrayList<String>();
		MongoCursor<Post> c = dbCol.find(Filters.eq("ownerId", userId)).iterator();
		while (c.hasNext())
			res.add(c.next().getPostId());
		return ok(res);
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		if (!Profiles.getProfile(userId).isOK())
			return error(NOT_FOUND);
		else {
			List<String> feed = new ArrayList<>();
			MongoCursor<Following> f = Profiles.dbFollowing.find(Filters.eq("userId1", userId)).iterator();
			while (f.hasNext()) {
				MongoCursor<Post> c = dbCol.find(Filters.eq("ownerId", f.next().getUserId2())).iterator();
				while (c.hasNext()) {
					feed.add(c.next().getPostId());
				}
//				feed.addAll(getPosts(followee).value());
			}
			return ok(feed);
		}
	}

}
