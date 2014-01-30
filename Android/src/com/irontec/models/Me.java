package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Me implements Parcelable {

	public Integer id;
	public String fullname;
    public String username;
    public String town;
    public String desc;
    public String facebook;
    public String twitter;
    public Integer badges;
    public Integer mayorships;
    public Boolean owner;
    public Integer friendRequests;
    public Integer checkins;
    public String img;
    public String lastPlaceName;
    
	public Me(JSONObject json) throws JSONException {
		super();
		JSONObject profile = json.getJSONObject("profile");
		this.fullname = profile.getString("fullname");
		this.username = profile.getString("username");
		this.town = profile.getString("town");
		this.desc = profile.getString("desc");
		this.facebook = profile.getString("facebook");
		this.twitter = profile.getString("twitter");
		this.badges = profile.getInt("badgets");
		this.mayorships = profile.getInt("mayorships");
		this.owner = profile.getBoolean("owner");
		this.friendRequests = profile.getInt("friendRequests");
		this.checkins = profile.getInt("checkins");
		this.img = profile.getString("img");
		if (profile.has("lastPlaceName")) {
			this.lastPlaceName = profile.getString("lastPlaceName");
		} else {
			this.lastPlaceName = "";
		}
		
	}
	
	public Me(Integer id) {
		this.id = id;
	}

	public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	out.writeInt(id);
        out.writeString(fullname);
        out.writeString(username);
        out.writeString(town);
        out.writeString(desc);
        out.writeString("facebook");
        out.writeString("twitter");
    	out.writeInt(badges);
    	out.writeInt(mayorships);
    	out.writeByte((byte) (owner ? 1 : 0));
    	out.writeInt(friendRequests);
    	out.writeInt(checkins);
    	out.writeString(img);
    	out.writeString(lastPlaceName);
    }

    public static final Parcelable.Creator<Me> CREATOR
            = new Parcelable.Creator<Me>() {
        public Me createFromParcel(Parcel in) {
            return new Me(in);
        }

        public Me[] newArray(int size) {
            return new Me[size];
        }
    };
    
    private Me(Parcel in) {
    	id = in.readInt();
        fullname = in.readString();
		username = in.readString();
		town = in.readString();
		desc = in.readString();
		facebook = in.readString();
		twitter = in.readString();
		badges = in.readInt();
		mayorships = in.readInt();
		owner = in.readByte() == 1;
		friendRequests = in.readInt();
		checkins = in.readInt();
		img = in.readString();
		lastPlaceName = in.readString();
    }
	
	
}
