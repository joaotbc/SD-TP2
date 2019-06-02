package microgram.impl.dropbox;

public class CreateDirectory {

	public static void main(String[] args) throws Exception {
		
//		String filename = "./src/microgram/impl/dropbox/joao.jpg";
		
//		File f = new File(filename);
		
		DropboxMedia.createClientWithAccessToken().delete("joao");
	}

	
}