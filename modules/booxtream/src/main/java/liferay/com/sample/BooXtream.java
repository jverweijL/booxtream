package liferay.com.sample;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author jverweij
 */

@Component(
	immediate = true,
	name = "janXtream",
	service = ModelListener.class
)
public class BooXtream extends BaseModelListener<AssetEntry> {
	
	//private static Log _log = LogFactoryUtil.getLog(BooXtream.class);

	@Override
	public void onAfterCreate(AssetEntry model) throws ModelListenerException {
		// TODO Auto-generated method stub
		
		System.out.println("New BooXtream is alive 85!");
		
		if (model.getClassName().equalsIgnoreCase(DLFileEntry.class.getName())) {
			
			System.out.println("Created DLFileEntry: " + model.getTitle());
			
			// check for tag 'booxtream'
			List<AssetTag> tags = _assetTagService.getTags(model.getGroupId(), "booxtream", 0, 1);
			if (tags.size() > 0 && model.getTags().contains(tags.get(0))) {
				System.out.println("Uploading document to booxtream");
				// get document and upload to booxtream
				try {
					//DLFileEntry f = null;
					//f = _dlFileEntryService.getFileEntry(model.getClassPK());
					FileEntry f = null;
					f = _dlAppService.getFileEntry(model.getClassPK());
					
					InputStream result = watermarkFile(f.getContentStream());
					
					ServiceContext serviceContext = new ServiceContext();
					serviceContext.setCompanyId(model.getCompanyId());
					
					_dlAppService.updateFileEntry(f.getFileEntryId(), 
												  f.getFileName(), 
												  f.getMimeType(), 
												  f.getTitle(), 
												  f.getDescription(), 
												  "", 
												  false, 
												  createTempFile(result), 
												  serviceContext);
					
					/*_dlFileEntryService.updateFileEntry(f.getFileEntryId(), 
														f.getFileName(), 
														f.getMimeType(), 
														f.getTitle(), 
														f.getDescription(),
														"",
														false,
														f.getFileEntryTypeId(),
														f.getDDMFormValuesMap(f.getFileVersion().getFileVersionId()),
														createTempFile(result), 
														null,
														0,
														serviceContext);*/
				} catch (PortalException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		
	}
	
	private InputStream watermarkFile(InputStream input) throws ClientProtocolException, IOException {
		
		//curl -u liferaybentest:GTUs89cshg9zetRLWqm8HPcxd4tqmn -i -X POST \
		//-F 'epubfile=@/tmp/9789491833212_preview_edition_sample_ebook.epub' \
		//-F "referenceid=12345678" \
		//-F "languagecode=1033" \
		//-F "disclaimer=1" \
		//-F "customername=ACME Corporate" \
		//-o /tmp/watermarked-edition.epub \
		//https://service.booxtream.com/booxtream.epub
		
		 
	    // get inputstream and store to temp file then pass file
		

		
		
		// http://www.baeldung.com/httpclient-multipart-upload
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
		 = new UsernamePasswordCredentials("liferaybentest", "GTUs89cshg9zetRLWqm8HPcxd4tqmn");
		provider.setCredentials(AuthScope.ANY, credentials);
		
		// build multipart upload request
        HttpEntity data = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)              
                .addBinaryBody("epubfile", createTempFile(input))
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
	
	private File createTempFile(InputStream input) throws IOException {
		File tmpDir = new File("/tmp");
		File tempFile = File.createTempFile("booxtream", ".epub",tmpDir);

	    // Delete temp file when program exits.
	    //tempFile.deleteOnExit();
	    
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
	 	
	 	return tempFile;
	}
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY)
	protected AssetTagService _assetTagService;
	
	//@Reference(cardinality=ReferenceCardinality.MANDATORY)
	//protected DLFileEntryService _dlFileEntryService;
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY)
	protected DLAppService _dlAppService;

}