package br.com.cmabreu.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.JLabel;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;

public class VesselLabelMouseListener implements MouseListener {
	private MainScreen mainScreen;
	
	public VesselLabelMouseListener(MainScreen mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel vesselLabel = (JLabel)e.getSource();
		// JPanel vesselPanel = (JPanel)vesselLabel.getParent();
		for( Map.Entry<Long, Vessel> vessel : this.mainScreen.getFeatureControl().entrySet() ) {
			JLabel aLabel = vessel.getValue().getText();
			if( aLabel != null ) {
				if( aLabel.getText().equals( vesselLabel.getText() ) ) {
					SimpleFeature feature = vessel.getValue().getFeature();
					System.out.println("Você clicou em " + feature.getAttribute("mmsi") );
				    try {	
			        	Envelope envelope = feature.getBounds();
					    this.mainScreen.getMapPane().setDisplayArea(envelope);
				    } catch ( Exception ex ) { }				
				}
				aLabel.setBackground(Color.WHITE);
			}
		}
		vesselLabel.setBackground( Color.decode("#f3f7e6") );
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
