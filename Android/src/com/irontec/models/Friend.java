package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable{

	public Long id;
    public String fullname;
    public String username;
    public String town;
    public String desc;
    public String facebook;
    public String twitter;
    public Integer badgets;
    public Integer mayorships;
    public Boolean owner;
    public Boolean friendshipRequester;
    public String friendshipState;
    public String userImage;
    
    public Friend(JSONObject json) throws JSONException {
		super();
		this.id = json.getLong("id");
		this.fullname = json.getString("fullname");
		this.username = json.getString("username");
		this.town = json.getString("town");
		this.desc = json.getString("desc");
		this.facebook = json.getString("facebook");
		this.twitter = json.getString("twitter");
		this.badgets = json.getInt("badgets");
		this.mayorships = json.getInt("mayorships");
		this.owner = json.getBoolean("owner");
		this.friendshipRequester = json.getBoolean("friendshipRequester");
		this.friendshipState = json.getString("friendshipState");
		if (json.isNull("userImage")) {
			this.userImage = "";
		} else {
			this.userImage = json.getString("userImage");
		}
	}
    
    public Friend() {
    	this.id = 0l;
		this.fullname = "";
		this.username = "";
		this.town = "";
		this.desc = "";
		this.facebook = "";
		this.twitter = "";
		this.badgets = 0;
		this.mayorships = 0;
		this.owner = false;
		this.friendshipRequester = false;
		this.friendshipState = "";
		this.userImage = "";
    }
    
	public Friend(Long id, String fullname, String username, String town,
			String desc, String facebook, String twitter, Integer badgets,
			Integer mayorships, Boolean owner, Boolean friendshipRequester,
			String friendshipState, String userImage) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.username = username;
		this.town = town;
		this.desc = desc;
		this.facebook = facebook;
		this.twitter = twitter;
		this.badgets = badgets;
		this.mayorships = mayorships;
		this.owner = owner;
		this.friendshipRequester = friendshipRequester;
		this.friendshipState = friendshipState;
		this.userImage = userImage;
	}

	public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	out.writeLong(id);
        out.writeString(fullname);
        out.writeString(username);
        out.writeString(town);
        out.writeString(desc);
        out.writeString("facebook");
        out.writeString("twitter");
    	out.writeInt(badgets);
    	out.writeInt(mayorships);
    	out.writeByte((byte) (owner ? 1 : 0));
    	out.writeByte((byte) (friendshipRequester ? 1 : 0));
    	out.writeString("friendshipState");
    	out.writeString("userImage");
    }

    public static final Parcelable.Creator<Friend> CREATOR
            = new Parcelable.Creator<Friend>() {
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
    
    private Friend(Parcel in) {
		id = in.readLong();
	    fullname = in.readString();
	    username = in.readString();
	    town = in.readString();
	    desc = in.readString();
	    facebook = in.readString();
	    twitter = in.readString();
	    badgets = in.readInt();
	    mayorships = in.readInt();
	    owner = in.readByte() == 1;   
	    friendshipRequester = in.readByte() == 1;
	    friendshipState = in.readString();
	    userImage = in.readString();
    }

}
