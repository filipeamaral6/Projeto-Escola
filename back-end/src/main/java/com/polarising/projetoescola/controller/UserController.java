package com.polarising.projetoescola.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.polarising.projetoescola.error.ApiRequestException;
import com.polarising.projetoescola.message.request.ChangePasswordForm;
import com.polarising.projetoescola.message.request.DeleteUserData;
import com.polarising.projetoescola.message.request.EditForm;
import com.polarising.projetoescola.message.request.ForgotPasswordForm;
import com.polarising.projetoescola.message.request.LoginForm;
import com.polarising.projetoescola.message.request.ResetPasswordForm;
import com.polarising.projetoescola.message.request.SignupForm;
import com.polarising.projetoescola.message.response.JwtResponse;
import com.polarising.projetoescola.message.response.ResponseMessage;
import com.polarising.projetoescola.model.Role;
import com.polarising.projetoescola.model.User;
import com.polarising.projetoescola.security.jwt.JwtProvider;
import com.polarising.projetoescola.security.services.UserPrinciple;
import com.polarising.projetoescola.services.EmailSenderService;
import com.polarising.projetoescola.services.HttpSOAPRequestService;
import com.polarising.projetoescola.services.XmlParserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	XmlParserService xmlParserService;

	@Autowired
	HttpSOAPRequestService http;

	@Autowired
	EmailSenderService emailSender;

	@Value("${polarising.app.emailDomain}")
	private String emailDomain;

	@Value("${polarising.app.tibco.users.url}")
	private String url;

	@Value("${polarising.app.tibco.users}")
	private String getUsersUrl;

	@Value("${polarising.app.tibco.users.get}")
	private String getUserUrl;

	@Value("${polarising.app.tibco.users.getByUsername}")
	private String getUserByUsernameUrl;

	@Value("${polarising.app.tibco.users.getByToken}")
	private String getUserByTokenUrl;

	@Value("${polarising.app.tibco.users.add}")
	private String addUserUrl;

	@Value("${polarising.app.tibco.users.delete}")
	private String deleteUserUrl;

	@Value("${polarising.app.tibco.users.update}")
	private String updateUserUrl;

	@Value("${polarising.app.tibco.users.updateResetPasswordCode}")
	private String updateResetPasswordCodeUrl;

	@PostMapping("/auth/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest)
			throws JsonProcessingException, IOException {

		String username;

		if (loginRequest.getUsername().contains(emailDomain)) {
			username = loginRequest.getUsername().substring(0, loginRequest.getUsername().indexOf("@"));
		} else {
			username = loginRequest.getUsername();
		}

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String jwt = jwtProvider.generateJwtToken(authentication);
			UserPrinciple userDetails = (UserPrinciple) authentication.getPrincipal();

			return ResponseEntity
					.ok(new JwtResponse(jwt, username, userDetails.getAuthorities(), userDetails.getName()));

		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AuthenticationException e) {
			throw new ApiRequestException(e.getLocalizedMessage());
		}
	}

	@PostMapping("/auth/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupForm signUpRequest)
			throws JsonProcessingException, IOException {

		User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getUsername(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getPersonalEmail(), emailDomain);

		HashSet<String> roles = (HashSet<String>) signUpRequest.getRole();

		for (Object value : roles.toArray()) {
			Role role = new Role();
			role.setName(xmlParserService.getRoleNameString(value.toString()));
			user.addRole(role);
		}

		String xmlResponse = null;
		String response = null;

		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://www.tibco.com/schemas/tibco/Schemas/User.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <user:InputData email=\""
					+ user.getEmail() + "\" firstName=\"" + user.getFirstName() + "\" id=\"?\" lastName=\""
					+ user.getLastName() + "\" password=\"" + user.getPassword() + "\" role=\""
					+ user.getRoles().stream().findFirst().get().getName() + "\" username=\"" + user.getUsername()
					+ "\" personalEmail=\"" + user.getPersonalEmail() + "\"/>\n" + "   </soapenv:Body>\n"
					+ "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/users/add", requestBody, addUserUrl);

			response = xmlParserService.getResponseMessage(xmlResponse, "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException("User already exists");
			}

			// Sending registration email
			/////////////////////////////////////////////////////////////////////////////////
			String emailSubject = "Polarising Employee Manager - Registration";

			File file = new File("src/main/resources/email/UserRegistration/Email.txt");
			String emailBody = FileUtils.readFileToString(file, "UTF-8");

			emailBody = emailBody.replace("$fullName", user.getFullName());
			emailBody = emailBody.replace("$username", user.getUsername());
			emailBody = emailBody.replace("$password", signUpRequest.getPassword());
			emailBody = emailBody.replace("$email", user.getEmail());

			Map<String, String> inlineImages = new HashMap<String, String>();
			inlineImages.put("company_logo", "src/main/resources/email/UserRegistration/images/company_logo.png");
			inlineImages.put("facebook@2x", "src/main/resources/email/UserRegistration/images/facebook@2x.png");
			inlineImages.put("linkedin@2x", "src/main/resources/email/UserRegistration/images/linkedin@2x.png");

			emailSender.SendEmail(user.getPersonalEmail(), emailSubject, emailBody, inlineImages);
			/////////////////////////////////////////////////////////////////////////////////

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage("Error while creating user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/users")
	public ResponseEntity<?> getUsers() throws IOException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body/>\n" + "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/users", requestBody, getUsersUrl);

			List<User> response = xmlParserService.getUsersFromXML(xmlResponse);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RestClientException e) {
			return new ResponseEntity<>(new ResponseMessage("Error while getting users: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UnrecognizedPropertyException e) {
			return new ResponseEntity<>(
					new ResponseMessage(xmlParserService.getResponseMessage(xmlResponse, "message")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUser(@PathVariable int id) throws IOException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:idon=\"http://www.tibco.com/schemas/tibco/Schemas/IdOnly.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <idon:InputData id=\"" + id + "\"/>\n"
					+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/users/id", requestBody, getUserUrl);

			User response = xmlParserService.getUsersFromXML(xmlResponse).get(0);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RestClientException e) {
			return new ResponseEntity<>(new ResponseMessage("Error while getting user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			return new ResponseEntity<>(
					new ResponseMessage(xmlParserService.getResponseMessage(xmlResponse, "message")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/users/username/{username}")
	public ResponseEntity<?> getUserByUsername(@PathVariable String username) throws IOException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://www.tibco.com/schemas/tibco/Schemas/UsernameOnly.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" + "      <user:InputData username=\""
					+ username + "\"/>\r\n" + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/users/username", requestBody, getUserByUsernameUrl);

			User response = xmlParserService.getUsersFromXML(xmlResponse).get(0);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RestClientException e) {
			return new ResponseEntity<>(new ResponseMessage("Error while getting user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			return new ResponseEntity<>(
					new ResponseMessage(xmlParserService.getResponseMessage(xmlResponse, "message")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/auth/token/{token}")
	public ResponseEntity<?> getUserByToken(@PathVariable String token) throws IOException {
		String xmlResponse = null;
		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:idon=\"http://www.tibco.com/schemas/tibco/Schemas/IdOnly.xsd\">\r\n"
					+ "   <soapenv:Header/>\r\n" + "   <soapenv:Body>\r\n" + "      <idon:InputData id=\"" + token
					+ "\"/>\r\n" + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/auth/token", requestBody, getUserByTokenUrl);

			User response = xmlParserService.getUsersFromXML(xmlResponse).get(0);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RestClientException e) {
			return new ResponseEntity<>(new ResponseMessage("Error while getting user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			return new ResponseEntity<>(
					new ResponseMessage(xmlParserService.getResponseMessage(xmlResponse, "message")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestBody DeleteUserData deleteUserData)
			throws IOException {

		User currentUser = null;
		try {
			currentUser = (User) getUserByUsername(deleteUserData.getUsername()).getBody();
		} catch (Exception e) {
			throw new ApiRequestException("Current user invalid!");
		}

		if (currentUser.getId() == id) {
			throw new ApiRequestException("Error: A user can't delete himself");
		}

		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:idon=\"http://www.tibco.com/schemas/tibco/Schemas/IdOnly.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <idon:InputData id=\"" + id + "\"/>\n"
					+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";

			String response = xmlParserService
					.getResponseMessage(http.post(url, "/users/delete", requestBody, deleteUserUrl), "message");

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage("Error while deleting user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody EditForm editForm) throws IOException {

		User user = (User) getUser(id).getBody();

		boolean resetedPassword = false;

		user.setFirstName(editForm.getFirstName());
		user.setLastName(editForm.getLastName());
		user.setUsername(editForm.getUsername());
		user.setEmail(editForm.getEmail());
		user.setPersonalEmail(editForm.getPersonalEmail());
		if (!editForm.getPassword().equals("resetFalse")) {
			user.setPassword(encoder.encode(editForm.getPassword()));
			resetedPassword = true;
		}

		HashSet<String> roles = (HashSet<String>) editForm.getRole();

		Role role = new Role();
		try {
			role.setName(xmlParserService.getRoleNameString(roles.toArray()[0].toString()));
		} catch (Exception e) {
			// System.out.println(e.getLocalizedMessage());
		}

		user.clearRoles();
		user.addRole(role);

		String xmlResponse = null;
		String response = null;

		try {
			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://www.tibco.com/schemas/tibco/Schemas/User.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <user:InputData email=\""
					+ user.getEmail() + "\" firstName=\"" + user.getFirstName() + "\" id=\"" + id + "\" lastName=\""
					+ user.getLastName() + "\" password=\"" + user.getPassword() + "\" role=\""
					+ user.getRoles().stream().findFirst().get().getName() + "\" username=\"" + user.getUsername()
					+ "\" personalEmail=\"" + user.getPersonalEmail() + "\" passwordRecoveryCode=\"" + editForm.getPasswordRecoveryCode() + "\"/>\n" + "   </soapenv:Body>\n"
					+ "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/users/update", requestBody, updateUserUrl);

			response = xmlParserService.getResponseMessage(xmlResponse, "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException(response);
			}

			// Sending password reset email email
			/////////////////////////////////////////////////////////////////////////////////
			if (resetedPassword) {
				sendPasswordResetEmail(user, editForm.getPassword());
			}
			/////////////////////////////////////////////////////////////////////////////////

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (

		ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage("Error while updating user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/auth/update-password")
	public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordForm changePasswordForm) throws IOException {

		User user = (User) getUserByUsername(changePasswordForm.getUsername()).getBody();

		String xmlResponse = null;
		String response = null;

		if (encoder.matches(changePasswordForm.getCurrentPassword(), user.getPassword())) {
			user.setPassword(encoder.encode(changePasswordForm.getNewPassword()));

			try {
				String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://www.tibco.com/schemas/tibco/Schemas/User.xsd\">\n"
						+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <user:InputData email=\""
						+ user.getEmail() + "\" firstName=\"" + user.getFirstName() + "\" id=\"" + user.getId() + "\" lastName=\""
						+ user.getLastName() + "\" password=\"" + user.getPassword() + "\" role=\""
						+ user.getRoles().stream().findFirst().get().getName() + "\" username=\"" + user.getUsername()
						+ "\" personalEmail=\"" + user.getPersonalEmail() + " passwordRecoveryCode=\"" + null + "\"/>\n" + "   </soapenv:Body>\n"
						+ "</soapenv:Envelope>";

				xmlResponse = http.post(url, "/users/update", requestBody, updateUserUrl);

				response = xmlParserService.getResponseMessage(xmlResponse, "message");

				if (response.contains("Error:")) {
					throw new ApiRequestException(response);
				}

				return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
			} catch (ApiRequestException e) {
				return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				return new ResponseEntity<>(
						new ResponseMessage("Error while updating user: " + e.getLocalizedMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			response = "Error: Invalid current password";

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping("/auth/recover-password")
	public ResponseEntity<?> recoverPassword(@RequestBody ForgotPasswordForm forgotPasswordForm)
			throws IOException, MessagingException {
		String[] splitEmail = forgotPasswordForm.getEmail().split("@");
		String username = splitEmail[0];
		String domain = "@" + splitEmail[1];
		User user = null;

		if (domain.equals(this.emailDomain)) {
			try {
				user = (User) getUserByUsername(username).getBody();
			} catch (Exception e) {
				return new ResponseEntity<>(new ResponseMessage("Error: Invalid email"),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			// Update password reset token in DB
			String xmlResponse = null;
			String response = null;

			try {
				// Update password reset token on DB

				String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sch=\"http://www.tibco.com/schemas/tibco/Schemas/Schema.xsd3\">\r\n" + 
						"   <soapenv:Header/>\r\n" + 
						"   <soapenv:Body>\r\n" + 
						"      <sch:InputData code=\"" + forgotPasswordForm.getCode() + "\" username=\"" + username + "\"/>\r\n" + 
						"   </soapenv:Body>\r\n" + 
						"</soapenv:Envelope>";

				xmlResponse = http.post(url, "/password-reset", requestBody, updateResetPasswordCodeUrl);

				response = xmlParserService.getResponseMessage(xmlResponse, "message");
				// ...

				// Sending password recover email
				/////////////////////////////////////////////////////////////////////////////////
				String emailSubject = "Polarising Employee Manager - Recover password";

				File file = new File("src/main/resources/email/RecoverPassword/Email.txt");
				String emailBody = FileUtils.readFileToString(file, "UTF-8");

				emailBody = emailBody.replace("$fullName", user.getFullName());
				emailBody = emailBody.replace("$code", forgotPasswordForm.getCode());
				emailBody = emailBody.replace("$changePasswordUrl", "http://localhost:4200/password-recovery/code");

				Map<String, String> inlineImages = new HashMap<String, String>();
				inlineImages.put("company_logo", "src/main/resources/email/RecoverPassword/images/company_logo.png");
				inlineImages.put("facebook@2x", "src/main/resources/email/RecoverPassword/images/facebook@2x.png");
				inlineImages.put("linkedin@2x", "src/main/resources/email/RecoverPassword/images/linkedin@2x.png");

				emailSender.SendEmail(user.getEmail(), emailSubject, emailBody, inlineImages);
				/////////////////////////////////////////////////////////////////////////////////

				response = xmlParserService.getResponseMessage(xmlResponse, "message");

				if (response.contains("Error:")) {
					throw new ApiRequestException(response);
				}

				return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
			} catch (ApiRequestException e) {
				return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				return new ResponseEntity<>(
						new ResponseMessage("Error while updating user: " + e.getLocalizedMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>(new ResponseMessage("Error: Invalid email domain"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/auth/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordForm resetPasswordForm)
			throws IOException, MessagingException {

		User user = null;

		try {
			user = (User) getUserByUsername(resetPasswordForm.getUsername()).getBody();
		} catch (Exception e) {
			throw new ApiRequestException("Error: Invalid username");
		}
		
		if (!user.getPasswordRecoveryCode().equals(resetPasswordForm.getCode()) || user.getPasswordRecoveryCode().equals("0")) {
			throw new ApiRequestException("Error: Invalid code");
		}

		String xmlResponse = null;
		String response = null;

		try {
			// Reset password
			user.setPassword(encoder.encode(resetPasswordForm.getNewPassword()));

			String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:user=\"http://www.tibco.com/schemas/tibco/Schemas/User.xsd\">\n"
					+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <user:InputData email=\""
					+ user.getEmail() + "\" firstName=\"" + user.getFirstName() + "\" id=\"" + user.getId() + "\" lastName=\""
					+ user.getLastName() + "\" password=\"" + user.getPassword() + "\" role=\""
					+ user.getRoles().stream().findFirst().get().getName() + "\" username=\"" + user.getUsername()
					+ "\" personalEmail=\"" + user.getPersonalEmail() + "\" passwordRecoveryCode=\"0\"/>\n" + "   </soapenv:Body>\n"
					+ "</soapenv:Envelope>";

			xmlResponse = http.post(url, "/users/update", requestBody, updateUserUrl);

			response = xmlParserService.getResponseMessage(xmlResponse, "message");

			if (response.contains("Error:")) {
				throw new ApiRequestException(response);
			}

			// Sending password recover email
			/////////////////////////////////////////////////////////////////////////////////
			sendPasswordResetEmail(user, resetPasswordForm.getNewPassword());
			/////////////////////////////////////////////////////////////////////////////////

			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.OK);
		} catch (ApiRequestException e) {
			return new ResponseEntity<>(new ResponseMessage(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>(new ResponseMessage("Error while updating user: " + e.getLocalizedMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendPasswordResetEmail(User user, String newPassword) throws IOException, MessagingException {
		String emailSubject = "Polarising Employee Manager - Password Reset";

		File file = new File("src/main/resources/email/ResetPassword/Email.txt");
		String emailBody = FileUtils.readFileToString(file, "UTF-8");

		emailBody = emailBody.replace("$fullName", user.getFullName());
		emailBody = emailBody.replace("$username", user.getUsername());
		emailBody = emailBody.replace("$password", newPassword);

		Map<String, String> inlineImages = new HashMap<String, String>();
		inlineImages.put("company_logo", "src/main/resources/email/ResetPassword/images/company_logo.png");
		inlineImages.put("facebook@2x", "src/main/resources/email/ResetPassword/images/facebook@2x.png");
		inlineImages.put("linkedin@2x", "src/main/resources/email/ResetPassword/images/linkedin@2x.png");

		emailSender.SendEmail(user.getEmail(), emailSubject, emailBody, inlineImages);
	}
}
