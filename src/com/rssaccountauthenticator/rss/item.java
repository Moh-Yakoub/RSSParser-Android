package com.rssaccountauthenticator.rss;


import java.util.Date;
public class item {
	String title;
	String link;
	String description;
	String author;
	String category;
	String comments;
	String enclosure;
	String guid;
	String pubDate;
	String source;
	media item_media;
	
	public long getDate(){
		if(pubDate!=null)return new Date().parse(pubDate);
		else return -1;
	}

}
