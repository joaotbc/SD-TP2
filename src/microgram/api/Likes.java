package microgram.api;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Likes {

		private String postId;
	    private String userId;

	    @BsonCreator
	    public Likes(@BsonProperty("postId") String postId, @BsonProperty("userId")String userId) {
	        this.postId = postId;
	        this.userId = userId;

	    }

}
