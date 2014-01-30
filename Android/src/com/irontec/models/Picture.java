package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {

	public Long idIrudia;
	public Long idLekua;
	public Long idErabiltzailea;
	public String username;
	public String datetime;
	public String url;
	public String tinyImg;
	public String normalImg;

	public Picture(JSONObject json) throws JSONException {
		super();
		if (checkJsonField(json, "id_irudia")) {
			this.idIrudia = json.getLong("id_irudia");
		} else {
			this.idIrudia = 0l;
		}
		if (checkJsonField(json, "id_lekua")) {
			this.idLekua = json.getLong("id_lekua");
		} else {
			this.idLekua = 0l;
		}
		if (checkJsonField(json, "id_erabiltzailea")) {
			this.idErabiltzailea = json.getLong("id_erabiltzailea");
		} else {
			this.idErabiltzailea = 0l;
		}
		if (checkJsonField(json, "username")) {
			this.username = json.getString("username");
		} else {
			this.username = "";
		}
		if (checkJsonField(json, "datetime")) {
			this.datetime = json.getString("datetime");
		} else {
			this.datetime = "";
		}
		if (checkJsonField(json, "url")) {
			this.url = json.getString("url");
		} else {
			this.url = "";
		}
		if (checkJsonField(json, "tinyImg")) {
			this.tinyImg = json.getString("tinyImg");
		} else {
			this.tinyImg = "";
		}
		if (checkJsonField(json, "normalImg")) {
			this.normalImg = json.getString("normalImg");
		} else {
			this.normalImg = "";
		}
		if (checkJsonField(json, "imgUrl")) {
			this.tinyImg = json.getString("imgUrl");
			this.normalImg = json.getString("imgUrl");
		}
	}

	public Picture() {
		this.idIrudia = 0l;
		this.idLekua = 0l;
		this.idErabiltzailea = 0l;
		this.username = "";
		this.datetime = "";
		this.url = "";
		this.tinyImg = "";
		this.normalImg = "";
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(idIrudia);
		out.writeLong(idLekua);
		out.writeLong(idErabiltzailea);
		out.writeString(username);
		out.writeString(datetime);
		out.writeString(url);
		out.writeString(tinyImg);
		out.writeString(normalImg);
	}

	public static final Parcelable.Creator<Picture> CREATOR
	= new Parcelable.Creator<Picture>() {
		public Picture createFromParcel(Parcel in) {
			return new Picture(in);
		}

		public Picture[] newArray(int size) {
			return new Picture[size];
		}
	};

	private Picture(Parcel in) {
		idIrudia = in.readLong();
		idLekua = in.readLong();
		idErabiltzailea = in.readLong();
		username = in.readString();
		datetime = in.readString();
		url = in.readString();
		tinyImg = in.readString();
		normalImg = in.readString();
	}

	public Boolean checkJsonField(JSONObject json, String name) {
		if (json.has(name) && !json.isNull(name)) {
			return true;
		} else {
			return false;
		}
	}

}
