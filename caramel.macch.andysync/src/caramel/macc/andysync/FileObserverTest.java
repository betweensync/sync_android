package caramel.macc.andysync;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class FileObserverTest extends Thread{
	String baseDir = "/";
	int interval = 1000;
	
	public FileObserverTest(String baseDir){
		this.baseDir = baseDir;
	}
	
	public void run(){
		Log.i("observe test", "dir observation starts..[" + this.baseDir + "]");
		
		try {
			File f = new File(this.baseDir);
			if (f.exists()){
				this.deleteSub(f);
			}
			Log.d("observe test", "---------------------");
			list(this.baseDir);
			
			String path = this.baseDir + File.separator + "test-file";
			
			Thread.sleep(interval);
			Log.i("observe test", "1. createNewFile [" + path + "]");
			// create a file..
			f = new File(path);
			f.createNewFile();			
			Thread.sleep(interval);
			
			Log.i("observe test", "1.1 renameTo [" + path + "]");
			// create a file..
			f = new File(path);
			path = path.replace("test-file", "test-file-rename");
			f.renameTo(new File(path));
			Thread.sleep(interval);			
			
			Log.i("observe test", "2. delete [" + path + "]");
			// delete a file..
			f = new File(path);
			f.delete();			
			Thread.sleep(interval);			
			
			// dir create..
			path = this.baseDir + File.separator + "sub-dir1";
			f = new File(path);
			f.mkdirs();
			Log.i("observe test", "3. mkdirs [" + path + "]");	
			Thread.sleep(interval);
			
			// sub file crate under new dir..
			path += File.separator +  "test-file-sub";
			f = new File(path);
			//f.mkdirs();
			f.createNewFile();
			Log.i("observe test", "4. createNewFile [" + path + "]");			
			Thread.sleep(interval);
			
			path = path.replace("sub-dir1/", "");
			Log.i("observe test", "4.1 move to [" + path + "]");		
			f.renameTo(new File(path));
			Thread.sleep(interval);
			
			// delete a sub file..
			f = new File(path);
			f.delete();
			Log.i("observe test", "5. delete [" + path + "]");
			Thread.sleep(interval);
			
			// create sub sub dir ..
			path = this.baseDir + File.separator + "sub-dir2" +File.separator + "sub-sub-dir1";
			f = new File(path);
			f.mkdirs();
			//f.createNewFile();			
			Log.i("observe test", "6. mkdirs [" + path + "]");			
			
			list(this.baseDir);
			
			Thread.sleep(interval);
			
			
			// rename parent dir..
			path = this.baseDir + File.separator + "sub-dir2";
			f = new File(path);
			path = this.baseDir + File.separator + "sub-dir2-rename";
			f.renameTo(new File(path));
			Log.i("observe test", "6.1 rename dir [" + path + "]");			
			
			list(this.baseDir);
			
			Thread.sleep(interval);
			
			// file crate under new dir..
			path += File.separator + "file-sub-sub";
			f = new File(path);
			f.createNewFile();
			Log.i("observe test", "7. createNewFile [" + path + "]");
			Thread.sleep(interval);
			
			// delete for test dir..
			path = this.baseDir + File.separator + "sub-dir2-rename";
			f = new File(path);
			deleteSub(f);
			f.delete();
			Log.i("observe test", "8. delete dir [" + path + "]");
			Thread.sleep(interval);
				
			list(this.baseDir);
			
			Log.i("observe test", "dir observation ends..[" + baseDir + "]");
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	private void deleteSub(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()){
				if(c.isDirectory())
					deleteSub(c);
				
				c.delete();
			}
		}
		
	}

	private static void list(String dir){
		String[] list = new File(dir).list();
		
		if (list == null)
			return;
		
		for(String name : list){
			Log.d("LIST", " - " + name);
			if (new File(name).isDirectory()){
				list(name);
			}
		}
		
	}
}
