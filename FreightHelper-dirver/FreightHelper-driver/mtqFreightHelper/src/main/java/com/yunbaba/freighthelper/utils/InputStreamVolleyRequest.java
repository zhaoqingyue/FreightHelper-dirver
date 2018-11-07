package com.yunbaba.freighthelper.utils;

import java.util.Map;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public class InputStreamVolleyRequest extends Request<byte[]> {
	private final Response.Listener<byte[]> mListener;
	private Map<String, String> mParams;
	// create a static map for directly accessing headers
	public Map<String, String> responseHeaders;

	public InputStreamVolleyRequest(int post, String mUrl, Response.Listener<byte[]> listener,
			Response.ErrorListener errorListener) {
		// TODO Auto-generated constructor stub

		super(post, mUrl, errorListener);
		// this request would never use cache.
		setShouldCache(false);
		mListener = listener;
		//mParams = params;
	}

	

//	public InputStreamVolleyRequest(int get, String url, Listener<byte[]> listener) {
//		// TODO Auto-generated constructor stub
//		super(get, url, listener);
//		
//		super();
//		// this request would never use cache.
//		setShouldCache(false);
//		mListener = listener;
//	}



	@Override
	protected void deliverResponse(byte[] response) {
		//MLog.e("response", ""+response.length);
		mListener.onResponse(response);
	}

	@Override
	protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
		
		//MLog.e("response", ""+response.statusCode);

		// Initialise local responseHeaders map with response headers received
		responseHeaders = response.headers;

		// Pass the response data here
		return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
	}
}
