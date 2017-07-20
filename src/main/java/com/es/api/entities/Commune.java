/**
 * 
 */
package com.es.api.entities;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.geo.GeoPoint;

/**
 * @author odonn
 *
 */
public class Commune {

    private String codeINSEE;
    private String name;
    private String codePostal;
    private String libelleAcheminement;
    private String line5;
    private String lat;
    private String lng;
    private GeoPoint location;

    public Commune(String codeinsee, String name, String cp, String libelle, String line, String lat, String lon) {
	this.codeINSEE = codeinsee;
	this.name = name;
	this.codePostal = cp;
	this.libelleAcheminement = libelle;
	this.line5 = line;
	this.lat = lat;
	this.lng = lon;
	
	if (StringUtils.isNotBlank(lat) && StringUtils.isNotBlank(lon)) {
    
	    setLocation(new GeoPoint(Double.valueOf(lat), Double.valueOf(lon)));   
	}
    }

    public String getCodeINSEE() {
	return codeINSEE;
    }

    public void setCodeINSEE(String codeINSEE) {
	this.codeINSEE = codeINSEE;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getCodePostal() {
	return codePostal;
    }

    public void setCodePostal(String codePostal) {
	this.codePostal = codePostal;
    }

    public String getLibelleAcheminement() {
	return libelleAcheminement;
    }

    public void setLibelleAcheminement(String libelleAcheminement) {
	this.libelleAcheminement = libelleAcheminement;
    }

    public String getLine5() {
	return line5;
    }

    public void setLine5(String line5) {
	this.line5 = line5;
    }

    public String getLat() {
	return lat;
    }

    public void setLat(String lat) {
	this.lat = lat;
    }

    public String getLng() {
	return lng;
    }

    public void setLng(String lng) {
	this.lng = lng;
    }

    public GeoPoint getLocation() {
	return location;
    }

    public void setLocation(GeoPoint location) {
	this.location = location;
    }
}
