package microgram.impl.mongo;

import java.util.List;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class MongoProfiles implements Profiles {

	@Override
	public Result<Profile> getProfile(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		// TODO Auto-generated method stub
		return null;
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
