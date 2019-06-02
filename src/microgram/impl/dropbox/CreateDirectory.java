package microgram.impl.dropbox;

import java.io.File;
import java.nio.file.Files;

public class CreateDirectory {

	public static void main(String[] args) throws Exception {
		
		String filename = "./src/microgram/impl/dropbox/joao.jpg";
		
//		File f = new File(filename);
		
		DropboxMedia.createClientWithAccessToken().delete("joao");
	}

	
}