package br.com.cmabreu.ui;

import javax.swing.JLabel;

import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;

public class Vessel {
	private SimpleFeature feature;
	private JLabel text;
	private Polygon hull;
	private Polygon antena;
	
	public void setText(JLabel text) {
		this.text = text;
	}
	
	public JLabel getText() {
		return text;
	}
	
	public SimpleFeature getFeature() {
		return feature;
	}

	public Polygon getHull() {
		return hull;
	}

	public void setHull(Polygon hull) {
		this.hull = hull;
	}

	public Polygon getAntena() {
		return antena;
	}

	public void setAntena(Polygon antena) {
		this.antena = antena;
	}

	public void setFeature(SimpleFeature feature) {
		this.feature = feature;
	}

}
