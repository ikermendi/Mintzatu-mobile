package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceHistory implements Parcelable {

	public static final Integer TYPE_COMMENT = 1;
	public static final Integer TYPE_IMAGE = 2;
	public static final Integer TYPE_CHECKIN = 3;

	public Long id;
	public Long idWho;
	public String who;
	public String when;
	public Integer type;
	public String comment;
	public String imgName;
	public String tinyImg;
	public String normalImg;
	public String whoImg;

	public PlaceHistory(Long id, Long idWho, String who, String when,
			String type, String comment, String imgName, String tinyImg,
			String normalImg, String whoImg) {
		super();
		this.id = id;
		this.idWho = idWho;
		this.who = who;
		this.when = when;
		this.type = 0;
		this.comment = comment;
		this.imgName = imgName;
		this.tinyImg = tinyImg;
		this.normalImg = normalImg;
		this.whoImg = whoImg;
	}

	public PlaceHistory(JSONObject json) throws JSONException {
		super();
		this.id = json.getLong("id");
		this.idWho = json.getLong("idWho");
		this.who = json.getString("who");
		this.when = json.getString("when");
		if (json.getString("type").equals("comment")) {
			this.type = 1;
		} else if (json.getString("type").equals("image")) {
			this.type = 2;
		} else {
			this.type = 3;
		}
		if (json.has("comment")) {
			this.comment = json.getString("comment");
		} else {
			this.comment = "";
		}
		if (json.has("imgName") && json.has("tinyImg") && json.has("normalImg")) {
			this.imgName = json.getString("imgName");
			this.tinyImg = json.getString("tinyImg");
			this.normalImg = json.getString("normalImg");
		}
		this.whoImg = json.getString("whoImg");
	}

	public static final Parcelable.Creator<PlaceHistory> CREATOR
	= new Parcelable.Creator<PlaceHistory>() {
		public PlaceHistory createFromParcel(Parcel in) {
			return new PlaceHistory(in);
		}

		public PlaceHistory[] newArray(int size) {
			return new PlaceHistory[size];
		}
	};

	private PlaceHistory(Parcel in) {
		id = in.readLong();
		idWho = in.readLong();
		who = in.readString();
		when = in.readString();
		type = in.readInt();
		comment = in.readString();
		imgName = in.readString();
		tinyImg = in.readString();
		normalImg = in.readString();
		whoImg = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(idWho);
		dest.writeString(who);
		dest.writeString(when);
		dest.writeInt(type);
		dest.writeString(comment);
		dest.writeString(imgName);
		dest.writeString(tinyImg);
		dest.writeString(normalImg);
		dest.writeString(whoImg);
	}

}
