package com.polarising.projetoescola.services;

import java.nio.charset.Charset;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpRequestService {
	private final RestTemplate restTemplate = getUf8Template();

	public String post(String url, String body) {

		String requestUrl = url;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);

		return restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class).getBody();
	}

	public String delete(String url, String body, String token) {

		String requestUrl = url;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		if (token != null) {
			headers.add("Authorization", "Bearer " + token);
		}
		HttpEntity<String> entity = null;
		entity = new HttpEntity<String>(body, headers);

		return restTemplate.exchange(requestUrl, HttpMethod.DELETE, entity, String.class).getBody();
	}

	private RestTemplate getUf8Template() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

		return template;
	}
}
