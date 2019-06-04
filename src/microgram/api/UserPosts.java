
package microgram.api;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserPosts {

		private String userId;
		private String postId;

	    @BsonCreator
	    public UserPosts(@BsonProperty("userId")String userId, @BsonProperty("postId") String postId) {
	    	this.userId = userId;
	    	this.postId = postId;
	    }

}