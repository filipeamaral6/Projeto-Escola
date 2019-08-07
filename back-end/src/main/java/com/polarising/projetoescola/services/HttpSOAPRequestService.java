package com.polarising.projetoescola.services;

import java.nio.charset.Charset;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpSOAPRequestService {

	private final RestTemplate restTemplate = getUf8Template();

	public String post(String url, @Nullable String subPath, String body, String soapAction) {
		if (subPath == null) {
			subPath = "";
		}
		String requestUrl = url + subPath;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/xml");
		headers.add("SOAPAction", soapAction);
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);

		return restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class).getBody();
	}
	
	private RestTemplate getUf8Template() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		
		return template;
	}
}
