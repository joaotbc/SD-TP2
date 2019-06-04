package microgram.impl.dropbox;

public class ListDirectory {
	
	public static void main(String[] args) throws Exception {
		
		DropboxMedia.createClientWithAccessToken().listDirectory("");
	}
}
