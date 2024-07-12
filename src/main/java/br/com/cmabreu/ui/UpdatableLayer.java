package br.com.cmabreu.ui;

import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapLayerEvent;
import org.geotools.styling.Style;

public class UpdatableLayer extends FeatureLayer {

	  public UpdatableLayer(@SuppressWarnings("rawtypes") FeatureCollection collection, Style style) {
	    super(collection, style);
	  }

	  public void updated() {
	    fireMapLayerListenerLayerChanged(MapLayerEvent.DATA_CHANGED);
	  }

}
