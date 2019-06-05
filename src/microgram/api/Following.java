package microgram.api;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Following {

	private String userId1;
    private String userId2;

    @BsonCreator
    public Following(@BsonProperty("userId1") String userId1, @BsonProperty("userId2")String userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;

    }
    
	public String getUserId1() {
		return userId1;
	}
	
	public String getUserId2() {
		return userId2;
	}

}