package com.polarising.projetoescola.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.polarising.projetoescola.model.Employee;
import com.polarising.projetoescola.model.Role;
import com.polarising.projetoescola.model.RoleName;
import com.polarising.projetoescola.model.User;

@Service
public class XmlParserService {
	
	public String getResponseMessage(String xmlString, String value) throws JsonProcessingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		XmlMapper xmlMapper = new XmlMapper();

		return objectMapper.writeValueAsString((xmlMapper.readTree(xmlString).findValue(value))).replace("\"", "");
	}

	public List<Employee> getEmployeesFromXML(String xmlString) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		JSONObject json = XML.toJSONObject(xmlString);
		String jsonString = json.toString();
		String jsonArrayString = "";
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try {
			jsonArrayString = jsonString.substring(jsonString.indexOf("["), jsonString.indexOf("]") + 1);

		} catch (StringIndexOutOfBoundsException e) {
			try {
				jsonArrayString = "["
						+ jsonString.substring(jsonString.lastIndexOf("{"), jsonString.lastIndexOf("}}}}}") + 1) + "]";
			} catch (StringIndexOutOfBoundsException ex) {
				jsonArrayString = "[" + jsonString.substring(jsonString.lastIndexOf("{"),
						jsonString.lastIndexOf("},\"ns0:OutputData\":\"\"}") + 1) + "]";
			}
		}
		JSONArray array = new JSONArray(jsonArrayString);
		try {
			for (int i = 0; i < array.length(); i++) {
				employees.add(objectMapper.readValue(array.get(i).toString(), Employee.class));
			}
		} catch(Exception e) {
			//System.out.println(e.getLocalizedMessage());
		}
		

		return employees;
	}
	
	public List<User> getUsersFromXML(String xmlString) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		JSONObject json = XML.toJSONObject(xmlString);
		String jsonString = json.toString();
		String jsonArrayString = "";
		ArrayList<User> employees = new ArrayList<User>();
		try {
			jsonArrayString = jsonString.substring(jsonString.indexOf("["), jsonString.indexOf("]") + 1);

		} catch (StringIndexOutOfBoundsException e) {
			try {
				jsonArrayString = "["
						+ jsonString.substring(jsonString.lastIndexOf("{"), jsonString.lastIndexOf("}}}}}") + 1) + "]";
			} catch (StringIndexOutOfBoundsException ex) {
				jsonArrayString = "[" + jsonString.substring(jsonString.lastIndexOf("{"),
						jsonString.lastIndexOf("},\"ns0:OutputData\":\"\"}") + 1) + "]";
			}
		}
		JSONArray array = new JSONArray(jsonArrayString);
		try {
		for (int i = 0; i < array.length(); i++) {
			String userString = array.get(i).toString();
			String[] arrayTest = userString.split("\"role\":");
			String[] arrayTest1 = arrayTest[1].split("id");
			userString = arrayTest[0] + "\"id" + arrayTest1[1];
			String roleString = arrayTest1[0].substring(arrayTest1[0].indexOf("ROLE"), arrayTest1[0].indexOf("\","));
			User user = objectMapper.readValue(userString, User.class);
			HashSet<Role> roles = new HashSet<>();
			Role role = new Role();
			role.setName(getRoleNameString(roleString));
			roles.add(role);
			user.setRoles(roles);
			employees.add(user);
		}

		return employees;
		} catch(Exception e) {
			//System.out.println(e.getLocalizedMessage());
			throw e;
		}
	}
	
	public RoleName getRoleNameString(String role) {
		switch (role) {
		case "ROLE_ADMIN":
			return RoleName.ROLE_ADMIN;
		default:
			return RoleName.ROLE_USER;
		}
	}
}
