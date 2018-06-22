package liferay.com.sample;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

public class TestXtream {

	public static void main(String[] args) throws ClientProtocolException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub

		InputStream result = watermarkFile(new FileInputStream("/liferay/projects/booXtream/dracula.epub"));
		
		OutputStream outputStream = null;
		
		
		 
		// write the inputStream to a FileOutputStream
				outputStream = 
		                    new FileOutputStream(new File("/tmp/dracula3.mod.epub"));

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = result.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				
				outputStream.close();
	}
	
	private static InputStream watermarkFile(InputStream input) throws ClientProtocolException, IOException {
		
		
		//curl -u liferaybentest:GTUs89cshg9zetRLWqm8HPcxd4tqmn -i -X POST \
		//-F 'epubfile=@/tmp/9789491833212_preview_edition_sample_ebook.epub' \
		//-F "referenceid=12345678" \
		//-F "languagecode=1033" \
		//-F "disclaimer=1" \
		//-F "customername=ACME Corporate" \
		//-o /tmp/watermarked-edition.epub \
		//https://service.booxtream.com/booxtream.epub
		
		 
	    // get inputstream and store to temp file then pass file
		File tempFile = File.createTempFile("booxtream", ".epub");

	    // Delete temp file when program exits.
	    tempFile.deleteOnExit();
	    
	 // write the inputStream to a FileOutputStream
	    OutputStream outputStream = null;
	 	outputStream = new FileOutputStream(tempFile);

	 	int read = 0;
	 	byte[] bytes = new byte[1024];

	 	while ((read = input.read(bytes)) != -1) {
	 		outputStream.write(bytes, 0, read);
	 	}
	 	input.close();
	 	outputStream.flush();
	 	outputStream.close(); 	

		
		
		// http://www.baeldung.com/httpclient-multipart-upload
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
		 = new UsernamePasswordCredentials("liferaybentest", "GTUs89cshg9zetRLWqm8HPcxd4tqmn");
		provider.setCredentials(AuthScope.ANY, credentials);
		
		// build multipart upload request
        HttpEntity data = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)              
                .addBinaryBody("epubfile", new File("/liferay/projects/booXtream/dracula.epub"))
                //.addBinaryBody("epubfile", new FileInputStream("/liferay/projects/booXtream/dracula.epub"), ContentType.create("application/epub+zip"), "dracula.epub")
                .addTextBody("referenceid", "987654321")
                .addTextBody("languagecode", "1033")
                .addTextBody("disclaimer", "1")
                .addTextBody("customername", "ACME Corp.")
                //.addTextBody("text", message, ContentType.DEFAULT_BINARY)
                .build();
		  
		HttpClient client = HttpClientBuilder.create()
		  .setDefaultCredentialsProvider(provider)
		  .build();
		
		
		// build http request and assign multipart upload data
        HttpUriRequest request = RequestBuilder
                .post("https://service.booxtream.com/booxtream.epub")
                .setEntity(data)
                .build();
		HttpResponse response = client.execute(request);
		System.out.println(response.getStatusLine());
		
		

		return response.getEntity().getContent();
	}

}
