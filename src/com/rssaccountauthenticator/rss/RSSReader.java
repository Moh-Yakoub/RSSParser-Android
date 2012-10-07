package com.rssaccountauthenticator.rss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class RSSReader {

	public List<channel> parse(InputStream in) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		return readChannels(parser);

	}

	private List<channel> readChannels(XmlPullParser parser) throws Exception {
		ArrayList<channel> channels = new ArrayList<channel>();

		parser.require(XmlPullParser.START_TAG, null, "rss");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("channel")) {
				channels.add(readChannel(parser));

			} else {
				skip(parser);
			}
		}
		return channels;
	}

	private channel readChannel(XmlPullParser parser) throws Exception {
		channel c = new channel();
		ArrayList<item> items = new ArrayList<item>();
		parser.require(XmlPullParser.START_TAG, null, "channel");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				parser.require(XmlPullParser.START_TAG, null, "title");
				c.title = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "title");

			} else if (name.equals("link")) {
				parser.require(XmlPullParser.START_TAG, null, "link");
				c.description = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "link");

			} else if (name.equals("description")) {
				parser.require(XmlPullParser.START_TAG, null, "description");
				c.description = readText(parser);
				parser.require(XmlPullParser.END_TAG, null, "description");
			} else if (name.equals("item")) {
				items.add(readItem(parser));
			} else if (name.equals("image")) {
				c.image = readchannelimage(parser);
			} else {
				skip(parser);
			}
			c.items = items;

		}
		return c;
	}

	private item readItem(XmlPullParser parser) throws Exception {
		item i = new item();
		media m = new media();
		parser.require(XmlPullParser.START_TAG, null, "item");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				i.title = validateString("title", parser);
			} else if (name.equals("link")) {
				i.link = validateString("link", parser);
			} else if (name.equals("guid")) {
				i.guid = validateString("guid", parser);
			} else if (name.equals("description")) {
				i.title = validateString("description", parser);
			} else if (name.equals("pubDate")) {
				i.title = validateString("pubDate", parser);
			} else if (name.startsWith("media:")) {
				updatemedia(parser, m, name);
			} else {
				skip(parser);
			}
		}
		i.item_media = m;
		return i;

	}

	private void skip(XmlPullParser parser) throws Exception {
		int depth = 1;
		while (depth != 0) {
			int c = parser.next();
			if (c == XmlPullParser.START_TAG)
				depth++;
			else if (c == XmlPullParser.END_TAG)
				depth--;
		}
	}

	private String readText(XmlPullParser parser) throws Exception {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private String validateString(String s, XmlPullParser parser)
			throws Exception {
		parser.require(XmlPullParser.START_TAG, null, s);
		String result = readText(parser);
		parser.require(XmlPullParser.END_TAG, null, s);
		return result;

	}

	private channelimage readchannelimage(XmlPullParser parser)
			throws Exception {
		channelimage image = new channelimage();
		parser.require(XmlPullParser.START_TAG, null, "image");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				image.title = validateString("title", parser);
			} else if (name.equals("url")) {
				image.url = validateString("url", parser);
			} else if (name.equals("link")) {
				image.link = validateString("link", parser);
			} else {
				skip(parser);
			}
		}
		return image;

	}

	private void updatemedia(XmlPullParser parser, media med, String s)
			throws Exception {
		parser.require(XmlPullParser.START_TAG, null, s);
		String tag = s.substring(s.indexOf(":") + 1, s.length());
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (tag.equals("description")) {
				med.description = validateString("media:description", parser);
			} else if (tag.equals("thumbnail")) {
				med.thumbnail_url = parser.getAttributeValue(null, "url");
				med.thumbnail_height = parser.getAttributeValue(null, "height");
				med.thumbnail_width = parser.getAttributeValue(null, "width");
			} else if (tag.equals("content")) {
				med.content_type = parser.getAttributeValue(null, "type");
				med.content_url = parser.getAttributeValue(null, "url");
			} else {
				skip(parser);
			}
		}

	}

}
