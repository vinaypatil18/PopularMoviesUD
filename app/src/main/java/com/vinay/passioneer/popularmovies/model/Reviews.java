package com.vinay.passioneer.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Reviews {
	private String id;
	private String author;
	private String content;
	private String url;


	public String getId() {
	return id;
	}


	public void setId(String id) {
	this.id = id;
	}


	public String getAuthor() {
	return author;
	}


	public void setAuthor(String author) {
	this.author = author;
	}


	public String getContent() {
	return content;
	}


	public void setContent(String content) {
	this.content = content;
	}

	
	public String getUrl() {
	return url;
	}

	public void setUrl(String url) {
	this.url = url;
	}
	
	public static final class ReviewsResponse {
		@SerializedName("results")
		private List<Reviews> reviews = new ArrayList<>();

		public List<Reviews> getReviews() {
			return reviews;
		}
	}
}
