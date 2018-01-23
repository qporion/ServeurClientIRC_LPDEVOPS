package server;

import java.io.File;

public class ShutdownHook extends Thread {

	public void run() {
		System.out.println("Clearing upload directory !");
		File theDir = new File(MainServer.UPLOAD_DIR);
		deleteDir(theDir);
	}
	
	private void deleteDir(File file) {
		
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}

}
