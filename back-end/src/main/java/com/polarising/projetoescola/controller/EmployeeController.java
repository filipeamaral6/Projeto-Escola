package com.polarising.projetoescola.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.polarising.projetoescola.error.ApiRequestException;
import com.polarising.projetoescola.message.request.CreateEmployeeForm;
import com.polarising.projetoescola.message.request.EditForm;
import com.polarising.projetoescola.message.response.ResponseMessage;
import com.polarising.projetoescola.model.Employee;
import com.polarising.projetoescola.model.EmployeeStatus;
import com.polarising.projetoescola.services.DateFormatterService;
import com.polarising.projetoescola.services.HttpRequestService;
import com.polarising.projetoescola.services.HttpSOAPRequestService;
import com.polarising.projetoescola.services.XmlParserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class EmployeeController {

	@Autowired
	HttpSOAPRequestService httpSoap;

	@Autowired
	HttpRequestService http;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	XmlParserService xmlParserService;

	@Autowired
	DateFormatterService dateFormatter;

	@Value("${polarising.app.tibco.employees.url}")
	private String url;

	@Value("${polarising.app.tibco.employees}")
	private String getEmployeesUrl;

	@Value("${polarising.app.tibco.employees.active}")
	private String getActiveEmployeesUrl;

	@Value("${polarising.app.tibco.employee.get}")
	private String getEmployeeUrl;

	@Value("${polarising.app.tibco.employee.add}")
	private String addEmployeeUrl;

	@Value("${polarising.app.tibco.employee.delete}")
	private String deleteEmployeeUrl;

	@Value("${polarising.app.tibco.employee.update}")
	private String updateEmployeeUrl;

	@Value("${polarising.app.emailDomain}")
	private String emailDomain;

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

	@Value("${polarising.app.office365.url}")
	private String office365Url;

	@PostMapping("/employees/add")
	public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeeForm createForm)
			throws IOException, ParseException {
		String response = null;
		try {

			Employee employee = new Employee(createForm.getFirstName(), createForm.getLastName(),
					createForm.getUsername(), createForm.getPersonalEmail(), createForm.getStartedAt(), emailDomain);

			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:emp=\"http://www.tibco.com/schemas/tibco/Schemas/Employee.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" + "      <emp:InputData createdAt=\""
					+ employee.getStartedAt() + "\" email=\"" + employee.getEmail() + "\" firstName=\""
					+ employee.getFirstName() + "\" id=\"?\" intranet=\"" + 0 + "\" lastName=\""
					+ employee.getLastName() + "\" office365=\"" + 0 + "\" personalEmail=\""
					+ employee.getPersonalEmail() + "\" slack=\"" + 0 + "\" startedAt=\"" + employee.getStartedAt()
					+ "\" username=\"" + employee.getUsername() + "\"/>\r\n" + "   </soapenv:Body>\r\n"
					+ "</soapenv:Envelope>";

			response = xmlParserService
					.getResponseMessage(httpSoap.post(url, "/employees/add", requestBody, addEmployeeUrl), "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException("Employee already exists");
			}

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (RestClientException e) {
			return new ResponseEntity<>(
					new ResponseMessage("Error while creating employee: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/admin/employees")
	public ResponseEntity<?> getEmployees() throws IOException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "   <soapenv:Header/>" + "   <soapenv:Body/>" + "</soapenv:Envelope>";

			xmlResponse = httpSoap.post(url, "/employees", requestBody, getEmployeesUrl);
			List<Employee> response = xmlParserService.getEmployeesFromXML(xmlResponse);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RestClientException e) {
			return new ResponseEntity<>(
					new ResponseMessage("Error while getting employees: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UnrecognizedPropertyException e) {
			return new ResponseEntity<>(
					new ResponseMessage(xmlParserService.getResponseMessage(xmlResponse, "message")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/employees/active")
	public ResponseEntity<?> getActiveEmployees() throws IOException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body/>\r\n" + "</soapenv:Envelope>";

			xmlResponse = httpSoap.post(url, "/employees/active", requestBody, getActiveEmployeesUrl);
			List<Employee> response = xmlParserService.getEmployeesFromXML(xmlResponse);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RestClientException e) {
			return new ResponseEntity<>(
					new ResponseMessage("Error while getting employees: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UnrecognizedPropertyException e) {
			return new ResponseEntity<>(
					new ResponseMessage(xmlParserService.getResponseMessage(xmlResponse, "message")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/employees/{id}")
	public ResponseEntity<?> getEmployee(@PathVariable int id)
			throws JsonProcessingException, IOException, ParseException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:idon=\"http://www.tibco.com/schemas/tibco/Schemas/IdOnly.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <idon:InputData id=\"" + id + "\"/>\n"
					+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";

			xmlResponse = httpSoap.post(url, "/employee/id", requestBody, getEmployeeUrl);
			Employee response = xmlParserService.getEmployeesFromXML(xmlResponse).get(0);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RestClientException e) {
			return new ResponseEntity<>(new ResponseMessage("Error while getting employee: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonParseException | UnrecognizedPropertyException e) {
			String response = xmlParserService.getResponseMessage(xmlResponse, "message");
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/admin/employees/delete/{id}")
	public ResponseEntity<?> deactivateEmployee(@PathVariable int id) {
		try {
			
			try {
				Employee employee = (Employee) getEmployee(id).getBody();
				String employeeEmail = employee.getEmail();
				if(employee.isOffice365()) {
					RemoveOffice365(employeeEmail);
				}
			} catch (Exception e) {
				throw new ApiRequestException(e.getLocalizedMessage());
			}
			
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:idon=\"http://www.tibco.com/schemas/tibco/Schemas/IdOnly.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <idon:InputData id=\"" + id + "\"/>\n"
					+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";

			String response = xmlParserService.getResponseMessage(
					httpSoap.post(url, "/employees/delete", requestBody, deleteEmployeeUrl), "message");

			

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage("Error while deleting employee: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void RemoveOffice365(String email) throws JsonParseException, JsonMappingException, IOException {
		String token = getOfficeAuthToken();
		http.delete("https://graph.microsoft.com/v1.0/users/" + email, null, token);
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

	// to finish integrating with tibco
	@PutMapping("/employees/{id}")
	public ResponseEntity<?> updateEmployee(@PathVariable int id, @Valid @RequestBody EditForm editForm)
			throws JsonProcessingException, IOException, ParseException {
		ResponseEntity<?> getEmployeeResponse = null;
		try {
			getEmployeeResponse = getEmployee(id);
			Employee employee = (Employee) getEmployeeResponse.getBody();

			employee.setFirstName(editForm.getFirstName());
			employee.setLastName(editForm.getLastName());
			employee.setUsername(editForm.getUsername());
			employee.setEmail(editForm.getEmail());
			employee.setPersonalEmail(editForm.getPersonalEmail());
			employee.setStartedAt(editForm.getStartedAt());
			
			int office365 = 0;
			if(employee.isOffice365()) {
				office365 = 1;
			}
			int intranet = 0;
			if(employee.isIntranet()) {
				intranet = 1;
			}

			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:emp=\"http://www.tibco.com/schemas/tibco/Schemas/Employee.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n"
					+ "      <emp:InputData createdAt=\"?\" email=\"" + employee.getEmail() + "\" firstName=\""
					+ employee.getFirstName() + "\" id=\"" + id + "\" intranet=\"" + intranet + "\" lastName=\""
					+ employee.getLastName() + "\" office365=\"" + office365 + "\" personalEmail=\"" + employee.getPersonalEmail()
					+ "\" slack=\"" + employee.getSlack() + "\" startedAt=\"" + employee.getStartedAt() + "\" username=\""
					+ employee.getUsername() + "\" status=\"" + employee.getStatus() + "\"/>\r\n" + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

			String response = xmlParserService.getResponseMessage(
					httpSoap.post(url, "/employee/update", requestBody, updateEmployeeUrl), "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException(response);
			}

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (ClassCastException e) { // if response from getEmployee is a parsed error message
			return getEmployeeResponse;
		} catch (RestClientException e) {
			return new ResponseEntity<>("Error while updating employee: " + e.getLocalizedMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/admin/employees/activate/{id}")
	public ResponseEntity<?> activateEmployee(@PathVariable int id)
			throws JsonProcessingException, IOException, ParseException {
		ResponseEntity<?> getEmployeeResponse = null;
		try {
			getEmployeeResponse = getEmployee(id);
			Employee employee = (Employee) getEmployeeResponse.getBody();

			employee.setStatus(EmployeeStatus.ACTIVE.toString());
			
			int office365 = 0;
			if(employee.isOffice365()) {
				office365 = 1;
			}
			int intranet = 0;
			if(employee.isIntranet()) {
				intranet = 1;
			}

			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:emp=\"http://www.tibco.com/schemas/tibco/Schemas/Employee.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n"
					+ "      <emp:InputData createdAt=\"?\" email=\"" + employee.getEmail() + "\" firstName=\""
					+ employee.getFirstName() + "\" id=\"" + id + "\" intranet=\"" + intranet + "\" lastName=\""
					+ employee.getLastName() + "\" office365=\"" + office365 + "\" personalEmail=\"" + employee.getPersonalEmail()
					+ "\" slack=\"" + employee.getSlack() + "\" startedAt=\"" + employee.getStartedAt() + "\" username=\""
					+ employee.getUsername() + "\" status=\"" + employee.getStatus() + "\"/>\r\n"
					+ "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

			String response = xmlParserService.getResponseMessage(
					httpSoap.post(url, "/employee/update", requestBody, updateEmployeeUrl), "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException(response);
			}

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (ClassCastException e) { // if response from getEmployee is a parsed error message
			return getEmployeeResponse;
		} catch (RestClientException e) {
			return new ResponseEntity<>("Error while updating employee: " + e.getLocalizedMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
