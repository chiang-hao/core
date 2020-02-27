package haoframe.core.generator.clazz;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import haoframe.core.generator.clazz.GeneratorClass.PropertyDataType;
import haoframe.core.utils.ClassUtils;


public class GeneratorClassTest {

	public static void main(String[] args) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://127.0.0.1:8080/get_object");
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity(); 
			InputStream is = entity.getContent(); 
			GeneratorClass  generatorClass = new GeneratorClass();
			generatorClass.addProperty("name",PropertyDataType.STRING);
			generatorClass.addProperty("count",PropertyDataType.DECIMAL);
			Class<?> clazz = generatorClass.create("com.wimi.helloworld.controll","UserInfo");
			if(clazz!=null) {
				ObjectInputStream objInt=new ObjectInputStream(is);
				Object obj=clazz.newInstance();
				obj=objInt.readObject();
				System.out.println(ClassUtils.getFieldValue(obj, "name"));
				System.out.println(ClassUtils.getFieldValue(obj, "count"));
				
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  
	}
	
}
