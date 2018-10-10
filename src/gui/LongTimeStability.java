package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import alphaBetaAnalysis.AlphaBetaAnalysis;
import danfulea.utils.TimeUtilities;
import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.math.Sort;
import danfulea.phys.PhysUtilities;
import danfulea.utils.FrameUtilities;

/**
 * Class for testing the Long time stability for alpha, beta and gamma detectors.<br>
 * Long time stability is all about measuring a standard test source (counts per seconds for 
 * alpha and beta devices or Net area in counts per seconds for the peak of interest in case of 
 * gamma detector) at various time intervals (e.g. each month). The experimental records (CPS and 
 * its uncertainty) are compared to the theoretical values computed from the well-known decay law. 
 * First thing to do is to set a measurement as REFERENCE, i.e. the theoretical values equal the experimental values. 
 * For each subsequent measurements, the theoretical values are computed from REFERENCE value using the decay law. 
 * The following are displayed in table:<br>
 * measurement date <br>
 * DELTADAYS which is the time interval in days from time ZERO (REFERENCE measurement) when usually the first measurement 
 * of test standard source was made <br>
 * test source nuclide <br>
 * nuclide half life <br>
 * TV is the theoretical value (e.g. CPS) set at time ZERO <br>
 * STV3 is a quantity equal with 3 times the standard uncertainty of TV<br>
 * EV is the experimental value (e.g. CPS) measured after a time interval DELTADAYS<br>
 * SEV3 is a quantity equal with 3 times the standard uncertainty of EV<br>
 * UG is a positive value for a fixed maximum allowed uncertainty (e.g. UG=9%)<br>
 * The program computes TV+/-UG as well as EV+/-SUMS where SUMS = STV3+SEV3. ALL DATA SHOULD 
 * LIE BETWEEN RED LINES otherwise appropriate measures are required.  
 * 
 * @author Dan Fulea, 07 Jul. 2011
 * 
 */
@SuppressWarnings("serial")
public class LongTimeStability extends JFrame implements ActionListener,
		ItemListener {
	public static int ALPHAMODE = 0;
	public static int BETAMODE = 1;
	public static int GAMMAMODE = 2;
	private int MODE = 2;
	private Bang mf;
	private ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(950, 700);
	private final Dimension sizeCb = new Dimension(90, 21);
	private final Dimension tableDimension = new Dimension(900, 170);
	private final Dimension chartDimension = new Dimension(420, 300);

	private String icrpDB = "";
	private String icrpTable = "";
	private String stabilityDB = "";
	private String stabilityAlphaTable = "";
	private String stabilityBetaTable = "";
	private String stabilityGammaTable = "";
	private String stabilityTable = "";

	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());

	@SuppressWarnings("rawtypes")
	private JComboBox dbCb, nucCb;
	private Vector<String> nucV;

	private JCheckBox setAsReferenceCh;
	@SuppressWarnings("rawtypes")
	private JComboBox dayCb, monthCb;
	private JTextField yearTf = new JTextField(5);
	private JTextField ugTf = new JTextField(5);
	private JTextField valueTf = new JTextField(5);
	private JTextField svalueTf = new JTextField(5);

	private String command = "";
	private static final String ADD_COMMAND = "ADD";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String TODAY_COMMAND = "TODAY";

	private ChartPanel cp;
	private ChartPanel unccp;

	/**
	 * The connection
	 */
	private Connection stabilitydbcon = null;
	
	/**
	 * Main table primary key column name
	 */
	private String mainTablePrimaryKey = "ID";
	
	/**
	 * The column used for sorting data in main table (ORDER BY SQL syntax)
	 */
	private String orderbyS = "ID";
	
	/**
	 * The database agent associated to main table
	 */
	private DatabaseAgentSupport dbagent;
	
	private JComboBox<String> orderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Contructor.
	 * @param mf the calling program class object
	 * @param MODE the mode (alpha, beta or gamma)
	 */
	public LongTimeStability(Bang mf, int MODE) {
		this.mf = mf;
		this.resources = mf.resources;
		this.MODE = MODE;
		String titleS = "";
		//DBConnection.startDerby();
//System.out.println("trebe revazut addul ca e horror!");
		icrpDB = resources.getString("library.master.jaeri.db");
		icrpTable = resources.getString("library.master.jaeri.db.indexTable");
		stabilityDB = resources.getString("stability.db");
		stabilityAlphaTable = resources.getString("stability.table.alpha");
		stabilityBetaTable = resources.getString("stability.table.beta");
		stabilityGammaTable = resources.getString("stability.table.gamma");
		if (this.MODE == LongTimeStability.ALPHAMODE) {
			titleS = resources.getString("ALPHA.STABILITY.NAME");
			stabilityTable = stabilityAlphaTable;
		} else if (this.MODE == LongTimeStability.BETAMODE) {
			titleS = resources.getString("BETA.STABILITY.NAME");
			stabilityTable = stabilityBetaTable;
		} else if (this.MODE == LongTimeStability.GAMMAMODE) {
			titleS = resources.getString("GAMMA.STABILITY.NAME");
			stabilityTable = stabilityGammaTable;
		}
		this.setTitle(titleS);
		//================================
		mainTablePrimaryKey = "ID";
		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
		String datas = this.resources.getString("data.load");// "Data";
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		opens = opens + file_sep + stabilityDB;
		stabilitydbcon = DatabaseAgent.getConnection(opens, "", "");		
		dbagent = new DatabaseAgentSupport(stabilitydbcon, 
				mainTablePrimaryKey, stabilityTable);
		dbagent.setHasValidAIColumn(false);
		//====================================
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});

		initDBComponents();
		performQueryDb();
		createGUI();

		mf.setEnabled(false);
		today();

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

		try{
			if (stabilitydbcon != null)
				stabilitydbcon.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		mf.setEnabled(true);
		dispose();

	}

	/**
	 * Create GUI.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createGUI() {
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";

		//-------------------------
		orderbyCb = dbagent.getOrderByComboBox();
		orderbyCb.setMaximumRowCount(5);
		orderbyCb.setPreferredSize(sizeOrderCb);
		orderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(AlphaBetaAnalysis.foreColor);
		orderP.add(label);
		orderP.add(orderbyCb);
		orderP.setBackground(AlphaBetaAnalysis.bkgColor);
		label = new JLabel(resources.getString("records.count"));//"Records count: ");
		label.setForeground(AlphaBetaAnalysis.foreColor);
		orderP.add(label);
		orderP.add(dbagent.getRecordsLabel());// recordsCount);
		
		suportSp.setPreferredSize(tableDimension);
		JScrollPane scrollPane = new JScrollPane(dbagent.getMainTable());//mainTable);
		dbagent.getMainTable().setFillsViewportHeight(true);
		suportSp.add(scrollPane);
		//-----------------------------------------		
				
		setAsReferenceCh = new JCheckBox(
				resources.getString("setAsReferenceCh"));
		setAsReferenceCh.setBackground(Bang.bkgColor);
		setAsReferenceCh.setForeground(Bang.foreColor);
		setAsReferenceCh.setSelected(false);

		String[] dbS = (String[]) resources.getObject("library.master.dbCb");
		dbCb = new JComboBox(dbS);
		String defaultS = dbS[1];// JAERI
		dbCb.setSelectedItem((Object) defaultS);
		dbCb.setMaximumRowCount(5);
		dbCb.setPreferredSize(sizeCb);
		dbCb.addItemListener(this);

		// nucCb = new JComboBox();
		// nucCb.setMaximumRowCount(15);
		// nucCb.setPreferredSize(sizeCb);
		// for (int i = 0; i < nucV.size(); i++) {
		// nucCb.addItem((String) nucV.elementAt(i));
		// }

		String[] sarray = new String[31];
		for (int i = 1; i <= 31; i++) {
			if (i < 10)
				sarray[i - 1] = "0" + i;
			else
				sarray[i - 1] = Convertor.intToString(i);
		}
		dayCb = new JComboBox(sarray);
		dayCb.setMaximumRowCount(5);
		dayCb.setPreferredSize(sizeCb);

		sarray = new String[12];
		for (int i = 1; i <= 12; i++) {
			if (i < 10)
				sarray[i - 1] = "0" + i;
			else
				sarray[i - 1] = Convertor.intToString(i);
		}
		monthCb = new JComboBox(sarray);
		monthCb.setMaximumRowCount(5);
		monthCb.setPreferredSize(sizeCb);

		//suportSp.setPreferredSize(tableDimension);//done above

		JPanel p0P = new JPanel();
		p0P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p0P.setBackground(Bang.bkgColor);
		buttonName = resources.getString("stability.delete.button");
		buttonToolTip = resources.getString("stability.delete.button.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("stability.delete.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p0P.add(button);

		JPanel p1P = new JPanel();
		p1P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p1P.setBackground(Bang.bkgColor);
		// p1P.add(setAsReferenceCh);
		label = new JLabel(resources.getString("stability.nuclide.database"));
		label.setForeground(Bang.foreColor);
		p1P.add(label);
		p1P.add(dbCb);
		label = new JLabel(resources.getString("stability.nuclide"));
		label.setForeground(Bang.foreColor);
		p1P.add(label);
		p1P.add(nucCb);
		label = new JLabel(resources.getString("stability.ug"));
		label.setForeground(Bang.foreColor);
		p1P.add(label);
		p1P.add(ugTf);
		ugTf.setText("9");
		p1P.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("reference.border"), Bang.foreColor));

		buttonName = resources.getString("stability.today.button");
		buttonToolTip = resources.getString("stability.today.button.toolTip");
		buttonIconName = resources.getString("img.today");
		button = FrameUtilities.makeButton(buttonIconName, TODAY_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("stability.today.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		JPanel dateP = new JPanel();
		dateP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		dateP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("date.border"), Bang.foreColor));
		label = new JLabel(resources.getString("toolbar.day"));
		label.setForeground(Bang.foreColor);
		dateP.add(label);
		dateP.add(dayCb);
		label = new JLabel(resources.getString("toolbar.month"));
		label.setForeground(Bang.foreColor);
		dateP.add(label);
		dateP.add(monthCb);
		label = new JLabel(resources.getString("toolbar.year"));
		label.setForeground(Bang.foreColor);
		dateP.add(label);
		dateP.add(yearTf);
		dateP.add(button);
		dateP.setBackground(Bang.bkgColor);

		JPanel p2P = new JPanel();
		p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p2P.setBackground(Bang.bkgColor);
		label = new JLabel(resources.getString("stability.value"));
		label.setForeground(Bang.foreColor);
		p2P.add(label);
		p2P.add(valueTf);
		valueTf.setToolTipText(resources.getString("stability.value.tooltip"));
		label = new JLabel(resources.getString("stability.value.unc"));
		label.setForeground(Bang.foreColor);
		p2P.add(label);
		p2P.add(svalueTf);
		p2P.add(setAsReferenceCh);
		buttonName = resources.getString("stability.add.button");
		buttonToolTip = resources.getString("stability.add.button.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADD_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources
				.getObject("stability.add.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p2P.add(button);

		createEmptyCharts();// cp,unccp initialization!
		JTable mainTable = dbagent.getMainTable();//asp.getTab();###############
		if (mainTable.getRowCount() > 0) {
			createCharts();
		}
		JPanel p3P = new JPanel();
		p3P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p3P.setBackground(Bang.bkgColor);
		p3P.add(cp);
		p3P.add(unccp);

		JPanel mainP = new JPanel();
		BoxLayout blmainP = new BoxLayout(mainP, BoxLayout.Y_AXIS);
		mainP.setLayout(blmainP);
		mainP.setBackground(Bang.bkgColor);
		mainP.add(orderP);//######################################
		mainP.add(suportSp);
		mainP.add(p0P);
		mainP.add(p1P);
		mainP.add(dateP);
		mainP.add(p2P);
		mainP.add(p3P);

		JPanel content = new JPanel(new BorderLayout());
		content.add(mainP, BorderLayout.CENTER);
		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
	}

	/**
	 * Set master database for nuclides (ICRP or JAERI).
	 */
	private void setMasterTables() {
		String dbName = (String) dbCb.getSelectedItem();

		if (dbName.equals(resources.getString("library.master.db"))) {
			icrpDB = resources.getString("library.master.db");
			icrpTable = resources.getString("library.master.db.indexTable");

		} else if (dbName
				.equals(resources.getString("library.master.jaeri.db"))) {
			icrpDB = resources.getString("library.master.jaeri.db");
			icrpTable = resources
					.getString("library.master.jaeri.db.indexTable");

		}
	}

	/**
	 * Retrieve nuclide information from database.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initDBComponents() {
		nucV = new Vector<String>();
		nucCb = new JComboBox();
		nucCb.setMaximumRowCount(15);
		nucCb.setPreferredSize(sizeCb);
		// for (int i = 0; i < nucV.size(); i++) {
		// nucCb.addItem((String) nucV.elementAt(i));
		// }
		Connection conn = null;

		Statement s = null;
		ResultSet rs = null;
		try {
			String datas = resources.getString("data.load");// Data
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = icrpDB;// "ICRP38"; // the name of the database
			opens = opens + file_sep + dbName;

			// createStabilityDB();

			conn = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");

			conn.setAutoCommit(false);

			s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM " + icrpTable);

			if (rs != null)
				while (rs.next()) {
					String ss = rs.getString(2);
					nucV.addElement(ss);
					nucCb.addItem(ss);
				}

			// delete the table
			// s.execute("drop table " +
			// "icrp38Rad");

			conn.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}

			// Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}
		}

		String nucs = "";
		try {
			String datas = resources.getString("data.load");// Data
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = stabilityDB;
			opens = opens + file_sep + dbName;

			conn = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");

			s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM " + stabilityTable
					+ " WHERE ID = " + 1);
			// if first record does not exists the following are skiped, so we
			// have
			// the combobox nuclide!!
			if (rs != null)
				while (rs.next()) {// 1 record!
					String ss = rs.getString(4);// nuclide
					nucs = ss;
					nucCb.setSelectedItem(nucs);
				}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}

			// Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}
		}
	}

	/**
	 * Create gamma database and tables.
	 */
	@SuppressWarnings("unused")
	private void createStabilityDB() {

		Connection conng = null;

		String datas = resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = resources.getString("stability.db");// "Devices";
		opens = opens + file_sep + dbName;
		String protocol = "jdbc:derby:";

		Statement s = null;

		try {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			// disable log file!
			System.setProperty("derby.stream.error.method",
					"jdf.db.DBConnection.disableDerbyLogFile");

			Class.forName(driver).newInstance();

			// conng = DriverManager.getConnection(protocol + opens
			// + ";create=true", "", "");
			conng = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");
			String str = "";
			// ------------------
			conng.setAutoCommit(false);
			s = conng.createStatement();

			// delete the table
			// s.execute("drop table " +
			// "GammaEnergyCalibration");

			str = "create table " + resources.getString("deadtime.table.alpha")
					+ " ( ID integer, " + "value DOUBLE PRECISION )";

			// s.execute(str);

			str = "create table " + resources.getString("deadtime.table.beta")
					+ " ( ID integer, " + "value DOUBLE PRECISION )";

			// s.execute(str);

			str = "create table "
					+ resources.getString("stability.table.alpha")
					+ " ( ID integer, "
					+ "date VARCHAR(50), deltadays DOUBLE PRECISION, nuclide VARCHAR(50), "
					+ "halflife_sec DOUBLE PRECISION, TV DOUBLE PRECISION, STV3 DOUBLE PRECISION, "
					+ "EV DOUBLE PRECISION, SEV3 DOUBLE PRECISION, PUG DOUBLE PRECISION, "
					+ "MUG DOUBLE PRECISION, TVMUG DOUBLE PRECISION, TVPUG DOUBLE PRECISION, "
					+ "EVMSUMS DOUBLE PRECISION, EVPSUMS DOUBLE PRECISION, PSUMS DOUBLE PRECISION, "
					+ "MSUMS DOUBLE PRECISION, ERRV DOUBLE PRECISION)";

			s.execute(str);

			str = "create table "
					+ resources.getString("stability.table.beta")
					+ " ( ID integer, "
					+ "date VARCHAR(50), deltadays DOUBLE PRECISION, nuclide VARCHAR(50), "
					+ "halflife_sec DOUBLE PRECISION, TV DOUBLE PRECISION, STV3 DOUBLE PRECISION, "
					+ "EV DOUBLE PRECISION, SEV3 DOUBLE PRECISION, PUG DOUBLE PRECISION, "
					+ "MUG DOUBLE PRECISION, TVMUG DOUBLE PRECISION, TVPUG DOUBLE PRECISION, "
					+ "EVMSUMS DOUBLE PRECISION, EVPSUMS DOUBLE PRECISION, PSUMS DOUBLE PRECISION, "
					+ "MSUMS DOUBLE PRECISION, ERRV DOUBLE PRECISION)";

			s.execute(str);

			str = "create table "
					+ resources.getString("stability.table.gamma")
					+ " ( ID integer, "
					+ "date VARCHAR(50), deltadays DOUBLE PRECISION, nuclide VARCHAR(50), "
					+ "halflife_sec DOUBLE PRECISION, TV DOUBLE PRECISION, STV3 DOUBLE PRECISION, "
					+ "EV DOUBLE PRECISION, SEV3 DOUBLE PRECISION, PUG DOUBLE PRECISION, "
					+ "MUG DOUBLE PRECISION, TVMUG DOUBLE PRECISION, TVPUG DOUBLE PRECISION, "
					+ "EVMSUMS DOUBLE PRECISION, EVPSUMS DOUBLE PRECISION, PSUMS DOUBLE PRECISION, "
					+ "MSUMS DOUBLE PRECISION, ERRV DOUBLE PRECISION)";

			s.execute(str);

			conng.commit();

			// /////
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			// Connection
			try {
				if (conng != null) {
					conng.close();
					conng = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	/*private void performCurrentSelection() {
		// of course, this solution is BARBARIC!!
		// we remove a panel, perform a SELECT statement
		// and rebuild the panel containing the select result.
		// An adequate solution must skip the time-consuming
		// SELECT statement (on huge database)
		// and handle INSERT/delete or update statements at
		// database level!!! Anyway, only INSERT statement
		// could be improved since a DELETE statement often
		// requires an ID update therefore a whole database
		// table scan is required so this solution is quite
		// good enough!
		// However, we don't make art here, we make science!:P
		suportSp.remove(asp);
		performQueryDb();
		validate();
	}*/

	/**
	 * Initialize device stability database.
	 */
	private void performQueryDb() {
		dbagent.init();
		orderbyS = mainTablePrimaryKey;
		
		JTable mainTable = dbagent.getMainTable();
		// allow single selection of rows...not multiple rows!
		ListSelectionModel rowSM = mainTable.getSelectionModel();
		rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if (mainTable.getRowCount() > 0){
			//select last row!
			mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
					mainTable.getRowCount() - 1); // last ID
		}
		
		/*try {
			String datas = resources.getString("data.load");
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = stabilityDB;
			opens = opens + file_sep + dbName;

			String s = "select * from " + stabilityTable;

			Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			DBOperation.select(s, con1);

			asp = new AdvancedSelectPanel();
			suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = asp.getTab();

			// ListSelectionModel rowSM = mainTable.getSelectionModel();
			// rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1);
				// //////////
				// createCharts();
				// ///////
			} else {
				// createEmptyCharts();
			}

			if (con1 != null)
				con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * Create empty charts
	 */
	private void createEmptyCharts() {
		// /
		XYSeries series = new XYSeries(resources.getString("chart.cps.NAME"));

		XYSeries uncseries = new XYSeries(
				resources.getString("chart.cps.unc.NAME"));
		// empty series!
		// Data collection!...only one series per collection for full control!
		XYSeriesCollection data = new XYSeriesCollection(series);

		XYSeriesCollection uncdata = new XYSeriesCollection(uncseries);

		// Axis
		NumberAxis xAxis = new NumberAxis(resources.getString("chart.x.date"));
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(resources.getString("chart.y.cps"));

		NumberAxis uncxAxis = new NumberAxis(
				resources.getString("chart.x.date"));
		uncxAxis.setAutoRangeIncludesZero(false);
		NumberAxis uncyAxis = new NumberAxis(resources.getString("chart.y.proc"));

		// Renderer
		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);
		// the series index (zero-based)in Collection...always 0 as seen above!
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

		XYItemRenderer uncrenderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);
		// the series index (zero-based)in Collection...always 0 as seen above!
		uncrenderer.setSeriesPaint(0, Color.RED);
		uncrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

		// Now the plot:
		XYPlot plot = new XYPlot();
		plot.setDataset(0, data);
		plot.setDomainAxis(0, xAxis);// the axis index;axis
		plot.setRangeAxis(0, yAxis);
		plot.setRenderer(0, renderer);

		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// allow chart movement by pressing CTRL and drag with mouse!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		// //
		XYPlot uncplot = new XYPlot();
		uncplot.setDataset(0, uncdata);
		uncplot.setDomainAxis(0, uncxAxis);// the axis index;axis
		uncplot.setRangeAxis(0, uncyAxis);
		uncplot.setRenderer(0, uncrenderer);

		uncplot.setOrientation(PlotOrientation.VERTICAL);
		uncplot.setBackgroundPaint(Color.lightGray);
		uncplot.setDomainGridlinePaint(Color.white);
		uncplot.setRangeGridlinePaint(Color.white);
		// allow chart movement by pressing CTRL and drag with mouse!
		uncplot.setDomainPannable(true);
		uncplot.setRangePannable(true);

		JFreeChart chart = new JFreeChart(resources.getString("chart.NAME"),
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(Bang.bkgColor);
		//
		JFreeChart uncchart = new JFreeChart(
				resources.getString("uncchart.NAME"),
				JFreeChart.DEFAULT_TITLE_FONT, uncplot, true);
		uncchart.setBackgroundPaint(Bang.bkgColor);

		// ---------------
		cp = new ChartPanel(chart, false, true, true, false, true);
		cp.setMouseWheelEnabled(true);// mouse wheel zooming!
		cp.requestFocusInWindow();
		cp.setPreferredSize(chartDimension);
		cp.setBorder(FrameUtilities.getGroupBoxBorder("", Bang.foreColor));
		cp.setBackground(Bang.bkgColor);

		unccp = new ChartPanel(uncchart, false, true, true, false, true);
		unccp.setMouseWheelEnabled(true);// mouse wheel zooming!
		unccp.requestFocusInWindow();
		unccp.setPreferredSize(chartDimension);
		unccp.setBorder(FrameUtilities.getGroupBoxBorder("", Bang.foreColor));
		unccp.setBackground(Bang.bkgColor);
	}

	/**
	 * Create the charts related to the device long time stability
	 */
	private void createCharts() {
		cp.removeAll();
		unccp.removeAll();
		JTable mainTable = dbagent.getMainTable();//asp.getTab();
		if (mainTable.getRowCount() == 0) {
			XYSeries series = new XYSeries(
					resources.getString("chart.cps.NAME"));

			XYSeries uncseries = new XYSeries(
					resources.getString("chart.cps.unc.NAME"));
			// empty series!
			// Data collection!...only one series per collection for full
			// control!
			XYSeriesCollection data = new XYSeriesCollection(series);

			XYSeriesCollection uncdata = new XYSeriesCollection(uncseries);

			// Axis
			NumberAxis xAxis = new NumberAxis(
					resources.getString("chart.x.date"));
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis(
					resources.getString("chart.y.cps"));

			NumberAxis uncxAxis = new NumberAxis(
					resources.getString("chart.x.date"));
			uncxAxis.setAutoRangeIncludesZero(false);
			NumberAxis uncyAxis = new NumberAxis(
					resources.getString("chart.y.proc"));

			// Renderer
			XYItemRenderer renderer = new StandardXYItemRenderer(
					StandardXYItemRenderer.LINES);
			// the series index (zero-based)in Collection...always 0 as seen
			// above!
			renderer.setSeriesPaint(0, Color.RED);
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

			XYItemRenderer uncrenderer = new StandardXYItemRenderer(
					StandardXYItemRenderer.LINES);
			// the series index (zero-based)in Collection...always 0 as seen
			// above!
			uncrenderer.setSeriesPaint(0, Color.RED);
			uncrenderer
					.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

			// Now the plot:
			XYPlot plot = new XYPlot();
			plot.setDataset(0, data);
			plot.setDomainAxis(0, xAxis);// the axis index;axis
			plot.setRangeAxis(0, yAxis);
			plot.setRenderer(0, renderer);

			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			// allow chart movement by pressing CTRL and drag with mouse!
			plot.setDomainPannable(true);
			plot.setRangePannable(true);

			// //
			XYPlot uncplot = new XYPlot();
			uncplot.setDataset(0, uncdata);
			uncplot.setDomainAxis(0, uncxAxis);// the axis index;axis
			uncplot.setRangeAxis(0, uncyAxis);
			uncplot.setRenderer(0, uncrenderer);

			uncplot.setOrientation(PlotOrientation.VERTICAL);
			uncplot.setBackgroundPaint(Color.lightGray);
			uncplot.setDomainGridlinePaint(Color.white);
			uncplot.setRangeGridlinePaint(Color.white);
			// allow chart movement by pressing CTRL and drag with mouse!
			uncplot.setDomainPannable(true);
			uncplot.setRangePannable(true);

			JFreeChart chart = new JFreeChart(
					resources.getString("chart.NAME"),
					JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(Bang.bkgColor);
			//
			JFreeChart uncchart = new JFreeChart(
					resources.getString("uncchart.NAME"),
					JFreeChart.DEFAULT_TITLE_FONT, uncplot, true);
			uncchart.setBackgroundPaint(Bang.bkgColor);

			cp.setChart(chart);
			unccp.setChart(uncchart);
			return;
		}
		// ===if here we have charts;
		XYSeries tvseries = new XYSeries(resources.getString("chart.tvseries.NAME"));
		XYSeries evseries = new XYSeries(resources.getString("chart.evseries.NAME"));
		XYSeries tvmugseries = new XYSeries(resources.getString("chart.tvmugseries.NAME"));
		XYSeries tvpugseries = new XYSeries(resources.getString("chart.tvpugseries.NAME"));
		XYSeries evmsumsseries = new XYSeries(resources.getString("chart.evmsumsseries.NAME"));
		XYSeries evpsumsseries = new XYSeries(resources.getString("chart.evpsumsseries.NAME"));
		
		XYSeries uncseries = new XYSeries(resources.getString("chart.uncseries.NAME"));
		XYSeries mugseries = new XYSeries(resources.getString("chart.mugseries.NAME"));
		XYSeries pugseries = new XYSeries(resources.getString("chart.pugseries.NAME"));
		XYSeries msumsseries = new XYSeries(resources.getString("chart.msumsseries.NAME"));
		XYSeries psumsseries = new XYSeries(resources.getString("chart.psumsseries.NAME"));
		
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		try {
			//String datas = resources.getString("data.load");// Data
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = stabilityDB;
			///opens = opens + file_sep + dbName;

			//conn = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");

			s = stabilitydbcon.createStatement();//conn.createStatement();
			// ----------			
			rs = s.executeQuery("SELECT * FROM " + stabilityTable);
			if (rs != null) {// from strange reasons rs is never null!!
				while (rs.next()) {
					double ug=Convertor.stringToDouble(rs.getString(10));//const
					double deltadays=Convertor.stringToDouble(rs.getString(3));
					double tv=Convertor.stringToDouble(rs.getString(6));
					double stv=Convertor.stringToDouble(rs.getString(7));
					double ev=Convertor.stringToDouble(rs.getString(8));
					double sev=Convertor.stringToDouble(rs.getString(9));
					double sums=100.0*Math.abs(stv+sev)/ev;
					double unc=100.0*Math.abs(tv-ev)/tv;
					
					tvseries.add(deltadays, tv);
					evseries.add(deltadays, ev);
					tvmugseries.add(deltadays, tv-tv*ug/100.0);
					tvpugseries.add(deltadays, tv+tv*ug/100.0);
					evmsumsseries.add(deltadays, ev-stv-sev);
					evpsumsseries.add(deltadays, ev+stv+sev);
					
					uncseries.add(deltadays, unc);
					mugseries.add(deltadays, -ug);
					pugseries.add(deltadays, ug);
					msumsseries.add(deltadays, -sums);
					psumsseries.add(deltadays, sums);
				}
			}
			//=========
			XYSeriesCollection data = new XYSeriesCollection(tvseries);
			data.addSeries(evseries);
			data.addSeries(tvmugseries);
			data.addSeries(tvpugseries);
			data.addSeries(evmsumsseries);
			data.addSeries(evpsumsseries);
			
			XYSeriesCollection uncdata = new XYSeriesCollection(uncseries);
			uncdata.addSeries(mugseries);
			uncdata.addSeries(pugseries);
			uncdata.addSeries(msumsseries);
			uncdata.addSeries(psumsseries);
			
			// Renderer
			XYItemRenderer renderer = new StandardXYItemRenderer(
					StandardXYItemRenderer.LINES);
			// the series index (zero-based)in Collection...always 0 as seen above!
			renderer.setSeriesPaint(0, Color.BLUE);
			renderer.setSeriesPaint(1, Color.BLACK);
			renderer.setSeriesPaint(2, Color.RED);
			renderer.setSeriesPaint(3, Color.RED);
			renderer.setSeriesPaint(4, Color.GREEN);
			renderer.setSeriesPaint(5, Color.GREEN);
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			
			XYItemRenderer uncrenderer = new StandardXYItemRenderer(
					StandardXYItemRenderer.LINES);
			// the series index (zero-based)in Collection...always 0 as seen above!
			uncrenderer.setSeriesPaint(0, Color.BLACK);
			uncrenderer.setSeriesPaint(1, Color.RED);
			uncrenderer.setSeriesPaint(2, Color.RED);
			uncrenderer.setSeriesPaint(3, Color.GREEN);
			uncrenderer.setSeriesPaint(4, Color.GREEN);
			uncrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			
			// Axis
			NumberAxis xAxis = new NumberAxis(
					resources.getString("chart.x.date"));
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis(
					resources.getString("chart.y.cps"));
			
			NumberAxis uncxAxis = new NumberAxis(
					resources.getString("chart.x.date"));
			uncxAxis.setAutoRangeIncludesZero(false);
			NumberAxis uncyAxis = new NumberAxis(
					resources.getString("chart.y.proc"));
			//plot
			XYPlot plot = new XYPlot();
			plot.setDataset(0, data);
			plot.setDomainAxis(0, xAxis);// the axis index;axis
			plot.setRangeAxis(0, yAxis);
			plot.setRenderer(0, renderer);

			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			// allow chart movement by pressing CTRL and drag with mouse!
			plot.setDomainPannable(true);
			plot.setRangePannable(true);
			
			XYPlot uncplot = new XYPlot();
			uncplot.setDataset(0, uncdata);
			uncplot.setDomainAxis(0, uncxAxis);// the axis index;axis
			uncplot.setRangeAxis(0, uncyAxis);
			uncplot.setRenderer(0, uncrenderer);

			uncplot.setOrientation(PlotOrientation.VERTICAL);
			uncplot.setBackgroundPaint(Color.lightGray);
			uncplot.setDomainGridlinePaint(Color.white);
			uncplot.setRangeGridlinePaint(Color.white);
			// allow chart movement by pressing CTRL and drag with mouse!
			uncplot.setDomainPannable(true);
			uncplot.setRangePannable(true);
			
			JFreeChart chart = new JFreeChart(
					resources.getString("chart.NAME"),
					JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(Bang.bkgColor);
			cp.setChart(chart);
			
			JFreeChart uncchart = new JFreeChart(
					resources.getString("uncchart.NAME"),
					JFreeChart.DEFAULT_TITLE_FONT, uncplot, true);
			uncchart.setBackgroundPaint(Bang.bkgColor);			
			unccp.setChart(uncchart);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return;
			}

			// Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return;
			}
		}
		// ==============

	}

	/**
	 * Most actions are set here
	 * @param arg0 arg0
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (command.equals(TODAY_COMMAND)) {
			today();
		} else if (command.equals(ADD_COMMAND)) {
			add();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		}
	}

	/**
	 * Sorts data from main table
	 */
	private void sort() {
		orderbyS = (String) orderbyCb.getSelectedItem();
		// performSelection();
		dbagent.performSelection(orderbyS);
	}
	
	/**
	 * JCombobox related actions are set here
	 */
	public void itemStateChanged(ItemEvent ie) {
		if (ie.getSource() == dbCb) {
			fetchNuclideBaseInfo();
		} else if (ie.getSource() == orderbyCb) {
			sort();
		} 
	}

	@SuppressWarnings("unchecked")
	/**
	 * Retrieve basic information of nuclides from the database
	 */
	private void fetchNuclideBaseInfo() {

		nucV = new Vector<String>();

		setMasterTables();

		Connection conn12 = null;

		Statement s = null;
		ResultSet rs = null;
		try {
			String datas = resources.getString("data.load");// Data
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = icrpDB;
			opens = opens + file_sep + dbName;

			conn12 = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");

			s = conn12.createStatement();

			rs = s.executeQuery("SELECT * FROM " + icrpTable);

			nucCb.removeAllItems();

			if (rs != null)
				while (rs.next()) {
					String ss = rs.getString(2);
					nucV.addElement(ss);

					nucCb.addItem(ss);
				}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}

			// Connection
			try {
				if (conn12 != null) {
					conn12.close();
					conn12 = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}
		}
	}

	/**
	 * Set measurement date as today.
	 */
	private void today() {
		String s = null;
		//TimeUtilities.today();
		TimeUtilities todayTu = new TimeUtilities();
		s = Convertor.intToString(todayTu.getDay());//TimeUtilities.iday);
		if (todayTu.getDay() < 10)//if (TimeUtilities.iday < 10)
			s = "0" + s;
		dayCb.setSelectedItem((Object) s);
		s = Convertor.intToString(todayTu.getMonth());//TimeUtilities.imonth);
		if (todayTu.getMonth() < 10)//if (TimeUtilities.imonth < 10)
			s = "0" + s;
		monthCb.setSelectedItem((Object) s);
		s = Convertor.intToString(todayTu.getYear());//TimeUtilities.iyear);
		yearTf.setText(s);
	}

	/**
	 * Convert nuclide half life to seconds.
	 * @param hl given half life
	 * @param hlu half life unit
	 * @return the result.
	 */
	public static double formatHalfLife(double hl, String hlu) {
		double result = hl;// return this value if hlu=seconds!!
		if (hlu.equals("y")) {
			result = hl * 365.25 * 24.0 * 3600.0;
		} else if (hlu.equals("d")) {
			result = hl * 24.0 * 3600.0;
		} else if (hlu.equals("h")) {
			result = hl * 3600.0;
		} else if (hlu.equals("m")) {
			result = hl * 60.0;
		} else if (hlu.equals("ms")) {
			result = hl / 1000.0;
		} else if (hlu.equals("us")) {
			result = hl / 1000000.0;
		}

		return result;
	}

	@SuppressWarnings("resource")
	/**
	 * Add a measurement entry. Can be new entry or a reference entry.
	 */
	private void add() {
		boolean mustSetReference = false;

		JTable mainTable = dbagent.getMainTable();//asp.getTab();
		int recordCount = mainTable.getRowCount();//this is for arrays!!!!
		
		int d0 = 0;
		int d = 0;
		int m0 = 0;
		int m = 0;
		int y0 = 0;
		int y = 0;

		double delta = 0;

		double hl = 0.0;
		String hlu = "";

		String nucs = (String) nucCb.getSelectedItem();// initialization

		double ug = 0.0;
		double value = 0.0;
		double svalue = 0.0;
		double value0 = 0.0;
		double svalue0 = 0.0;
		boolean nulneg = false;
		try {
			// d0 = Convertor.stringToInt((String) day0Cb.getSelectedItem());
			d = Convertor.stringToInt((String) dayCb.getSelectedItem());
			// m0 = Convertor.stringToInt((String) month0Cb.getSelectedItem());
			m = Convertor.stringToInt((String) monthCb.getSelectedItem());
			// y0 = Convertor.stringToInt((String) year0Tf.getText());
			y = Convertor.stringToInt((String) yearTf.getText());
			if (d <= 0)
				nulneg = true;
			if (m <= 0)
				nulneg = true;
			if (y <= 0)
				nulneg = true;

			ug = Convertor.stringToDouble(ugTf.getText());
			if (ug <= 0)
				nulneg = true;
			value = Convertor.stringToDouble(valueTf.getText());
			if (value <= 0)
				nulneg = true;
			svalue = Convertor.stringToDouble(svalueTf.getText());
			if (svalue <= 0)
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
		// =========================================
		String measurementDate0 = "";

		//TimeUtilities.setDate(d, m, y);
		//String measurementDate = TimeUtilities.formatDate();
		TimeUtilities tu = new TimeUtilities(d, m, y);
		String measurementDate = tu.formatDate();
		// measurementActivity = PhysUtilities.decayLaw(ac, hlsec, d0, m0, y0,
		// d, m, y);

		setMasterTables();
		Connection conn = null;

		Statement s = null;
		ResultSet rs = null;

		// =========NUCLIDE=========================================
		String[] arrayMeasDate = new String[recordCount];
		double[] tv0 = new double[recordCount];
		double[] stv0 = new double[recordCount];
		double[] ev = new double[recordCount];
		double[] sev = new double[recordCount];
		// ========TEST IF WE ALREADY HAVE A NUCLIDE REFERENCE:======
		try {
			//String datas = resources.getString("data.load");// Data
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = stabilityDB;
			//opens = opens + file_sep + dbName;

			//conn = DBConnection.getDerbyConnection(opens, "", "");

			s = stabilitydbcon.createStatement();//conn.createStatement();
			// ----------
			/*int ii = 0;
			rs = s.executeQuery("SELECT * FROM " + stabilityTable);
			if (rs != null) {// from strange reasons rs is never null!!
				while (rs.next()) {// 1 record!
					arrayMeasDate[ii] = rs.getString(2);
					tv0[ii] = Convertor.stringToDouble(rs.getString(6));
					stv0[ii] = Convertor.stringToDouble(rs.getString(7));
					ev[ii] = Convertor.stringToDouble(rs.getString(8));
					sev[ii] = Convertor.stringToDouble(rs.getString(9));
					ii++;
				}
			}
			// -----------
			if (recordCount == 0)
				mustSetReference = true;
			// ---------
			*/
			rs = s.executeQuery("SELECT * FROM " + stabilityTable
					+ " WHERE ID = " + 1);//rs.afterLast();
			// if first record does not exists the following are skiped, so we
			// have
			// the combobox nuclide!!
			if (rs != null) {
				// System.out.println("enter");
				while (rs.next()) {// 1 record!
					String ss = "";
					// set nuclide only if it not set as reference!
					if (!setAsReferenceCh.isSelected()) {
						ss = rs.getString(4);// nuclide
						nucs = ss;

						ss = rs.getString(10);// ug
						ug = Convertor.stringToDouble(ss);

					} else {
						mustSetReference = true;
					}

					ss = rs.getString(2);// measurement date
					measurementDate0 = ss;
					//TimeUtilities.unformatDate(measurementDate0);
					TimeUtilities tu2 = new TimeUtilities(measurementDate0);
					d0 = tu2.getDay();//TimeUtilities.iday;
					m0 = tu2.getMonth();//TimeUtilities.imonth;
					y0 = tu2.getYear();//TimeUtilities.iyear;

					Calendar cal0 = Calendar.getInstance();
					cal0.set(y0, m0, d0);
					Date dt0 = cal0.getTime();

					Calendar cal = Calendar.getInstance();
					cal.set(y, m, d);
					Date dt = cal.getTime();
					long delt = dt.getTime();
					delt = delt - dt0.getTime();
					delta = delt;
					delta = delta / 1000.0;// milisec->sec
					delta = delta / 3600.0; // sec->h
					delta = delta / 24.0;// h->days
				}
			} else {
				mustSetReference = true;
				// no delta
			}
			
			int ii = 0;
			rs = s.executeQuery("SELECT * FROM " + stabilityTable);
			if (rs != null) {// from strange reasons rs is never null!!
				while (rs.next()) {// 1 record!
					arrayMeasDate[ii] = rs.getString(2);
					tv0[ii] = Convertor.stringToDouble(rs.getString(6));
					stv0[ii] = Convertor.stringToDouble(rs.getString(7));
					ev[ii] = Convertor.stringToDouble(rs.getString(8));
					sev[ii] = Convertor.stringToDouble(rs.getString(9));
					
			//System.out.println(arrayMeasDate[ii]);						
					ii++;				
				}
			}
			//rs.afterLast();
			// -----------
			if (recordCount == 0)
				mustSetReference = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return;
			}

			// Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return;
			}
		}
		// ===========GETTING NUCLIDE INFORMATION
		try {
			String datas = resources.getString("data.load");// Data
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = icrpDB;// "ICRP38"; // the name of the database
			opens = opens + file_sep + dbName;

			conn = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");

			// conn.setAutoCommit(false);

			s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM " + icrpTable
					+ " WHERE NUCLIDE = " + "'" + nucs + "'");

			if (rs != null)
				while (rs.next()) {// 1 record!
					String ss = rs.getString(3);
					hl = Convertor.stringToDouble(ss);
					ss = rs.getString(4);
					hlu = ss;
				}

			// conn.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return;
			}

			// Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return;
			}
		}
		double hlsec = formatHalfLife(hl, hlu);
		// System.out.println("t12 sec= "+hlsec);
		// ============END NUCLIDE INFORMATION
		try {
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = stabilityDB;
			//opens = opens + file_sep + dbName;
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			PreparedStatement psUpdate = null;

			if (mustSetReference) {
				if (recordCount == 0) {
					// insert
					psInsert = stabilitydbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
							+ stabilityTable + " values " + "(?, ?, ?, ?, ?, "
							+ "?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, "
							+ "?, ?, ?)");
					//we expect few entires (<100) it is ok to be in order!!!
					int id = recordCount + 1;//dbagent.getAIPrimaryKeyValue()+1;//recordCount + 1;//
					double deltaDays = 0.0;
					psInsert.setString(1, Convertor.intToString(id));//
					psInsert.setString(2, measurementDate);
					psInsert.setString(3, Convertor.doubleToString(deltaDays));
					psInsert.setString(4, nucs);
					psInsert.setString(5, Convertor.doubleToString(hlsec));
					psInsert.setString(6, Convertor.doubleToString(value));
					psInsert.setString(7,
							Convertor.doubleToString(3.0 * svalue));
					psInsert.setString(8, Convertor.doubleToString(value));
					psInsert.setString(9,
							Convertor.doubleToString(3.0 * svalue));
					psInsert.setString(10, Convertor.doubleToString(ug));
					psInsert.setString(11, Convertor.doubleToString(-ug));
					psInsert.setString(12, Convertor.doubleToString(value - ug
							* value / 100.0));
					psInsert.setString(13, Convertor.doubleToString(value + ug
							* value / 100.0));
					psInsert.setString(14,
							Convertor.doubleToString(value - 6.0 * svalue));
					psInsert.setString(15,
							Convertor.doubleToString(value + 6.0 * svalue));
					psInsert.setString(16,
							Convertor.doubleToString(6.0 * svalue));
					psInsert.setString(17,
							Convertor.doubleToString(-6.0 * svalue));
					psInsert.setString(18, Convertor.doubleToString(0.0));
					// =abs(value-value0) x 100 / value0
					psInsert.executeUpdate();

				} else {
					// update
					// update first row
					double deltaDays = 0.0;
					psUpdate = stabilitydbcon.prepareStatement("update " + stabilityTable//con1.prepareStatement("update " + stabilityTable
							+ " set DATE=?, DELTADAYS=?, "
							+ "NUCLIDE=?, HALFLIFE_SEC=?, TV=?, STV3=?, "
							+ "EV=?, SEV3=?, PUG=?, MUG=?, TVMUG=?, TVPUG=?, "
							+ "EVMSUMS=?, EVPSUMS=?, PSUMS=?, MSUMS=?, "
							+ "ERRV=? where ID=1");
					psUpdate.setString(1, measurementDate);
					psUpdate.setString(2, Convertor.doubleToString(deltaDays));
					psUpdate.setString(3, nucs);
					psUpdate.setString(4, Convertor.doubleToString(hlsec));
					psUpdate.setString(5, Convertor.doubleToString(value));
					psUpdate.setString(6,
							Convertor.doubleToString(3.0 * svalue));
					psUpdate.setString(7, Convertor.doubleToString(value));
					psUpdate.setString(8,
							Convertor.doubleToString(3.0 * svalue));
					psUpdate.setString(9, Convertor.doubleToString(ug));
					psUpdate.setString(10, Convertor.doubleToString(-ug));
					psUpdate.setString(11, Convertor.doubleToString(value - ug
							* value / 100.0));
					psUpdate.setString(12, Convertor.doubleToString(value + ug
							* value / 100.0));
					psUpdate.setString(13,
							Convertor.doubleToString(value - 6.0 * svalue));
					psUpdate.setString(14,
							Convertor.doubleToString(value + 6.0 * svalue));
					psUpdate.setString(15,
							Convertor.doubleToString(6.0 * svalue));
					psUpdate.setString(16,
							Convertor.doubleToString(-6.0 * svalue));
					psUpdate.setString(17, Convertor.doubleToString(0.0));

					psUpdate.executeUpdate();
					// now the rest:
					for (int i = 1; i < recordCount; i++) {
						// update value!!
						String measDate = arrayMeasDate[i];
						//TimeUtilities.unformatDate(measDate);
						TimeUtilities tu3 = new TimeUtilities(measDate);
						d0 = tu3.getDay();//TimeUtilities.iday;
						m0 = tu3.getMonth();//TimeUtilities.imonth;
						y0 = tu3.getYear();//TimeUtilities.iyear;

						Calendar cal0 = Calendar.getInstance();
						cal0.set(y0, m0, d0);
						Date dt0 = cal0.getTime();

						Calendar cal = Calendar.getInstance();
						cal.set(y, m, d);
						Date dt = cal.getTime();
						long delt = dt.getTime();
						delt = delt - dt0.getTime();
						delta = delt;
						delta = delta / 1000.0;// milisec->sec
						delta = delta / 3600.0; // sec->h
						delta = delta / 24.0;// h->days
						
						delta=-delta;//!!here y,m,d is set as referennce so
						//delta is computed backwards!!!
						//and value ans svalue is just set!!!!
						value0 = PhysUtilities.decayLaw(value, hlsec, d, m, y,d0, m0,
								y0 );//backwards
						svalue0 = PhysUtilities.decayLaw(svalue, hlsec, d, m, y, d0,
								m0, y0);
						double value1 = ev[i];
						double svalue1 = sev[i]/3.0;//sev is 3 sev!!
						double err = Math.abs(value1 - value0) * 100.0 / value0;

						psUpdate = stabilitydbcon//con1
								.prepareStatement("update "
										+ stabilityTable
										+ " set DELTADAYS=?, "
										+ "NUCLIDE=?, HALFLIFE_SEC=?, TV=?, STV3=?, "
										+ "EV=?, SEV3=?, PUG=?, MUG=?, TVMUG=?, TVPUG=?, "
										+ "EVMSUMS=?, EVPSUMS=?, PSUMS=?, MSUMS=?, "
										+ "ERRV=? where ID=?");

						psUpdate.setString(1, Convertor.doubleToString(delta));
						psUpdate.setString(2, nucs);
						psUpdate.setString(3, Convertor.doubleToString(hlsec));
						psUpdate.setString(4, Convertor.doubleToString(value0));
						psUpdate.setString(5,
								Convertor.doubleToString(3.0 * svalue0));
						psUpdate.setString(6, Convertor.doubleToString(value1));
						psUpdate.setString(7,
								Convertor.doubleToString(3.0 * svalue1));
						psUpdate.setString(8, Convertor.doubleToString(ug));
						psUpdate.setString(9, Convertor.doubleToString(-ug));
						psUpdate.setString(
								10,
								Convertor.doubleToString(value0 - ug * value0
										/ 100.0));
						psUpdate.setString(
								11,
								Convertor.doubleToString(value0 + ug * value0
										/ 100.0));
						psUpdate.setString(
								12,
								Convertor.doubleToString(value1 - 3.0*svalue0
										- 3.0*svalue1));
						psUpdate.setString(
								13,
								Convertor.doubleToString(value1 + 3.0*svalue0
										+ 3.0*svalue1));
						psUpdate.setString(14,
								Convertor.doubleToString(3.0*svalue0 + 3.0*svalue1));
						psUpdate.setString(15,
								Convertor.doubleToString(-3.0*svalue0 - 3.0*svalue1));
						psUpdate.setString(16, Convertor.doubleToString(err));

						psUpdate.setString(17, Convertor.intToString(i + 1));

						psUpdate.executeUpdate();
					}
				}
			} else {
				// insert, and have delta!!!
				value0 = PhysUtilities.decayLaw(tv0[0], hlsec, d0, m0, y0, d,
						m, y);
				svalue0 = PhysUtilities.decayLaw(stv0[0], hlsec, d0, m0, y0, d,
						m, y)/3.0;//stv is in fact 3 stv
				double err = Math.abs(value - value0) * 100.0 / value0;

				psInsert = stabilitydbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
						+ stabilityTable + " values " + "(?, ?, ?, ?, ?, "
						+ "?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, " + "?, ?, ?)");
				//few records..it is ok to always be in order 1,2,...
				int id = recordCount + 1;//dbagent.getAIPrimaryKeyValue()+1;//recordCount + 1;//
				psInsert.setString(1, Convertor.intToString(id));//
				psInsert.setString(2, measurementDate);
				psInsert.setString(3, Convertor.doubleToString(delta));
				psInsert.setString(4, nucs);
				psInsert.setString(5, Convertor.doubleToString(hlsec));
				psInsert.setString(6, Convertor.doubleToString(value0));
				psInsert.setString(7, Convertor.doubleToString(3.0 * svalue0));
				psInsert.setString(8, Convertor.doubleToString(value));
				psInsert.setString(9, Convertor.doubleToString(3.0 * svalue));
				psInsert.setString(10, Convertor.doubleToString(ug));
				psInsert.setString(11, Convertor.doubleToString(-ug));
				psInsert.setString(12,
						Convertor.doubleToString(value0 - ug * value0 / 100.0));
				psInsert.setString(13,
						Convertor.doubleToString(value0 + ug * value0 / 100.0));
				psInsert.setString(14,
						Convertor.doubleToString(value - 3.0*svalue0 - 3.0*svalue));
				psInsert.setString(15,
						Convertor.doubleToString(value + 3.0*svalue0 + 3.0*svalue));
				psInsert.setString(16,
						Convertor.doubleToString(3.0*svalue0 + 3.0*svalue));
				psInsert.setString(17,
						Convertor.doubleToString(-3.0*svalue0 - 3.0*svalue));
				psInsert.setString(18, Convertor.doubleToString(err));
				// =abs(value-value0) x 100 / value0
				psInsert.executeUpdate();

			}

			if (psInsert != null)
				psInsert.close();
			if (psUpdate != null)
				psUpdate.close();
			//if (con1 != null)
				//con1.close();

			//performCurrentSelection();
			dbagent.performSelection(orderbyS);
			createCharts();

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

	}

	/**
	 * Mark rows to be deleted and row IDs to be updated. Internally used by delete.
	 * @param rows rows
	 * @return the result
	 */
	private String[][] createTableSortableIds(int[] rows) {
		int length = 0;

		JTable aspTable = dbagent.getMainTable();//asp.getTab();
		int rowTableCount = aspTable.getRowCount();
		length = rowTableCount - rows[0];//masterpiece

		length = length + 1;// de la selrowsclone[0] la tableRecordCount

		String[][] stsi = new String[length][3];// simulez tabela
		int k = 0;
		for (int i = 0; i < stsi.length; i++)
			for (int j = 0; j < rows.length; j++) {
				if (i + rows[0] == rows[j]) {
					stsi[i][0] = "M";// de la Muie==delete
					stsi[i][1] = Convertor.intToString(rows[j]);
					stsi[i][2] = Convertor.intToString(rows[j]);
					k++;// inregistreaza saltul!!!
					break;
				}
				// nu s-a gasit:
				if (j == rows.length - 1) {
					stsi[i][0] = Convertor.intToString(i + rows[0]);
					stsi[i][1] = Convertor.intToString(i + rows[0] - k);// se va
																		// updata
					stsi[i][2] = Convertor.intToString(i + rows[0]);// se va
																	// updata
				}
			}

		return stsi;
	}

	/**
	 * Delete one or more entries!
	 */
	private void delete() {

		try {
			// prepare db query data
			String datas = resources.getString("data.load");
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = stabilityDB;
			opens = opens + file_sep + dbName;
			// make a connection

			JTable aspTable = dbagent.getMainTable();//asp.getTab();
			// int rowTableCount = aspTable.getRowCount();// =MAX ID!!
			// int selID = 0;// NO ZERO ID
			// int selRow = aspTable.getSelectedRow();
			// if (selRow != -1) {
			// selID = (Integer) aspTable.getValueAt(selRow, 0);
			// }

			int[] selRows = aspTable.getSelectedRows();
			if (selRows == null) {
				return;
			}
			if (selRows.length == 0) {
				return;
			}

			// determin sirul id-urilor selectate!!!!
			int[] selIds = new int[selRows.length];
			for (int i = 0; i < selIds.length; i++) {
				selIds[i] = (Integer) aspTable.getValueAt(selRows[i], 0);
			}
			// sortez sirul crescator
			int[] selIdsClone = Sort.newQSortInt(selIds);
			// am sirul selectiilor candidate la stergere!!!!
			// creez simularea tabelului sortat dupa id!!!
			String[][] spTableNew = createTableSortableIds(selIdsClone);
			// for (int i=0;i<spTableNew.length;i++){
			// System.out.println(spTableNew[i][0]+" "+spTableNew[i][1]+" "+spTableNew[i][2]);
			// }
			// ==========================
			Connection con1 = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");
			Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = s.executeQuery("SELECT * FROM " + stabilityTable);// +" ORDER BY ID");

			while (res.next()) {
				int id = res.getInt("ID");
				// if (id == selID) {
				// res.deleteRow();
				// }
				// --------
				for (int i = 0; i < spTableNew.length; i++) {
					if (spTableNew[i][0].compareTo("M") == 0) {
						// delete
						String ids = spTableNew[i][2];// where id = ids!!
						int selIDe = Convertor.stringToInt(ids);
						if (id == selIDe) {
							res.deleteRow();
						}
					}
				}
			}
			// ============now update:
			PreparedStatement psUpdate = null;
			psUpdate = con1.prepareStatement("update " + stabilityTable
					+ " set ID=? where ID=?");
			for (int i = 0; i < spTableNew.length; i++) {
				if (spTableNew[i][0].compareTo("M") != 0) {
					psUpdate.setString(1, spTableNew[i][1]);
					psUpdate.setString(2, spTableNew[i][2]);
					psUpdate.executeUpdate();
				}
			}
			// =================
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			if (psUpdate != null)
				psUpdate.close();
			if (con1 != null)
				con1.close();

			//performCurrentSelection();
			dbagent.performSelection(orderbyS);
			createCharts();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
