package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Badge implements Parcelable{

    public Long id;
    public String name;
    public String desc;
    public String when;
    public String iden;
    public String img;
    
	public Badge(JSONObject json) throws JSONException {
		super();
		this.id = json.getLong("id");
		this.name = json.getString("name");
		this.desc = json.getString("desc");
		this.when = json.getString("when");
		this.iden = json.getString("iden");
		this.img = json.getString("img");
	}
    
	public Badge(Long id, String name, String desc, String when, String iden,
			String img) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.when = when;
		this.iden = iden;
		this.img = img;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(desc);
		dest.writeString(when);
		dest.writeString(when);
		dest.writeString(img);
	}

	public static final Parcelable.Creator<Badge> CREATOR
	= new Parcelable.Creator<Badge>() {
		public Badge createFromParcel(Parcel in) {
			return new Badge(in);
		}

		public Badge[] newArray(int size) {
			return new Badge[size];
		}
	};

	private Badge(Parcel in) {
		this.id = in.readLong();
		this.name = in.readString();
		this.desc = in.readString();
		this.when = in.readString();
		this.iden = in.readString();
		this.img = in.readString();
	}
	
}
