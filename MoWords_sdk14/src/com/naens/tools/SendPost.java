package com.naens.tools;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public 	class SendPost {

	private HttpClient httpClient;
	private ProgressDialog progressDialog;
	private List<NameValuePair> nameValuePairs;
	private boolean calceled = false;
	private Activity activity;
	private OnResponseListener onResponseListener;
	private String url;
	private InnerTask innerTask;
	private boolean compressed = false;
	private String content = null;

	private SendPost(Activity activity, String url) {
		this.activity = activity;
		this.url = url;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog = new ProgressDialog(SendPost.this.activity);
			}
		});
		httpClient = new DefaultHttpClient();
		innerTask = new InnerTask ();
	}

	public SendPost(Activity activity, String url, List<NameValuePair> nameValuePairs) {
		this (activity, url);
		this.nameValuePairs = nameValuePairs;
	}

	public SendPost(Activity activity, String url, String string, boolean compressed) {
		this (activity, url);
		this.content = string;
		this.compressed = compressed;
	}

	public void execute () {
		innerTask.execute(url);
	}

	public interface OnResponseListener {
		public void onResponseReceived (HttpResponse response);
	}

	public void setOnResponseListener(OnResponseListener onResponseListener) {
		this.onResponseListener = onResponseListener;
	}

	private class InnerTask extends AsyncTask<String, Void, Void> {
		protected void onPreExecute() {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.setMessage("sending..");
					progressDialog.show();
				}
			});
		}

		protected Void doInBackground(String... urls) {
			try {
				HttpPost httpPost = new HttpPost(urls[0]);
				if (content != null) {
					if (compressed) {
						byte [] data = content.getBytes("UTF-8");
						httpPost.setHeader("Content-Encoding", "gzip");
						httpPost.setEntity(AndroidHttpClient.getCompressedEntity(data, activity.getContentResolver()));
					} else {
						StringEntity se = new StringEntity(content);
						se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
						httpPost.setEntity(se);
					}
				} else {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
				HttpResponse response = httpClient.execute(httpPost);
				if (onResponseListener != null) {
					onResponseListener.onResponseReceived(response);
				}
			} catch (IOException e) {
				Log.i("TAG", "error!");
				calceled = cancel(true);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
					}
				});
				Log.e("TAG", e.getMessage());
			}

			return null;
		}

		protected void onPostExecute(Void unused) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
					if (calceled) {
						Toast.makeText(activity, "Wrong Url", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(activity, "sent.", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

}
