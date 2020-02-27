package chianghao.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class FileUtils {

	
	public static String inputStreamToBase64(InputStream in) {
        byte[] data = null;
		try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = in.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		if(data==null) {
			return "";
		}
		return Base64.getEncoder().encodeToString(data);
	}
	
	
	public static byte[] is2ByeteArray(InputStream is){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			while((rc=is.read(buff, 0, 100))>0) {
				baos.write(buff, 0, rc);
			}
			return baos.toByteArray();
		}catch(Exception e) {
			
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}

	
	public static String getFilePath(String fileName,String... paths) {
		StringBuffer sb = new StringBuffer();
		if(paths!=null) {
			for(String path:paths) {
				sb.append("/"+path);
			}
		}
		sb.append("/"+fileName);
		return sb.toString();
	}
	
	
	public static File getStoreFile(String fileName,String  rootDir,String... paths) {
		File root = new File(rootDir);
		if(!root.exists()) {
			root.mkdir();
		}
		File imageFileDir=root;
		if(paths!=null) {
			for(String path:paths) {
				imageFileDir = new File(imageFileDir,path);
				if(!imageFileDir.exists()) {
					imageFileDir.mkdir();
				}
			}
		}
		File imageFile = new File(imageFileDir,fileName);
		return imageFile;
	}
	
	public static void write(byte[] imgbyte,String  rootDir ,String fileName,String... paths) {
		try {
			File root = new File(rootDir);
			if(!root.exists()) {
				root.mkdir();
			}
			File imageFileDir=root;
			if(paths!=null) {
				for(String path:paths) {
					imageFileDir = new File(imageFileDir,path);
					if(!imageFileDir.exists()) {
						imageFileDir.mkdir();
					}
				}
			}
			File imageFile = new File(imageFileDir,fileName);
			OutputStream os = new FileOutputStream(imageFile);
			os.write(imgbyte, 0, imgbyte.length);
			os.flush();
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
