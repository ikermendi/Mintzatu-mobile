package com.irontec.api;

import org.json.JSONObject;

public abstract class HttpResponseHandler {
	public abstract void onSuccess(JSONObject response);
	public abstract void onFailure();
}
