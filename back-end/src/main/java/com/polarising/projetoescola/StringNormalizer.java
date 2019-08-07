package com.polarising.projetoescola;

import java.text.Normalizer;

import org.springframework.stereotype.Service;

@Service
public class StringNormalizer {

	private static StringNormalizer instance = null;
	
	/**
	 * Class by https://gist.github.com/rponte
	 */
	public String unaccent(String src) {
		return Normalizer
				.normalize(src, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
	}
	
	
	public static StringNormalizer getInstace() {
		if (instance == null) {
			instance = new StringNormalizer();
		}
		
		return instance;
	}
	
}