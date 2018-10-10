package gui;

import gammaBetaMc.GammaBetaFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import alphaBetaAnalysis.AlphaBetaAnalysis;
import alphaMc.AlphaFrame;

import nuclideExposure.NuclideExposure;

import danfulea.utils.ScanDiskLFGui;
import danfulea.db.DatabaseAgent;
//import jdf.db.DBConnection;
import danfulea.utils.FrameUtilities;

//import jdf.ui.frame.FrameUtilities;

/**
 * BANG is a scrambled acronym which stands for<br>
 * Alpha, Beta, Gamma analysis (both theoretical and experimental) <br>
 * and Nuclide data, public exposures, dosimetry and shielding (X-ray included).<br>
 * It is a modular application also known as RadiationHelper! 
 * 
 * @author Dan Fulea, 18 JUN. 2011
 * 
 */
@SuppressWarnings("serial")
public class Bang extends JFrame implements ActionListener {
	// public static Color bkgColor = new Color(112, 178, 136, 255);//green
	// default
	// public static Color bkgColor = new Color(130, 0, 0,
	// 255);//brown-red..VAMPIRE
	// public static Color bkgColor = new Color(245, 255, 250, 255);//white
	// -cream
	public static Color bkgColor = new Color(230, 255, 210, 255);// Linux mint
																	// green
																	// alike
	public static Color foreColor = Color.black;// Color.white;
	public static Color textAreaBkgColor = Color.white;// Color.black;
	public static Color textAreaForeColor = Color.black;// Color.yellow;

	private final Dimension PREFERRED_SIZE = new Dimension(600, 400);
	private static final String BASE_RESOURCE_CLASS = "gui.resources.BangResources";
	protected ResourceBundle resources;
	private static final String EXIT_COMMAND = "EXIT";
	private static final String ABOUT_COMMAND = "ABOUT";
	private static final String LOOKANDFEEL_COMMAND = "LOOKANDFEEL";
	private static final String GAMMAANALYSIS_COMMAND = "GAMMAANALYSIS";
	private static final String NEDS_COMMAND = "NEDS";
	private static final String ALPHAMC_COMMAND = "ALPHAMC";
	private static final String GESMC_COMMAND = "GESMC";
	private static final String ALPHAANALYSIS_COMMAND = "ALPHAANALYSIS";
	private static final String BETAANALYSIS_COMMAND = "BETAANALYSIS";

	private static final String ALPHA_STABILITY_COMMAND = "ALPHASTABILITY";
	private static final String BETA_STABILITY_COMMAND = "BETASTABILITY";
	private static final String GAMMA_STABILITY_COMMAND = "GAMMASTABILITY";
	private static final String SELFABSORPTION_COMMAND = "SELFABSORPTION";
	private static final String KCL_COMMAND = "KCL";
	private String command = null;

	/**
	 * Constructor
	 */
	public Bang() {
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("Application.NAME"));

		JMenuBar menuBar = createMenuBar(resources);
		setJMenuBar(menuBar);

		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);
		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});

	}

	/**
	 * Close program
	 */
	private void attemptExit() {
		DatabaseAgent.shutdownDerby();//DBConnection.shutdownDerby();
		dispose();
		System.exit(0);
	}

	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

	/**
	 * GUI creation.
	 */
	private void createGUI() {
		Character mnemonic = null;
		JButton button = null;
		// JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";

		

		JPanel p11 = new JPanel();
		p11.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		// roiCreateP.setBorder(FrameUtilities.getGroupBoxBorder(
		// resources.getString("roi.creation.border"), foreColor));
		buttonName = resources.getString("alphaAnalysis.button");
		buttonToolTip = resources.getString("alphaAnalysis.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName,
				ALPHAANALYSIS_COMMAND, buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("alphaAnalysis.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p11.add(button);
		buttonName = resources.getString("betaAnalysis.button");
		buttonToolTip = resources.getString("betaAnalysis.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName,
				BETAANALYSIS_COMMAND, buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("betaAnalysis.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p11.add(button);
		
		buttonName = resources.getString("gammaAnalysis.button");
		buttonToolTip = resources.getString("gammaAnalysis.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName,
				GAMMAANALYSIS_COMMAND, buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("gammaAnalysis.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p11.add(button);

		p11.setBackground(bkgColor);
		p11.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("experimental.border"), foreColor));

		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));

		buttonName = resources.getString("alphaMC.button");
		buttonToolTip = resources.getString("alphaMC.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName, ALPHAMC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("alphaMC.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1.add(button);

		buttonName = resources.getString("gesMC.button");
		buttonToolTip = resources.getString("gesMC.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName, GESMC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("gesMC.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1.add(button);

		buttonName = resources.getString("neds.button");
		buttonToolTip = resources.getString("neds.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName, NEDS_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("neds.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1.add(button);

		p1.setBackground(bkgColor);
		p1.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("theor.border"), foreColor));

		JPanel rrP = new JPanel();
		BoxLayout blrrP = new BoxLayout(rrP, BoxLayout.Y_AXIS);
		rrP.setLayout(blrrP);
		rrP.add(p11, null);
		rrP.add(p1, null);
		rrP.setBackground(bkgColor);

		JPanel content = new JPanel(new BorderLayout());
		content.add(rrP, BorderLayout.CENTER);
		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
	}

	/**
	 * Setting up the menu bar.
	 * 
	 * @param resources the resources
	 * @return the menu bar
	 */
	private JMenuBar createMenuBar(ResourceBundle resources) {
		// create the menus
		JMenuBar menuBar = new JMenuBar();

		String label;
		Character mnemonic;
		ImageIcon img;
		String imageName = "";

		// the file menu
		label = resources.getString("menu.file");
		mnemonic = (Character) resources.getObject("menu.file.mnemonic");
		JMenu fileMenu = new JMenu(label, true);
		fileMenu.setMnemonic(mnemonic.charValue());

		imageName = null;// resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.stability.alpha");
		mnemonic = (Character) resources
				.getObject("menu.file.stability.alpha.mnemonic");
		JMenuItem alphaItem = new JMenuItem(label, mnemonic.charValue());
		alphaItem.setActionCommand(ALPHA_STABILITY_COMMAND);
		alphaItem.addActionListener(this);
		alphaItem.setIcon(img);
		alphaItem.setToolTipText(resources
				.getString("menu.file.stability.alpha.toolTip"));
		fileMenu.add(alphaItem);

		imageName = null;// resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.stability.beta");
		mnemonic = (Character) resources
				.getObject("menu.file.stability.beta.mnemonic");
		JMenuItem betaItem = new JMenuItem(label, mnemonic.charValue());
		betaItem.setActionCommand(BETA_STABILITY_COMMAND);
		betaItem.addActionListener(this);
		betaItem.setIcon(img);
		betaItem.setToolTipText(resources
				.getString("menu.file.stability.beta.toolTip"));
		fileMenu.add(betaItem);

		imageName = null;// resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.stability.gamma");
		mnemonic = (Character) resources
				.getObject("menu.file.stability.gamma.mnemonic");
		JMenuItem gammaItem = new JMenuItem(label, mnemonic.charValue());
		gammaItem.setActionCommand(GAMMA_STABILITY_COMMAND);
		gammaItem.addActionListener(this);
		gammaItem.setIcon(img);
		gammaItem.setToolTipText(resources
				.getString("menu.file.stability.gamma.toolTip"));
		fileMenu.add(gammaItem);

		fileMenu.addSeparator();
		
		imageName = null;// resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.self");
		mnemonic = (Character) resources
				.getObject("menu.file.self.mnemonic");
		JMenuItem selfItem = new JMenuItem(label, mnemonic.charValue());
		selfItem.setActionCommand(SELFABSORPTION_COMMAND);
		selfItem.addActionListener(this);
		selfItem.setIcon(img);
		selfItem.setToolTipText(resources
				.getString("menu.file.self.toolTip"));
		fileMenu.add(selfItem);

		fileMenu.addSeparator();

		imageName = resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.exit");
		mnemonic = (Character) resources.getObject("menu.file.exit.mnemonic");
		JMenuItem exitItem = new JMenuItem(label, mnemonic.charValue());
		exitItem.setActionCommand(EXIT_COMMAND);
		exitItem.addActionListener(this);
		exitItem.setIcon(img);
		exitItem.setToolTipText(resources.getString("menu.file.exit.toolTip"));
		fileMenu.add(exitItem);

		// the help menu
		label = resources.getString("menu.help");
		mnemonic = (Character) resources.getObject("menu.help.mnemonic");
		JMenu helpMenu = new JMenu(label);
		helpMenu.setMnemonic(mnemonic.charValue());

		imageName = resources.getString("img.about");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.help.about");
		mnemonic = (Character) resources.getObject("menu.help.about.mnemonic");
		JMenuItem aboutItem = new JMenuItem(label, mnemonic.charValue());
		aboutItem.setActionCommand(ABOUT_COMMAND);
		aboutItem.addActionListener(this);
		aboutItem.setIcon(img);
		aboutItem
				.setToolTipText(resources.getString("menu.help.about.toolTip"));

		label = resources.getString("menu.help.LF");
		mnemonic = (Character) resources.getObject("menu.help.LF.mnemonic");
		JMenuItem lfItem = new JMenuItem(label, mnemonic.charValue());
		lfItem.setActionCommand(LOOKANDFEEL_COMMAND);
		lfItem.addActionListener(this);
		lfItem.setToolTipText(resources.getString("menu.help.LF.toolTip"));
		
		label = resources.getString("menu.help.kcl");
		mnemonic = (Character) resources.getObject("menu.help.kcl.mnemonic");
		JMenuItem kclItem = new JMenuItem(label, mnemonic.charValue());
		kclItem.setActionCommand(KCL_COMMAND);
		kclItem.addActionListener(this);
		kclItem.setToolTipText(resources.getString("menu.help.kcl.toolTip"));

		helpMenu.add(lfItem);
		helpMenu.addSeparator();
		helpMenu.add(kclItem);
		helpMenu.addSeparator();
		helpMenu.add(aboutItem);

		// finally, glue together the menu and return it
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	/**
	 * Setting up actions!
	 * @param arg0 arg0
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (command.equals(ABOUT_COMMAND)) {
			about();
		} else if (command.equals(EXIT_COMMAND)) {
			attemptExit();
		} else if (command.equals(LOOKANDFEEL_COMMAND)) {
			lookAndFeel();
		} else if (command.equals(ALPHAANALYSIS_COMMAND)) {
			alphaAnalysis();
		} else if (command.equals(BETAANALYSIS_COMMAND)) {
			betaAnalysis();
		} else if (command.equals(GAMMAANALYSIS_COMMAND)) {
			gammaAnalysis();
		} else if (command.equals(NEDS_COMMAND)) {
			nuclideExposure();
		} else if (command.equals(ALPHAMC_COMMAND)) {
			alphaMC();
		} else if (command.equals(GESMC_COMMAND)) {
			gesMC();
		} else if (command.equals(ALPHA_STABILITY_COMMAND)) {
			alphaStability();
		} else if (command.equals(BETA_STABILITY_COMMAND)) {
			betaStability();
		} else if (command.equals(GAMMA_STABILITY_COMMAND)) {
			gammaStability();
		} else if (command.equals(SELFABSORPTION_COMMAND)) {
			selfAbsorption();
		}else if (command.equals(KCL_COMMAND)) {
			KClCalculator();
		}
	}

	/**
	 * Go to Kcl calculator
	 */
	private void KClCalculator(){
		new KClCalculator(this);
	}

	/**
	 * Go to self-absorbtion study
	 */
	private void selfAbsorption(){
		new SelfAbsorptionFrame(this);
	}
	
	/**
	 * Go to experimental Alpha analysis
	 */
	private void alphaAnalysis(){
		AlphaBetaAnalysis.showLAF = false;
		AlphaBetaAnalysis.bkgColor = bkgColor;
		AlphaBetaAnalysis.foreColor = foreColor;
		AlphaBetaAnalysis.textAreaBkgColor = textAreaBkgColor;
		AlphaBetaAnalysis.textAreaForeColor = textAreaForeColor;
		setVisible(false);
		
		AlphaBetaAnalysis.IMODE=AlphaBetaAnalysis.IALPHA;
		new AlphaBetaAnalysis(this);
	}

	/**
	 * Go to experimental Beta analysis 
	 */
	private void betaAnalysis(){
		AlphaBetaAnalysis.showLAF = false;
		AlphaBetaAnalysis.bkgColor = bkgColor;
		AlphaBetaAnalysis.foreColor = foreColor;
		AlphaBetaAnalysis.textAreaBkgColor = textAreaBkgColor;
		AlphaBetaAnalysis.textAreaForeColor = textAreaForeColor;
		setVisible(false);

		AlphaBetaAnalysis.IMODE=AlphaBetaAnalysis.IBETA;
		new AlphaBetaAnalysis(this);		
	}

	/**
	 * Go to Alpha device stability test
	 */
	private void alphaStability() {
		new LongTimeStability(this,LongTimeStability.ALPHAMODE);
	}

	/**
	 * Go to Beta device stability test
	 */
	private void betaStability() {
		new LongTimeStability(this,LongTimeStability.BETAMODE);
	}

	/**
	 * Go to Gamma device stability test
	 */
	private void gammaStability() {
		new LongTimeStability(this,LongTimeStability.GAMMAMODE);
	}

	/**
	 * Go to theoretical gamma or beta detection efficiency
	 */
	private void gesMC() {
		GammaBetaFrame.showLAF = false;
		GammaBetaFrame.bkgColor = bkgColor;
		GammaBetaFrame.foreColor = foreColor;
		GammaBetaFrame.textAreaBkgColor = textAreaBkgColor;
		GammaBetaFrame.textAreaForeColor = textAreaForeColor;
		setVisible(false);
		new GammaBetaFrame(this);
	}

	/**
	 * Go to theoretical alpha efficiency. 
	 */
	private void alphaMC() {
		AlphaFrame.showLAF = false;
		AlphaFrame.bkgColor = bkgColor;
		AlphaFrame.foreColor = foreColor;
		AlphaFrame.textAreaBkgColor = textAreaBkgColor;
		AlphaFrame.textAreaForeColor = textAreaForeColor;
		setVisible(false);
		new AlphaFrame(this);
	}

	/**
	 * Go to nuclide exposure module
	 */
	private void nuclideExposure() {
		NuclideExposure.showLAF = false;
		NuclideExposure.bkgColor = bkgColor;
		NuclideExposure.foreColor = foreColor;
		NuclideExposure.textAreaBkgColor = textAreaBkgColor;
		NuclideExposure.textAreaForeColor = textAreaForeColor;
		setVisible(false);
		new NuclideExposure(this);
	}

	/**
	 * Go to experimental gamma analysis module
	 */
	private void gammaAnalysis() {
		GammaAnalysisFrame.showLAF = false;
		GammaAnalysisFrame.bkgColor = bkgColor;
		GammaAnalysisFrame.foreColor = foreColor;
		GammaAnalysisFrame.textAreaBkgColor = textAreaBkgColor;
		GammaAnalysisFrame.textAreaForeColor = textAreaForeColor;
		setVisible(false);
		new GammaAnalysisFrame(this);
	}

	/**
	 * Display the about window
	 */
	private void about() {
		new BangAboutFrame(this);
	}

	/**
	 * Changing the look and feel can be done here. Also display some gadgets.
	 */
	private void lookAndFeel() {
		setVisible(false);// setEnabled(false);
		new ScanDiskLFGui(this);
	}
}
