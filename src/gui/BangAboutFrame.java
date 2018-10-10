package gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import danfulea.utils.FrameUtilities;

/**
 * The About window displays some information about the application. 
 * 
 * @author Dan Fulea, 06 Apr. 2011
 */

@SuppressWarnings("serial")
public class BangAboutFrame extends JFrame {
	private Bang mf;
	private static final String BASE_RESOURCE_CLASS = "gui.resources.BangResources";
	private ResourceBundle resources;

	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel2 = new JLabel();
	private JLabel lbAuthor = new JLabel();
	private JLabel lbVersion = new JLabel();
	private JLabel jLabel7 = new JLabel();
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTextArea textLicense = new JTextArea();

	/**
	 * Constructor. About window is connected to main window.
	 * @param mf, the Bang object
	 */
	public BangAboutFrame(Bang mf) {
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("About.NAME"));
		this.setResizable(false);

		this.mf = mf;

		jLabel1.setForeground(Bang.foreColor);
		jLabel2.setForeground(Bang.foreColor);
		lbAuthor.setForeground(Bang.foreColor);
		lbVersion.setForeground(Bang.foreColor);
		jLabel7.setForeground(Bang.foreColor);
		textLicense.setBackground(Bang.textAreaBkgColor);
		textLicense.setForeground(Bang.textAreaForeColor);

		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);
		mf.setEnabled(false);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);// not necessary,
																// exit normal!
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});
	}

	/**
	 * Exit method
	 */
	private void attemptExit() {
		mf.setEnabled(true);
		dispose();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void createGUI() {

		jPanel1.setLayout(new java.awt.BorderLayout());

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabel1.setIcon(FrameUtilities.getImageIcon(
				this.resources.getString("icon.url"), this));
		jLabel1.setText(this.resources.getString("Application.NAME"));
		jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);
		jPanel1.setBackground(Bang.bkgColor);

		jPanel3.setLayout(new java.awt.GridLayout(3, 2, 0, 4));

		jLabel2.setText(this.resources.getString("Author"));
		jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		jPanel3.add(jLabel2);

		lbAuthor.setText(this.resources.getString("Author.name"));

		jPanel3.add(lbAuthor);

		jLabel7.setText(this.resources.getString("Version"));
		jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		jPanel3.add(jLabel7);

		lbVersion.setText(this.resources.getString("Version.name"));

		jPanel3.add(lbVersion);
		jPanel3.setBackground(Bang.bkgColor);

		jPanel1.add(jPanel3, java.awt.BorderLayout.SOUTH);

		getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

		jPanel2.setLayout(new java.awt.BorderLayout());

		jScrollPane1.setBorder(new javax.swing.border.TitledBorder(
				new javax.swing.border.LineBorder(
						new java.awt.Color(0, 51, 255), 1, true),
				"BSD Licence",
				javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.TOP));
		jScrollPane1
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane1
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setAutoscrolls(true);
		textLicense.setColumns(1);
		textLicense.setEditable(false);

		textLicense.setLineWrap(true);
		textLicense.setRows(10);
		textLicense.setText(this.resources.getString("License"));
		textLicense.setWrapStyleWord(true);
		jScrollPane1.setViewportView(textLicense);

		jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);
		jPanel2.setBackground(Bang.bkgColor);

		getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);
		pack();
	}
}
