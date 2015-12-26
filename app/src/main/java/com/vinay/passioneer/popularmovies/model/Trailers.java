package com.vinay.passioneer.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Trailers {
	
	private String id;
	@SerializedName("iso_639_1")
	private String iso6391;
	private String key;
	private String name;
	private String site;
	private int size;
	private String type;


	public String getId() {
	return id;
	}


	public void setId(String id) {
	this.id = id;
	}


	public String getIso6391() {
	return iso6391;
	}


	public void setIso6391(String iso6391) {
	this.iso6391 = iso6391;
	}


	public String getKey() {
	return key;
	}


	public void setKey(String key) {
	this.key = key;
	}


	public String getName() {
	return name;
	}


	public void setName(String name) {
	this.name = name;
	}


	public String getSite() {
	return site;
	}


	public void setSite(String site) {
	this.site = site;
	}


	public int getSize() {
	return size;
	}


	public void setSize(int size) {
	this.size = size;
	}

	
	public String getType() {
	return type;
	}

	
	public void setType(String type) {
	this.type = type;
	}
	
	public static final class TrailersResponse {
		@SerializedName("results")
		private List<Trailers> trailers = new ArrayList<>();

		public List<Trailers> getTrailers() {
			return trailers;
		}
	}

}
