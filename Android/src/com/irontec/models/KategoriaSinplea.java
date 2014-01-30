package com.irontec.models;

import org.json.JSONException;
import org.json.JSONObject;

public class KategoriaSinplea {

	public Long id;
	public String name;
	public String imgCat;
	
	public KategoriaSinplea(JSONObject json) throws JSONException {
		super();
		this.id = json.getLong("id");
		this.name = json.getString("name");
		this.imgCat = json.getString("imgCat");
	}
	public KategoriaSinplea(Long id, String name, String imgCat) {
		super();
		this.id = id;
		this.name = name;
		this.imgCat = imgCat;
	}
	
}
