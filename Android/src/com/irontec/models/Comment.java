package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {

	public Integer semeak;
	public String iruzkina;
	public String noiz;
	public Long idErabiltzaile;
	public Long idLeku;
	public Long idAita;
	public Long idIruzkin;
	public String userImg;
	public String izena;
	
	public Comment(JSONObject json) throws JSONException {
		this.semeak = json.getInt("semeak");
		this.iruzkina = json.getString("iruzkina");
		this.noiz = json.getString("noiz");
		this.idErabiltzaile = json.getLong("idErabiltzaile");
		this.idLeku = json.getLong("idLeku");
		if (json.isNull("idAita")) {
			this.idAita = null;
		} else {
			this.idAita = json.getLong("idAita");
		}
		this.idIruzkin = json.getLong("idIruzkin");
		this.userImg = json.getString("userImg");
		this.izena = json.getString("izena");
	}
	
	public Comment(Integer semeak, String iruzkina, String noiz,
			Long idErabiltzaile, Long idLeku, Long idAita, Long idIruzkin,
			String userImg, String izena) {
		super();
		this.semeak = semeak;
		this.iruzkina = iruzkina;
		this.noiz = noiz;
		this.idErabiltzaile = idErabiltzaile;
		this.idLeku = idLeku;
		this.idAita = idAita;
		this.idIruzkin = idIruzkin;
		this.userImg = userImg;
		this.izena = izena;
	}
	
	

}
