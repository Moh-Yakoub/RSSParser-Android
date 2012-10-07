package com.rssaccountauthenticator.rss;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RSSParser extends AsyncTask<String, String, List<channel>> {
	private Context c;

	public RSSParser(Context c) {
		// TODO Auto-generated constructor stub
		this.c = c;
	}

	@Override
	protected List<channel> doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		try {
			HttpGet get = new HttpGet(arg0[0]);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			RSSReader reader = new RSSReader();
			List<channel> channels = reader.parse(is);
			return channels;

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("inside parser task", e.toString());
			return null;
		}

	}

	@Override
	protected void onPostExecute(List<channel> result) {
		// TODO Auto-generated method stub
		if (result == null) {
			Log.e("OnPostExecute", "Error");
			Toast.makeText(c, "Error", Toast.LENGTH_LONG).show();
		} else {
			channel c = result.get(0);
			List<item> items = c.items;
			for (int i = 0; i < items.size(); i++) {
				item it = items.get(i);
				media m = it.item_media;
				Log.v("onPostExecute", it.guid + "," + m.content_type + ","
						+ m.thumbnail_height + "," + m.thumbnail_url);
			}
			Toast.makeText(this.c, "Done", Toast.LENGTH_LONG).show();

		}

	}
}
