/**
 * 
 */
package com.samsung.apps.restaurantscan.model;

/**
 * @author Avdhoot Saple
 * 
 * Created on Apr 25, 2015
 * 
 * Model class to hold row data for restaurants
 */
public class Overview {
	
	private String name;
	private String vicinity;
	private String placeId;
	private String iconUrl;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVicinity() {
		return vicinity;
	}
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	

}
