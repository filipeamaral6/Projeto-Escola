package com.polarising.projetoescola.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarising.projetoescola.error.ApiRequestException;
import com.polarising.projetoescola.message.request.InviteData;
import com.polarising.projetoescola.message.response.ResponseMessage;
import com.polarising.projetoescola.services.EmailSenderService;
import com.polarising.projetoescola.services.HttpRequestService;
import com.polarising.projetoescola.services.HttpSOAPRequestService;
import com.polarising.projetoescola.services.XmlParserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PlatformsController {

	@Autowired
	HttpSOAPRequestService httpSoap;

	@Autowired
	HttpRequestService http;

	@Autowired
	XmlParserService xmlParserService;

	@Autowired
	EmailSenderService emailSender;

	@Value("${polarising.app.tibco.platforms.url}")
	private String tibcoUrl;

	@Value("${polarising.app.tibco.platforms.slack.invite}")
	private String inviteSlackUrl;

	@Value("${polarising.app.tibco.platforms.intranet.invite}")
	private String inviteIntranetUrl;

	@Value("${polarising.app.office365.url}")
	private String office365Url;

	@Value("${polarising.app.tibco.platforms.office365.invite}")
	private String inviteOffice365Url;

	@Value("${polarising.app.office365.tenantId}")
	private String tenantId;

	@Value("${polarising.app.office365.clientId}")
	private String clientId;

	@Value("${polarising.app.office365.clientSecret}")
	private String clientSecret;

	@Value("${polarising.app.office365.username}")
	private String username;

	@Value("${polarising.app.office365.password}")
	private String password;

	@PostMapping("/platforms/slack/invite")
	public ResponseEntity<?> inviteSlack(@RequestBody InviteData inviteData) {
		String response = null;
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:idon=\"http://www.tibco.com/schemas/tibco/Schemas/IdOnly.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" + "      <idon:InputData id=\""
					+ inviteData.getId() + "\"/>\r\n" + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

			xmlResponse = httpSoap.post(tibcoUrl, "/platform/slack/invite", requestBody, inviteSlackUrl);
			response = xmlParserService.getResponseMessage(xmlResponse, "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException("Error");
			}

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);

		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ResourceAccessException e) {
			System.out.println(e.getLocalizedMessage());
			return new ResponseEntity<>(new ResponseMessage("Finishing Slack invite"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ResponseMessage("Error while inviting to Slack: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/platforms/intranet/invite")
	public ResponseEntity<?> inviteIntranet(@RequestBody InviteData inviteData) {
		String response = null;
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sch=\"http://www.tibco.com/schemas/tibco/Schemas/Schema.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" + "    <sch:InputData email=\""
					+ inviteData.getPersonalEmail() + "\" firstName=\"" + inviteData.getFirstName() + "\" lastName=\""
					+ inviteData.getLastName() + "\" startDate=\"" + inviteData.getStartedAt() + "\" username=\""
					+ inviteData.getUsername() + "\"/>\r\n" + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

			xmlResponse = httpSoap.post(tibcoUrl, "/intranet/add", requestBody, inviteIntranetUrl);
			response = xmlParserService.getResponseMessage(xmlResponse, "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException("Error");
			}

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);

		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>("Error while inviting to Intranet: " + e.getLocalizedMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/platforms/office365/invite")
	public ResponseEntity<?> inviteOffice365(@RequestBody InviteData inviteData) {
		String response = null;
		String xmlResponse = null;
		try {
			String token = getOfficeAuthToken();

			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:off=\"http://www.tibco.com/schemas/tibco/Schemas/Office365.xsd2\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" + "      <off:InputData displayName=\""
					+ inviteData.getFirstName() + " " + inviteData.getLastName() + "\" mailNickname=\""
					+ inviteData.getUsername() + "\" password=\"" + inviteData.getPassword() + "\" token=\"" + token
					+ "\" userPrincipalName=\"" + inviteData.getEmail() + "\"/>\r\n" + "   </soapenv:Body>\r\n"
					+ "</soapenv:Envelope>";

			xmlResponse = httpSoap.post(tibcoUrl, "/office365/invite", requestBody, inviteOffice365Url);
			response = xmlParserService.getResponseMessage(xmlResponse, "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException("Error");
			}

			String emailSubject = "Polarising Employee Manager - Office 365 registration";

			File file = new File("src/main/resources/email/Office365Registration/Email.txt");
//		    Scanner scanner = new Scanner(file);
			String emailBody = FileUtils.readFileToString(file, "UTF-8");

			emailBody = emailBody.replace("$fullName", inviteData.getFirstName() + " " + inviteData.getLastName());
			emailBody = emailBody.replace("$email", inviteData.getEmail());
			emailBody = emailBody.replace("$password", inviteData.getPassword());
			emailBody = emailBody.replace("$loginUrl", "https://login.microsoftonline.com");

			Map<String, String> inlineImages = new HashMap<String, String>();
			inlineImages.put("company_logo", "src/main/resources/email/Office365Registration/images/company_logo.png");
			inlineImages.put("facebook@2x", "src/main/resources/email/Office365Registration/images/facebook@2x.png");
			inlineImages.put("linkedin@2x", "src/main/resources/email/Office365Registration/images/linkedin@2x.png");
			inlineImages.put("office365", "src/main/resources/email/Office365Registration/images/office365.png");

			emailSender.SendEmail(inviteData.getPersonalEmail(), emailSubject, emailBody, inlineImages);

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);

		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>("Error while inviting to Office 365: " + e.getLocalizedMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/platforms/office365/delete/{email}")
	public ResponseEntity<?> RemoveOffice365(@PathVariable String email) {
		try {
			http.delete("https://graph.microsoft.com/v1.0/users/" + email, null, getOfficeAuthToken());
			return new ResponseEntity<>(new ResponseMessage("Removed Office 365 account successfully"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error while removing Office 365 account: " + e.getLocalizedMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getOfficeAuthToken() throws JsonParseException, JsonMappingException, IOException {
		String token = null;
		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = "grant_type=password&client_id=" + clientId + "&client_secret=" + clientSecret
				+ "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default&userName="
				+ URLEncoder.encode(username, StandardCharsets.UTF_8.toString()) + "&password=" + password + "";
		String url = office365Url.replace("{{TenantID}}", tenantId);

		String responseString = http.post(url, requestBody);

		// OfficeToken officeToken = objectMapper.readValue(responseString,
		// OfficeToken.class);
		JsonNode node = objectMapper.readTree(responseString);

		token = node.get("access_token").asText();

		return token;
	}
}
