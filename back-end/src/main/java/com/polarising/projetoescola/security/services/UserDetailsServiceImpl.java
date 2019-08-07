package com.polarising.projetoescola.security.services;

import com.polarising.projetoescola.error.ApiRequestException;
import com.polarising.projetoescola.model.User;
import com.polarising.projetoescola.services.HttpSOAPRequestService;
import com.polarising.projetoescola.services.XmlParserService;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	HttpSOAPRequestService http;

	@Autowired
	XmlParserService xmlParserService;

	@Value("${polarising.app.tibco.users.url}")
	private String url;

	@Value("${polarising.app.tibco.users}")
	private String getUsersUrl;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<User> users;
		try {
			users = fetchData();
			User userToBuild = null;

			for (User user : users) {
				if (user.getUsername().equals(username)) {
					userToBuild = user;
				}
			}

			return UserPrinciple.build(userToBuild);
		} catch(ApiRequestException e) {
			throw new ApiRequestException(e.getLocalizedMessage());
		}
		catch (Exception e) {
			throw new ApiRequestException("Invalid username/password combination");
		}

	}

	private List<User> fetchData() throws IOException {
		String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ "   <soapenv:Header/>\n" + "   <soapenv:Body/>\n" + "</soapenv:Envelope>";
		try {
			List<User> users = xmlParserService.getUsersFromXML(http.post(url, "/users", requestBody, getUsersUrl));
			return users;
		} catch (Exception e) {
			throw new ApiRequestException("Can't reach server");
		}

	}
}