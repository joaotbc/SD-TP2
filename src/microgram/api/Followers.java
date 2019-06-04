package microgram.api;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Followers {

	private String userId;
    private String userId2;

    @BsonCreator
    public Followers(@BsonProperty("userId") String userId, @BsonProperty("userId2")String userId2) {
        this.userId = userId;
        this.userId2 = userId2;

    }

}