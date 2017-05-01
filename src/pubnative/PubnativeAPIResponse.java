package pubnative;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PubnativeAPIResponse {
	
	@Test
	public void verifyApiToken() throws IOException{
		
        FileInputStream fileInput =null;
        Properties properties = null;
        
		try {
			
			Path currentRelativePath = Paths.get("");
			String PATH = currentRelativePath.toAbsolutePath().toString();
			System.out.println("Getting the Request Parameteres from the Properties file");
			File file = new File(PATH + "\\requestParam.properties");
			fileInput = new FileInputStream(file);
			properties = new Properties();
			properties.load(fileInput);

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String urlWithParams = "https://api.pubnative.net/api/v3/native?";
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();

	    params.add(new BasicNameValuePair("apptoken", properties.getProperty("apiToken")));
		params.add(new BasicNameValuePair("os", properties.getProperty("os")));
		params.add(new BasicNameValuePair("al", properties.getProperty("al")));
		params.add(new BasicNameValuePair("mf", properties.getProperty("mf")));
        params.add(new BasicNameValuePair("osver", properties.getProperty("osver")));
        params.add(new BasicNameValuePair("devicemodel", properties.getProperty("devicemodel")));
		params.add(new BasicNameValuePair("dnt", properties.getProperty("dnt")));
        params.add(new BasicNameValuePair("zoneid", properties.getProperty("zoneid")));

	    String paramString = URLEncodedUtils.format(params, "utf-8");

	    urlWithParams += paramString;
				
		URL url = new URL(urlWithParams);
		HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		int code = connection.getResponseCode();
		System.out.println("Response code is : " + code);
		Assert.assertTrue((code == 200), "Verifying Response Code");
		
		String responseMessage = connection.getResponseMessage();
		System.out.println("Response message is : " + responseMessage);
	}

}
