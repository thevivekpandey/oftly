package com.oftly.oftly;

public class CountryInfo {
	String countryName;
	String countryCode;
	String countryPhoneCode;
	
	public CountryInfo() {
	}
	public CountryInfo(String countryName, String countryPhoneCode) {
		this.countryName = countryName;
		this.countryPhoneCode = countryPhoneCode;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public void setCountryPhoneCode(String countryPhoneCode) {
		this.countryPhoneCode = countryPhoneCode;
	}	
	public String getCountryName() {
		return this.countryName;
	}
	public String getCountryCode() {
		return this.countryCode;
	}
	public String getCountryPhoneCode() {
		return this.countryPhoneCode;
	}
}
