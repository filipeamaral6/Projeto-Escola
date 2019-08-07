package com.polarising.projetoescola.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateFormatterService {
	private String angularDateFormat = "yyyy/MM/dd";
	private String tibcoDateFormat = "yyyy-MM-ddXXX";
	private String receivedDateFormat = "yyyy-MM-dd";
	private SimpleDateFormat angularDateFormatter = new SimpleDateFormat(angularDateFormat);
	private SimpleDateFormat tibcoDateFormatter = new SimpleDateFormat(tibcoDateFormat);
	private SimpleDateFormat receivedDateFormatter = new SimpleDateFormat(receivedDateFormat);
	
	public String convertToTibcoDateFormat(String date) throws ParseException {
		Date parsedDate = receivedDateFormatter.parse(date);
		String convertedDateString = tibcoDateFormatter.format(parsedDate);

		return convertedDateString;
	}
	
	public String convertToAngularDateFormat(String date) throws ParseException {
		Date parsedDate = angularDateFormatter.parse(date);
		String convertedDateString = angularDateFormatter.format(parsedDate);
		
		return convertedDateString;
	}
	
	public Date parseTibcoDate(String dateString) throws ParseException {
		return tibcoDateFormatter.parse(dateString);
	}
	
	public Date parseAngularDate(String dateString) throws ParseException {
		return angularDateFormatter.parse(dateString);
	}
}
