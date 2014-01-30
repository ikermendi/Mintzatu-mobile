package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

public class People {

	public Long id;
	public String fullname;
	public String username;
	public String town;
	public String desc;
	public String facebook;
	public String twitter;
	public Boolean friends;
	public Boolean friendshipRequester;
	public Integer friendshipState;
	public String userImg;

	public People(JSONObject json) throws JSONException {
		super();
		this.id = json.getLong("id");
		if (checkJsonField(json, "fullname")) {
			this.fullname = json.getString("fullname");
		} else {
			this.fullname = "";
		}
		if (checkJsonField(json, "username")) {
			this.username = json.getString("username");
		} else {
			this.username = "";
		}
		if (checkJsonField(json, "town")) {
			this.town = json.getString("town");
		} else {
			this.town = "";
		}
		if (checkJsonField(json,"desc")) {
			this.desc = json.getString("desc");
		} else {
			this.desc = "";
		}
		if (checkJsonField(json,"facebook")) {
			this.facebook = json.getString("facebook");
		} else {
			this.facebook = "";
		}
		if (checkJsonField(json,"twitter")) {
			this.twitter = json.getString("twitter");
		} else {
			this.twitter = "";
		}
		if (checkJsonField(json,"friends")) {
			this.friends = json.getBoolean("friends");
		} else {
			this.friends = false;;
		}
		if (checkJsonField(json, "friendshipState")) {
			this.friendshipState = json.getInt("friendshipState");
		} else {
			this.friendshipState = 0;
		}
		if (checkJsonField(json, "friendshipRequester")) {
			this.friendshipRequester = json.getBoolean("friendshipRequester");
		} else {
			this.friendshipRequester = false;
		}
		if (checkJsonField(json,"userImage")) {
			this.userImg = json.getString("userImage");
		} else {
			this.userImg = "";
		}

	}

	public People(Long id, String fullname, String username, String town, String desc,
			String facebook, String twitter, Boolean friends, Boolean friendshipRequester, Integer friendshipState,
			String userImg) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.username = username;
		this.town = town;
		this.desc = desc;
		this.facebook = facebook;
		this.twitter = twitter;
		this.friends = friends;
		this.friendshipRequester = friendshipRequester;
		this.friendshipState = friendshipState;
		this.userImg = userImg;
	}

	public Boolean checkJsonField(JSONObject json, String name) {
		if (json.has(name) && !json.isNull(name)) {
			return true;
		} else {
			return false;
		}
	}

}
