package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable{

	public Integer id_kategoria;
	public String izena;
	public String deskribapena;
	public Integer irudiaFileSize;
	public String irudiaMimeType;
	public String irudiaBaseName;
	public String imgUrl;


	public Category() {
		this.id_kategoria = 0;
		this.izena = "";
		this.deskribapena = "";
		this.irudiaFileSize = 0;
		this.irudiaMimeType = "";
		this.irudiaBaseName = "";
		this.imgUrl = "";
	}

	public Category(Integer id_kategoria, String izena, String deskribapena,
			Integer irudiaFileSize, String irudiaMimeType,
			String irudiaBaseName, String imgUrl) {
		super();
		this.id_kategoria = id_kategoria;
		this.izena = izena;
		this.deskribapena = deskribapena;
		this.irudiaFileSize = irudiaFileSize;
		this.irudiaMimeType = irudiaMimeType;
		this.irudiaBaseName = irudiaBaseName;
		this.imgUrl = imgUrl;
	}

	public Category(JSONObject json) throws JSONException {
		super();
		this.id_kategoria = json.getInt("id_kategoria");
		this.izena = json.getString("izena");
		this.deskribapena = json.getString("deskribapena");
		this.irudiaFileSize = json.getInt("irudiaFileSize");
		this.irudiaMimeType = json.getString("irudiaMimeType");
		this.irudiaBaseName = json.getString("irudiaBaseName");
		this.imgUrl = json.getString("imgUrl");
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id_kategoria);
		dest.writeString(izena);
		dest.writeString(izena);
		dest.writeInt(irudiaFileSize);
		dest.writeString(irudiaMimeType);
		dest.writeString("irudiaBaseName");
		dest.writeString("imgUrl");
	}

	public static final Parcelable.Creator<Category> CREATOR
	= new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	private Category(Parcel in) {
		id_kategoria = in.readInt();
		izena = in.readString();
		deskribapena = in.readString();
		irudiaFileSize = in.readInt();
		irudiaMimeType = in.readString();
		irudiaBaseName = in.readString();
		imgUrl = in.readString();
	}

}
