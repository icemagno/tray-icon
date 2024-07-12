package br.com.cmabreu.ui;

import org.geotools.map.MapLayerListEvent;
import org.geotools.map.MapLayerListListener;

public class MapListener implements MapLayerListListener {

	@Override
	public void layerAdded(MapLayerListEvent event) {
		System.out.println( "Layer Added: " + event.getLayer().getTitle() );
	}

	@Override
	public void layerRemoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerChanged(MapLayerListEvent event) {
		System.out.println( "Layer Changed: "  + event.getLayer().getTitle() );
	}

	@Override
	public void layerMoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerPreDispose(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

}
