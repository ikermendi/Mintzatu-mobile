package com.irontec.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
	
	public Double distantzia;
	public Integer checks;
	public Integer comments;
	public Long id_lekua;
	public Long id_kategoria;
	public Long id_erabiltzailea;
	public String izena;
	public String helbidea;
	public Double lat;
	public Double lng;
	public String herria;
	public String deskribapena;
	public String url;
	public String noiz;
	public String irudia;
	public List<Category> kategoriak;
	public String katImgUrl;
	
	public Place() {
		this.distantzia = 0d;
		this.checks = 0;
		this.comments = 0;
		this.id_lekua = 0l;
		this.id_kategoria = 0l;
		this.id_erabiltzailea = 0l;
		this.izena = "";
		this.helbidea = "";
		this.lat = 0d;
		this.lng = 0d;
		this.herria = "";
		this.deskribapena = "";
		this.url = "";
		this.noiz = "";
		this.irudia = "";
		this.kategoriak = new ArrayList<Category>();
		this.katImgUrl = "";
	}
	
	public Place(Double distantzia, Integer checks, Integer comments,
			Long id_lekua, Long id_kategoria, Long id_erabiltzailea,
			String izena, String helbidea, Double lat, Double lng,
			String herria, String deskribapena, String url, String noiz,
			String irudia, List<Category> kategoriak, String katImgUrl) {
		super();
		this.distantzia = (distantzia == null) ? 0d : distantzia;
		this.checks = (checks == null) ? 0 : checks;
		this.comments = (checks == null) ? 0 : checks;
		this.id_lekua = (id_lekua == null) ? 0 : id_lekua;
		this.id_kategoria = (id_kategoria == null) ? 0 : id_kategoria;
		this.id_erabiltzailea = (id_erabiltzailea == null) ? 0 : id_erabiltzailea;
		this.izena = (izena == null) ? "" : izena;
		this.helbidea = (helbidea == null) ? "" : helbidea;
		this.lat = (lat == null) ? 0d : lat;
		this.lng = (lng == null) ? 0d : lng;
		this.herria = (herria == null) ? "" : herria;
		this.deskribapena = (deskribapena == null) ? "" : deskribapena;
		this.url = (url == null) ? "" : url;
		this.noiz = (noiz == null) ? "" : noiz;
		this.irudia = (irudia == null) ? "" : irudia;
		this.kategoriak = (kategoriak == null) ? new ArrayList<Category>() : kategoriak;
		this.katImgUrl = (katImgUrl == null) ? "" : katImgUrl;
	}

	public Place(JSONObject json) throws JSONException {
		super();
		if (checkJsonField(json, "distantzia")) {
			this.distantzia = json.getDouble("distantzia");
		} else {
			this.distantzia = 0d;
		}
		if (checkJsonField(json, "checks")) {
			this.checks = json.getInt("checks");
		} else {
			this.checks = 0;
		}
		if (checkJsonField(json, "comments")) {
			this.comments = json.getInt("comments");
		} else {
			this.comments = 0;
		}
		if (checkJsonField(json, "id_lekua")) {
			this.id_lekua = json.getLong("id_lekua");
		} else {
			this.id_lekua = 0l;
		}
		if (checkJsonField(json, "id_kategoria")) {
			this.id_kategoria = json.getLong("id_kategoria");
		} else {
			this.id_kategoria = 0l;
		}
		if (checkJsonField(json, "id_erabiltzaile")) {
			this.id_erabiltzailea = json.getLong("id_erabiltzaile");
		} else {
			this.id_erabiltzailea = 0l;
		}
		if (checkJsonField(json, "izena")) {
			this.izena = json.getString("izena");
		} else {
			this.izena = "";
		}
		if (checkJsonField(json, "helbidea")) {
			this.helbidea = json.getString("helbidea");
		} else {
			this.helbidea = "";
		}
		if (checkJsonField(json, "helbideaLat")) {
			this.lat = json.getDouble("helbideaLat");
		} else {
			this.lat = 0d;
		}		
		if (checkJsonField(json, "helbideaLng")) {
			this.lng = json.getDouble("helbideaLng");
		} else {
			this.lng = 0d;
		}		
		if (checkJsonField(json, "herria")) {
			this.herria = json.getString("herria");
		} else {
			this.herria = "";
		}
		if (checkJsonField(json, "deskribapena")) {
			this.deskribapena = json.getString("deskribapena");
		} else {
			this.deskribapena = "";
		}
		if (checkJsonField(json, "url")) {
			this.url = json.getString("url");
		} else {
			this.url = "";
		}
		if (checkJsonField(json, "noiz")) {
			this.noiz = json.getString("noiz");
		} else {
			this.noiz = "";
		}
		if (checkJsonField(json,"irudia")) {
			this.irudia = json.getString("irudia");
		} else {
			this.irudia = "";
		}
		if (checkJsonField(json,"katImgUrl")) {
			this.katImgUrl = json.getString("katImgUrl");
		} else {
			this.katImgUrl = "";
		}
		if (checkJsonField(json, "kategoria")) {
			JSONArray jsonKategoriak = json.getJSONArray("kategoria");
			ArrayList<Category> kategoriak = new ArrayList<Category>();
			for(int i = 0; i < jsonKategoriak.length(); i++) {
				Category kategoria = new Category(jsonKategoriak.getJSONObject(i));
				kategoriak.add(kategoria);
			}
			this.kategoriak = kategoriak;
		} else {
			this.kategoriak = null;
		}
		if (checkJsonField(json, "kategoriaIzena")) {
			ArrayList<Category> kategoriak = new ArrayList<Category>();
			Category kategoria = new Category();
			kategoria.izena = json.getString("kategoriaIzena");
			kategoriak.add(kategoria);
			this.kategoriak = kategoriak;
		}
		
	}
	
	public Boolean checkJsonField(JSONObject json, String name) {
		if (json.has(name) && !json.isNull(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(distantzia);
		dest.writeInt(checks);
		dest.writeInt(comments);
		dest.writeLong(id_lekua);
		dest.writeLong(id_kategoria);
		dest.writeLong(id_erabiltzailea);
		dest.writeString(izena);
		dest.writeString(helbidea);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeString(herria);
		dest.writeString(deskribapena);
		dest.writeString(url);
		dest.writeString(noiz);
		dest.writeString(irudia);
		dest.writeList(kategoriak);
		dest.writeString(katImgUrl);
	}

	public static final Parcelable.Creator<Place> CREATOR
	= new Parcelable.Creator<Place>() {
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	private Place(Parcel in) {
		distantzia = in.readDouble();
		checks = in.readInt();
		comments = in.readInt();
		id_lekua = in.readLong();
		id_kategoria = in.readLong();
		id_erabiltzailea = in.readLong();
		izena = in.readString();
		helbidea = in.readString();
		lat = in.readDouble();
		lng = in.readDouble();
		herria = in.readString();
		deskribapena = in.readString();
		url = in.readString();
		noiz = in.readString();
		irudia = in.readString();
		kategoriak = new ArrayList<Category>();
	    in.readList(kategoriak, getClass().getClassLoader());
	    katImgUrl = in.readString();
	}

	public String getShortPlaceDescription() {
		if (this.deskribapena.length() > 140) {
			return this.deskribapena.substring(0,139).concat("... (+)");
		} else {
			return this.deskribapena;
		}
	}
	
}
