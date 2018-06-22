/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.blade.samples.modellistener;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Liferay
 */
@Component(immediate = true,
		   name = "booXtream",
		   service = ModelListener.class,
		   property = {
//				   "service.ranking=999"
		   })
public class BooXtreamListener extends BaseModelListener<AssetEntry> {
	
	private static Log _log = LogFactoryUtil.getLog(BooXtreamListener.class);
	
	

	@Override
	public void onAfterCreate(AssetEntry model) throws ModelListenerException {
		
		_log.debug("booxtream ready for takeoff");
		System.out.println("Starting BooXtreamListener module with model: " + model.getClassName());
		if (model.getClassName().equalsIgnoreCase(DLFileEntry.class.getName())) {

			
			System.out.println("Created DLFileEntry: " + model.getTitle());
			
			// check for tag 'booxtream'
			List<AssetTag> tags = _assetTagService.getTags(model.getGroupId(), "booxtream", 0, 1);
			if (tags.size() > 0 && model.getTags().contains(tags.get(0))) {
				System.out.println("Uploading document to booxtream");
				// get document and upload to booxtream
				try {
					DLFileEntry f = null;
					f = _dlFileEntryService.getFileEntry(model.getClassPK());
					/*InputStream result = watermarkFile(f.getContentStream());
					
					_dlFileEntryService.updateFileEntry(f.getFileEntryId(), 
														f.getFileName(), 
														f.getMimeType(), 
														f.getTitle(), 
														f.getDescription(),
														"",
														false,
														f.getFileEntryTypeId(),
														f.getDDMFormValuesMap(f.getFileVersion().getFileVersionId()),
														null, 
														result,
														0,
														_serviceContext);*/
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}	
	}
	
	/*private InputStream watermarkFile(InputStream input) throws ClientProtocolException, IOException {
		
		
		curl -u liferaybentest:GTUs89cshg9zetRLWqm8HPcxd4tqmn -i -X POST \
		-F 'epubfile=@/tmp/9789491833212_preview_edition_sample_ebook.epub' \
		-F "referenceid=12345678" \
		-F "languagecode=1033" \
		-F "disclaimer=1" \
		-F "customername=ACME Corporate" \
		-o /tmp/watermarked-edition.epub \
		https://service.booxtream.com/booxtream.epub
		
		
		// http://www.baeldung.com/httpclient-multipart-upload
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
		 = new UsernamePasswordCredentials("liferaybentest", "GTUs89cshg9zetRLWqm8HPcxd4tqmn");
		provider.setCredentials(AuthScope.ANY, credentials);
		  
		HttpClient client = HttpClientBuilder.create()
		  .setDefaultCredentialsProvider(provider)
		  .build();
		
		HttpPost post = new HttpPost("https://service.booxtream.com/booxtream.epub");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addBinaryBody("epubfile", input);
		builder.addTextBody("referenceid", "987654321");
		builder.addTextBody("languagecode", "1033");
		builder.addTextBody("disclaimer", "1");
		builder.addTextBody("customername", "ACME Corp.");
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		return response.getEntity().getContent();
	}*/
	
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY)
	protected AssetTagService _assetTagService;
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY)
	protected DLFileEntryService _dlFileEntryService;
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY)
	protected ServiceContext _serviceContext;
	

}