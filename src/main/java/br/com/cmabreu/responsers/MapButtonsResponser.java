package br.com.cmabreu.responsers;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.PanTool;
import org.geotools.swing.tool.ScrollWheelTool;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.ZoomOutTool;

public class MapButtonsResponser {
	private JMapPane mapPane;
	
	public MapButtonsResponser(JMapPane mapPane) {
		this.mapPane = mapPane;
	}

	public void noTool() {
		mapPane.setCursorTool( null );
	}
	
	public void voidTool() {
		mapPane.setCursorTool(
            new CursorTool() {
                @Override
                public void onMouseClicked(MapMouseEvent ev) {
                    // selectFeatures(ev);
                }
            }
        );		
	}
	
	public void zoomIn() {
		mapPane.setCursorTool( new ZoomInTool() );		
	}

	public void zoomWheel() {
		mapPane.setCursorTool( new ScrollWheelTool( this.mapPane ) );		
	}
	
	public void zoomOut() {
		mapPane.setCursorTool( new ZoomOutTool() );
	}

	public void pan() {
		mapPane.setCursorTool( new PanTool() );
	}

	
}
