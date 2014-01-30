package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendRequest {
	
	public Long idRel;
    public String noiz;
    public String who;
    public Long userId;
    public String userIden;
    public String userImg;
    
	public FriendRequest(Long idRel, String noiz, String who, Long userId,
			String userIden, String userImg) {
		super();
		this.idRel = idRel;
		this.noiz = noiz;
		this.who = who;
		this.userId = userId;
		this.userIden = userIden;
		this.userImg = userImg;
	}
    
	public FriendRequest(JSONObject json) throws JSONException {
		super();
		this.idRel = json.getLong("idRel");
		this.noiz = json.getString("noiz");
		this.who = json.getString("who");
		this.userId = json.getLong("userId");
		this.userIden = json.getString("userIden");
		this.userImg = json.getString("userImg");
	}

}
