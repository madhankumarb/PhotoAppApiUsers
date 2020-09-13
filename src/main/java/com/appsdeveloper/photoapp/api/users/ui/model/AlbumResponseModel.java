package com.appsdeveloper.photoapp.api.users.ui.model;

public class AlbumResponseModel {
    private String albumId;
    private String userId; 
    private String name;
    private String description;
    private boolean serviceDown;
    private boolean anyException;
    
	public String getAlbumId() {
		return albumId;
	}
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isServiceDown() {
		return serviceDown;
	}
	public void setServiceDown(boolean serviceDown) {
		this.serviceDown = serviceDown;
	}
	public boolean isAnyException() {
		return anyException;
	}
	public void setAnyException(boolean anyException) {
		this.anyException = anyException;
	}
    
}
