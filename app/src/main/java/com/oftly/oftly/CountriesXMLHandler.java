package com.oftly.oftly;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CountriesXMLHandler extends DefaultHandler {
	ArrayList<CountryInfo> countryInfo;
	CountryInfo oneCountryInfo;
	boolean currentElement = false;
	String currentValue = "";
	
	public ArrayList<CountryInfo> getCountryList() {
		return countryInfo;
	}
	
	@Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
        if (elementName.equals("countries")) {
        	countryInfo = new ArrayList<CountryInfo>();
        } else if (elementName.equals("country")) {
        	oneCountryInfo = new CountryInfo();
        	oneCountryInfo.setCountryName(attributes.getValue("name"));
        	oneCountryInfo.setCountryPhoneCode(attributes.getValue("phoneCode"));
        	oneCountryInfo.setCountryCode(attributes.getValue("code"));        
        }
    }

	@Override
    public void endElement(String s, String s1, String element) throws SAXException {
		if (element.equals("country")) {
			countryInfo.add(oneCountryInfo);
		}
    }

	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
    }

}
