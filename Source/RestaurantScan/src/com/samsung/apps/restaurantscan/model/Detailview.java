/**
 * 
 */
package com.samsung.apps.restaurantscan.model;

/**
 * @author Avdhoot Saple 
 * 
 * Created on Apr 25, 2015
 * 
 * model class to hold the details view
 */
public class Detailview {

	private String address;
	private String phone;
	private String openNow;
	private String priceLevel;
	private String types;
	private String photoReference;

	

	public String getOpenNow() {
		return openNow;
	}

	public void setOpenNow(String openNow) {
		this.openNow = openNow;
	}

	public String getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(String priceLevel) {
		this.priceLevel = priceLevel;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhotoReference() {
		return photoReference;
	}

	public void setPhotoReference(String photoReference) {
		this.photoReference = photoReference;
	}
	
	

}
