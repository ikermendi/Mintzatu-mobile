package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendProfile implements Parcelable {

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
    public Boolean friends;
    public Boolean friendshipRequester;
    public Integer friendshipState;
    public String img;
    public String lastPlaceName;
    
	public FriendProfile(JSONObject json) throws JSONException {
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
		if (profile.has("friends")) {
			this.friends = profile.getBoolean("friends");
		}
		if (profile.has("friendshipRequester")) {
			this.friendshipRequester = profile.getBoolean("friendshipRequester");
		}
		if (profile.has("friendshipState")) {
			this.friendshipState = profile.getInt("friendshipState");
		}
		this.img = profile.getString("img");
		if (profile.has("lastPlaceName")) {
			this.lastPlaceName = profile.getString("lastPlaceName");
		} else {
			this.lastPlaceName = "";
		}
	}
	
	public FriendProfile(Integer id) {
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
    	out.writeByte((byte) (friends ? 1 : 0));
    	out.writeByte((byte) (friendshipRequester ? 1 : 0));
    	out.writeInt(friendshipState);
    	out.writeString(img);
    	out.writeString(lastPlaceName);
    }

    public static final Parcelable.Creator<FriendProfile> CREATOR
            = new Parcelable.Creator<FriendProfile>() {
        public FriendProfile createFromParcel(Parcel in) {
            return new FriendProfile(in);
        }

        public FriendProfile[] newArray(int size) {
            return new FriendProfile[size];
        }
    };
    
    private FriendProfile(Parcel in) {
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
		friends = in.readByte() == 1;
		friendshipRequester = in.readByte() == 1;
		friendshipState = in.readInt();
		img = in.readString();
		lastPlaceName = in.readString();
    }
	
	
}
