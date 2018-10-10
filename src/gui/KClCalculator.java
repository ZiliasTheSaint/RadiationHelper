package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * Class designed to compute the activity of 40K contained in a KCl sample (potassium chloride). 
 * It is useful for chemists or physicists when preparing beta standard sources!! 
 * 
 * @author Dan Fulea, 24 OCT. 2011
 * 
 */
@SuppressWarnings("serial")
public class KClCalculator extends JFrame implements ActionListener{
	private Bang mf;
	private ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(600, 400);
	private JTextField kTf=new JTextField(5);
	private JTextField clTf=new JTextField(5);
	private JTextField k40procentualTf=new JTextField(5);
	private JTextField g1Tf=new JTextField(15);
	private JTextField gTf=new JTextField(5);
	private JTextField amTf=new JTextField(5);
	private JTextField hlTf=new JTextField(5);
	private JTextField aTf=new JTextField(15);
	
	private String command = "";
	private static final String INIT_COMMAND = "INIT";
	private static final String COMPUTE_COMMAND = "COMPUTE";
	
	/**
	 * Constructor.
	 * @param mf the Bang object
	 */
	public KClCalculator(Bang mf){
		this.mf = mf;
		this.resources = mf.resources;
		String titleS = resources.getString("KCl.NAME");
		
		this.setTitle(titleS);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});
		
		createGUI();

		mf.setEnabled(false);
		
		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);
	}

	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

	/**
	 * Close program
	 */
	private void attemptExit() {

		mf.setEnabled(true);
		dispose();

	}

	/**
	 * Create GUI
	 */
	private void createGUI() {
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		JPanel p1P = new JPanel();
		p1P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p1P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.k"));
		label.setForeground(Bang.foreColor);
		p1P.add(label);
		p1P.add(kTf);kTf.setText("39.0983");
		
		JPanel p2P = new JPanel();
		p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p2P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.cl"));
		label.setForeground(Bang.foreColor);
		p2P.add(label);
		p2P.add(clTf);clTf.setText("35.453");
		
		JPanel p3P = new JPanel();
		p3P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p3P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.40k"));
		label.setForeground(Bang.foreColor);
		p3P.add(label);
		p3P.add(k40procentualTf);k40procentualTf.setText("0.0117");

		JPanel p4P = new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p4P.setBackground(Bang.bkgColor);
		buttonName = resources.getString("kcl.initiate.button");
		buttonToolTip = resources.getString("kcl.initiate.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName,
				INIT_COMMAND, buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("kcl.initiate.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p4P.add(button);
		
		JPanel kclP = new JPanel();
		BoxLayout blkclP = new BoxLayout(kclP, BoxLayout.Y_AXIS);
		kclP.setLayout(blkclP);
		kclP.setBackground(Bang.bkgColor);
		kclP.add(p1P);
		kclP.add(p2P);
		kclP.add(p3P);
		kclP.add(p4P);
		kclP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("kcl.border"), Bang.foreColor));
		
		JPanel p11P = new JPanel();
		p11P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p11P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.1g"));
		label.setForeground(Bang.foreColor);
		p11P.add(label);
		p11P.add(g1Tf);
		
		JPanel p12P = new JPanel();
		p12P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p12P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.hl"));
		label.setForeground(Bang.foreColor);
		p12P.add(label);
		p12P.add(hlTf);hlTf.setText("1.27E9");		

		JPanel p13P = new JPanel();
		p13P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p13P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.am"));
		label.setForeground(Bang.foreColor);
		p13P.add(label);
		p13P.add(amTf);amTf.setText("40.0");
		
		JPanel p14P = new JPanel();
		p14P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p14P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.g"));
		label.setForeground(Bang.foreColor);
		p14P.add(label);
		p14P.add(gTf);

		JPanel p15P = new JPanel();
		p15P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p15P.setBackground(Bang.bkgColor);
		buttonName = resources.getString("kcl.compute.button");
		buttonToolTip = resources.getString("kcl.compute.button.toolTip");
		buttonIconName = null;// resources.getString("img.pan.right");
		button = FrameUtilities.makeButton(buttonIconName,
				COMPUTE_COMMAND, buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("kcl.compute.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p15P.add(button);
		
		JPanel p16P = new JPanel();
		p16P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p16P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("kcl.a"));
		label.setForeground(Bang.foreColor);
		p16P.add(label);
		p16P.add(aTf);
		
		JPanel mainP = new JPanel();
		BoxLayout blmainP = new BoxLayout(mainP, BoxLayout.Y_AXIS);
		mainP.setLayout(blmainP);
		mainP.setBackground(Bang.bkgColor);
		mainP.add(kclP);
		mainP.add(p11P);
		mainP.add(p12P);
		mainP.add(p13P);
		mainP.add(p14P);
		mainP.add(p15P);
		mainP.add(p16P);
		
		JPanel content = new JPanel(new BorderLayout());
		content.add(mainP, BorderLayout.CENTER);
		//content.add(kclP, BorderLayout.CENTER);
		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
	}
	
	/**
	 * Setting up actions!
	 * @param arg0 arg0
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (command.equals(INIT_COMMAND)) {
			init();
		} else if (command.equals(COMPUTE_COMMAND)) {
			compute();
		} 
	}
	
	/**
	 * Initialize computations.
	 */
	private void init(){
		double abundence=0.0;
		double aKnatural=0.0;
		double aCl=0.0;
		
		boolean nulneg=false;
		try {
			aKnatural = Convertor.stringToDouble(kTf.getText());
			aCl = Convertor.stringToDouble(clTf.getText());
			abundence = Convertor.stringToDouble(k40procentualTf.getText());

			if (aKnatural <= 0)
				nulneg = true;
			if (aCl <= 0)
				nulneg = true;
			if (abundence <= 0)
				nulneg = true;
			
		} catch (Exception e) {
			String title = resources.getString("number.error.title");
			String message = resources.getString("number.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
			return;
		}
		
		if (nulneg) {
			String title = resources.getString("number.error.title");
			String message = resources.getString("number.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		//-----------------------
		double mass1g = abundence*aKnatural/(100.0*(aCl+aKnatural));
		g1Tf.setText(Convertor.doubleToString(mass1g));
	}
	
	/**
	 * Compute 40K activity
	 */
	private void compute(){
		double mass1g=0.0;
		double amK40=0.0;
		double hlK40=0.0;
		double grams=0.0;
		
		boolean nulneg=false;
		//===========
		try {
			mass1g = Convertor.stringToDouble(g1Tf.getText());
			amK40 = Convertor.stringToDouble(amTf.getText());
			hlK40 = Convertor.stringToDouble(hlTf.getText());
			grams = Convertor.stringToDouble(gTf.getText());

			if (mass1g <= 0)
				nulneg = true;
			if (amK40 <= 0)
				nulneg = true;
			if (hlK40 <= 0)
				nulneg = true;
			if (grams <= 0)
				nulneg = true;
			
		} catch (Exception e) {
			String title = resources.getString("number.error.title");
			String message = resources.getString("number.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
			return;
		}
		
		if (nulneg) {
			String title = resources.getString("number.error.title");
			String message = resources.getString("number.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		//==============
		double na=6.022E23;//Avogadro!
		hlK40=hlK40*365.25*24.0*3600.0;//sec
		double a1g=Math.log(2.0)*na*mass1g/(hlK40*amK40);
		double a = a1g * grams;
		aTf.setText(Convertor.doubleToString(a));
	}
}
