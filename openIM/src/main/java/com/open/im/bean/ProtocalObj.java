package com.open.im.bean;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class ProtocalObj extends DataSupport implements Serializable{

//	public String toXML() {
//		XStream x = new XStream();
//		x.alias(this.getClass().getSimpleName(), this.getClass());
//		return x.toXML(this);
//	}
//
//	public Object fromXML(String xml) {
//		XStream x = new XStream();
//		x.alias(this.getClass().getSimpleName(), this.getClass());
//		return x.fromXML(xml);
//	}
	public String toJson(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public ProtocalObj fromJson(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, this.getClass());
	}
}
