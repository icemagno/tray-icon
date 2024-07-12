package br.com.cmabreu.ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.osm.OSMService;
import org.geotools.tile.util.TileLayer;
import org.geotools.xml.styling.SLDParser;
import org.json.JSONObject;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.responsers.MapButtonsResponser;

public class MainScreen {
	private TrayIcon trayIcon;
	private SystemTray tray;
	private MapContent map;
	private JLabel messageTypeLabel;
	private JLabel lastMMSILabel;
	private SimpleFeatureType VESSEL;
	private DefaultFeatureCollection vesselCollection;
	private JMapPane mapPane = new JMapPane();
	private JSONObject config;
	private Logger logger = LoggerFactory.getLogger(MainScreen.class);
	private Map<Long, Vessel> featureControl = new HashMap<Long, Vessel>();
	private JPanel westContentPanel;
	private JFrame mainScreen;

	public Map<Long, Vessel> getFeatureControl() {
		return featureControl;
	}

	public JMapPane getMapPane() {
		return mapPane;
	}

	public MainScreen(JSONObject config) {
		logger.info("init");
		this.config = config;
	}

	private void initGeometries() {
		// Tipo para armazenar uma Feature de navio
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.setName("Vessel");
		builder.add("ship_geom", Polygon.class);
		builder.add("antena_geom", Polygon.class);
		builder.length(100).add("shipname", String.class);
		builder.length(30).add("shiptype", String.class);
		builder.length(7).add("shipcolor", String.class);
		builder.length(7).add("antenacolor", String.class);
		builder.add("mmsi", Long.class);
		builder.add("lat", Float.class);
		builder.add("lon", Float.class);
		builder.add("length", Float.class);
		builder.add("width", Float.class);
		builder.add("bearing", Integer.class);
		builder.add("opacity", Double.class);
		builder.add("visible", Boolean.class);
		builder.length(200).add("callsign", String.class);
		this.VESSEL = builder.buildFeatureType();

		//
		this.vesselCollection = new DefaultFeatureCollection("Vessels", this.VESSEL);

	}

	private void trayTest(JFrame mainScreen) {

		System.out.println("system tray supported");
		tray = SystemTray.getSystemTray();

		Image image = Toolkit.getDefaultToolkit().getImage("/magno/logo.png");
		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exiting....");
				System.exit(0);
			}
		};

		PopupMenu popup = new PopupMenu();
		MenuItem defaultItem = new MenuItem("Exit");
		defaultItem.addActionListener(exitListener);
		popup.add(defaultItem);
		defaultItem = new MenuItem("Open");

		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainScreen.setVisible(true);
				mainScreen.setExtendedState(JFrame.NORMAL);
			}
		});

		popup.add(defaultItem);
		trayIcon = new TrayIcon(image, "SystemTray Demo", popup);
		trayIcon.setImageAutoSize(true);

		mainScreen.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == JFrame.ICONIFIED) {
					try {
						tray.add(trayIcon);
						mainScreen.setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to tray");
					}
				}
				if (e.getNewState() == 7) {
					try {
						tray.add(trayIcon);
						mainScreen.setVisible(false);
						System.out.println("added to SystemTray");
					} catch (AWTException ex) {
						System.out.println("unable to add to system tray");
					}
				}
				if (e.getNewState() == JFrame.MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					mainScreen.setVisible(true);
					System.out.println("Tray icon removed");
				}
				if (e.getNewState() == JFrame.NORMAL) {
					tray.remove(trayIcon);
					mainScreen.setVisible(true);
					System.out.println("Tray icon removed");
				}
			}
		});

		mainScreen.setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));

	}

	public void startMap() {
		mainScreen = new JFrame();
		mainScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainScreen.setSize(1200, 700);
		mainScreen.setLayout(new BorderLayout());

		try {
			System.out.println("setting look and feel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Unable to set LookAndFeel");
		}

		if (SystemTray.isSupported())
			trayTest(mainScreen);

		if (this.config.getBoolean("maximized")) {
			mainScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
			mainScreen.setUndecorated(true);
		}

		createMainPanel();
		createMenuBar();

		mainScreen.setVisible(true);
		map.addMapLayerListListener(new MapListener());
		this.initGeometries();
		if (this.config.getBoolean("loadOSM")) {
			String baseURL = this.config.getString("OSMSource");
			TileService service = new OSMService("OSM", baseURL);
			map.addLayer(new TileLayer(service));
		}

		if (this.config.getBoolean("loadMainMap"))
			this.loadShapeMap(map);
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		mainScreen.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);

		JMenuItem newAction = new JMenuItem("New");
		newAction.addActionListener(ev -> {
			String mmsi = JOptionPane.showInputDialog("Digite o MMSI:");
			System.out.println(mmsi);
		});

		JMenuItem openAction = new JMenuItem("Open");
		openAction.addActionListener(ev -> {
			JDialog d = new JDialog(this.mainScreen, "dialog Box");
			d.setLocationRelativeTo(null);
			d.setSize(200, 200);
			JButton button = new JButton("Close");
			button.addActionListener(e -> {
				d.dispose();
			});
			d.add(button);
			/*
			 * final Toolkit toolkit = Toolkit.getDefaultToolkit(); final Dimension
			 * screenSize = toolkit.getScreenSize(); final int x = (screenSize.width -
			 * d.getWidth()) / 2; final int y = (screenSize.height - d.getHeight()) / 2;
			 * d.setLocation(x, y);
			 */
			d.setVisible(true);
		});

		JMenuItem exitAction = new JMenuItem("Exit");
		JMenuItem cutAction = new JMenuItem("Cut");
		JMenuItem copyAction = new JMenuItem("Copy");
		JMenuItem pasteAction = new JMenuItem("Paste");

		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);

	}

	private void createMainPanel() {
		Container mainPanel = mainScreen.getContentPane();
		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.add(getNorthPanel(), BorderLayout.NORTH);
		mainPanel.add(getSouthPanel(), BorderLayout.SOUTH);
		mainPanel.add(getWestPanel(), BorderLayout.WEST);
		mainPanel.add(getCenterPanel(), BorderLayout.CENTER);
	}

	private JPanel getWestPanel() {
		JPanel westButtonsPanel = new JPanel();
		westButtonsPanel.setPreferredSize(new Dimension(220, 40));
		westButtonsPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel west = new JPanel();
		west.setLayout(new BorderLayout(0, 0));
		west.setBorder(BorderFactory.createEtchedBorder());
		west.add(westButtonsPanel, BorderLayout.NORTH);
		addScrollFrame(west, westButtonsPanel);
		return west;
	}

	private JPanel getNorthPanel() {
		JPanel northContentPanel = new JPanel();
		northContentPanel.setPreferredSize(new Dimension(100, 40));
		northContentPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel north = new JPanel(new BorderLayout());
		north.add(northContentPanel, BorderLayout.WEST);
		north.setBorder(BorderFactory.createEtchedBorder());
		return north;
	}

	private JPanel getSouthPanel() {
		JPanel messageTypePanel = new JPanel(new BorderLayout());
		messageTypePanel.setPreferredSize(new Dimension(140, 20));
		messageTypePanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel lastMMSIPanel = new JPanel(new BorderLayout());
		lastMMSIPanel.setPreferredSize(new Dimension(190, 20));
		lastMMSIPanel.setBorder(BorderFactory.createEtchedBorder());

		messageTypeLabel = new JLabel("");
		messageTypeLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 9));
		messageTypeLabel.setOpaque(true);
		messageTypeLabel.setHorizontalAlignment(JLabel.CENTER);
		messageTypeLabel.setBackground(Color.WHITE);
		messageTypePanel.add(messageTypeLabel);

		lastMMSILabel = new JLabel("");
		lastMMSILabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 9));
		lastMMSILabel.setOpaque(true);
		lastMMSILabel.setHorizontalAlignment(JLabel.CENTER);
		lastMMSILabel.setBackground(Color.WHITE);
		lastMMSIPanel.add(lastMMSILabel);

		JPanel south = new JPanel(new BorderLayout());
		south.setPreferredSize(new Dimension(200, 20));
		south.add(messageTypePanel, BorderLayout.WEST);
		south.add(lastMMSIPanel, BorderLayout.EAST);
		south.setBorder(BorderFactory.createEtchedBorder());
		return south;
	}

	private JPanel getCenterPanel() {
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(BorderFactory.createEtchedBorder());

		this.map = new MapContent();
		this.mapPane.setMapContent(this.map);
		this.mapPane.setBackground(getConfigColor("backgroundColor"));
		center.add(this.mapPane, BorderLayout.CENTER);

		JPanel centerButtonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerButtonBar.setBorder(BorderFactory.createEtchedBorder());
		centerButtonBar.setPreferredSize(new Dimension(500, 40));

		// Botoes
		MapButtonsResponser mapButtonsResponser = new MapButtonsResponser(this.mapPane);

		JButton zoomInButton = new JButton("Zoom In");
		zoomInButton.addActionListener(e -> {
			mapButtonsResponser.zoomIn();
		});
		centerButtonBar.add(zoomInButton);

		JButton drawButton = new JButton("Zoom Out");
		drawButton.addActionListener(e -> {
			mapButtonsResponser.zoomOut();
		});
		centerButtonBar.add(drawButton);

		JButton goExtentButton = new JButton("Enquadrar");
		goExtentButton.addActionListener(e -> {
			try {
				Envelope envelope = this.vesselCollection.collection().getBounds();
				this.mapPane.setDisplayArea(envelope);
			} catch (Exception ex) {
			}
		});
		centerButtonBar.add(goExtentButton);

		JButton noToolButton = new JButton("No Tool");
		noToolButton.addActionListener(e -> {
			mapButtonsResponser.noTool();
		});
		centerButtonBar.add(noToolButton);

		center.add(centerButtonBar, BorderLayout.NORTH);
		return center;
	}

	public void addScrollFrame(JPanel toWho, JPanel buttonPanel) {

		JButton btnAddType = new JButton("Add");
		btnAddType.addActionListener(e -> {
			addVesselPane("This is a new Card!");
		});
		buttonPanel.add(btnAddType);

		JButton btnDeleteField = new JButton("Del");
		buttonPanel.add(btnDeleteField);

		JButton btnSaveRule = new JButton("Save");
		buttonPanel.add(btnSaveRule);

		this.westContentPanel = new JPanel();
		westContentPanel.setLayout(new BoxLayout(westContentPanel, BoxLayout.Y_AXIS));

		JScrollPane scroll = new JScrollPane(westContentPanel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		scroll.setViewportView(westContentPanel);
		toWho.add(scroll, BorderLayout.CENTER);
	}

	private JLabel addVesselPane(String text) {
		JLabel vesselLabel = new JLabel(text, JLabel.LEFT);
		vesselLabel.setFont(new Font("Arial", Font.BOLD, 10));
		vesselLabel.setOpaque(true);
		vesselLabel.setBackground(Color.WHITE);
		vesselLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		vesselLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		vesselLabel.setPreferredSize(new Dimension(200, 35));
		vesselLabel.addMouseListener(new VesselLabelMouseListener(this));

		JPanel jp = new JPanel(new BorderLayout());
		jp.setMaximumSize(new Dimension(200, 35));

		jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0),
				BorderFactory.createLineBorder(Color.BLACK)));

		jp.add(vesselLabel);
		this.westContentPanel.add(jp);
		this.mainScreen.revalidate();
		return vesselLabel;
	}

	private Color getConfigColor(String configName) {
		return new Color(Integer.parseInt(this.config.getString(configName).substring(1), 16));
	}

	private void loadShapeMap(MapContent map) {
		try {
			File file = new File("/layers/" + this.config.getString("mainMapName"));
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			Style style = getStyle(this.config.getString("mainMapStyle"));
			if (style == null)
				style = SLD.createSimpleStyle(featureSource.getSchema());
			FeatureLayer layer = new FeatureLayer(featureSource, style);
			layer.setTitle("Base Map");
			map.addLayer(layer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Style getStyle(String styleName) {
		try {
			SLDParser parser = new SLDParser(CommonFactoryFinder.getStyleFactory());
			parser.setInput(new File("/layers/estilos/" + styleName));
			parser.parseSLD();
			Style[] style = parser.readDOM();
			return style[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
