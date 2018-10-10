package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import danfulea.math.Convertor;
import danfulea.math.Sort;
import danfulea.math.numerical.ModelingData;
import danfulea.utils.FrameUtilities;
import danfulea.utils.ListUtilities;

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

/**
 * Class designed to study the self absorbtion effect for detectors. It is useful to 
 * estimate the MINIMUM sample mass to be measured in order to obtain the MAXIMUM efficiency 
 * as well as reducing the self-absorbtion effect (for alpha and beta detectors). The evaluation is 
 * quickly performed using the cross value of two linear functions computed by chi-fit numerical method. 
 * 
 * @author Dan Fulea, 23 OCT. 2011
 * 
 */
@SuppressWarnings("serial")
public class SelfAbsorptionFrame extends JFrame implements ActionListener{
	private Bang mf;
	private ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(900, 700);
	private static final Dimension sizeLst = new Dimension(450, 200);
	private final Dimension chartDimension = new Dimension(450, 300);
	@SuppressWarnings("rawtypes")
	private JList dataList;
	private JScrollPane listSp;
	@SuppressWarnings("rawtypes")
	private DefaultListModel dlm = new DefaultListModel();
	private ChartPanel cp;
	private JTextField massTf=new JTextField(5);
	private JTextField effTf=new JTextField(5);
	private JTextField massOptimTf=new JTextField(15);
	private JTextField effOptimTf=new JTextField(15);
	
	private String command = "";
	private static final String ADD_COMMAND = "ADD";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String COMPUTE_COMMAND = "COMPUTE";
	
	private int nDataList = 0;
	private Vector<Double> massV,effV;
	private double afit_ascend=0.0;
	private double bfit_ascend=0.0;
	private double afit_lin=0.0;
	private double bfit_lin=0.0;
	private double xendascend=0.0;
	private double xstartlin=0.0;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * Constructor.
	 * @param mf the calling program, Bang object
	 */
	public SelfAbsorptionFrame(Bang mf){
		this.mf = mf;
		this.resources = mf.resources;
		String titleS = resources.getString("SelfAbsorption.NAME");
		
		this.setTitle(titleS);

		dataList = new JList(dlm);
		massV = new Vector<Double>();
		effV = new Vector<Double>();
		
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
		
		JPanel p2P = new JPanel();
		p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("self.massTf"));
		label.setForeground(Bang.foreColor);
		p2P.add(label);
		p2P.add(massTf);
		label = new JLabel(resources.getString("self.effTf"));
		label.setForeground(Bang.foreColor);
		p2P.add(label);
		p2P.add(effTf);
		buttonName = resources.getString("self.add.button");
		buttonToolTip = resources.getString("self.add.button.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADD_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("self.add.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p2P.add(button);
		p2P.setBackground(Bang.bkgColor);
		
		listSp = new JScrollPane(dataList);
		listSp.setPreferredSize(sizeLst);// --!!!--for resising well
		JPanel listP = new JPanel(new BorderLayout());
		listP.add(listSp, BorderLayout.CENTER);
		listP.setBackground(Bang.bkgColor);
		
		JPanel listButP = new JPanel();
		BoxLayout bllistButP = new BoxLayout(listButP, BoxLayout.Y_AXIS);
		listButP.setLayout(bllistButP);
		buttonName = resources.getString("self.delete.button");
		buttonToolTip = resources.getString("self.delete.button.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("self.delete.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		listButP.add(button);
		listButP.add(Box.createRigidArea(new Dimension(0, 10)));// some space
		buttonName = resources.getString("self.compute.button");
		buttonToolTip = resources.getString("self.compute.button");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("self.compute.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		listButP.add(button);
		listButP.setBackground(Bang.bkgColor);
		
		JPanel p3P = new JPanel();
		p3P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p3P.add(listP);
		p3P.add(listButP);
		p3P.setBackground(Bang.bkgColor);
		
		createEmptyCharts();
		
		JPanel chartP = new JPanel();
		chartP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		chartP.setBackground(Bang.bkgColor);
		chartP.add(cp);
		

		JPanel p22P = new JPanel();
		p22P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("self.optimum.massTf"));
		label.setForeground(Bang.foreColor);
		p22P.add(label);
		p22P.add(massOptimTf);
		label = new JLabel(resources.getString("self.optimum.effTf"));
		label.setForeground(Bang.foreColor);
		p22P.add(label);
		p22P.add(effOptimTf);
		p22P.setBackground(Bang.bkgColor);
		
		JPanel infoBoxP = new JPanel();
		BoxLayout bl03 = new BoxLayout(infoBoxP, BoxLayout.Y_AXIS);
		infoBoxP.setLayout(bl03);
		infoBoxP.add(p2P);
		infoBoxP.add(p3P);
		infoBoxP.add(chartP);
		infoBoxP.add(p22P);
		infoBoxP.setBackground(Bang.bkgColor);
		
		JPanel content = new JPanel(new BorderLayout());
		//content.add(mainP, BorderLayout.CENTER);
		content.add(infoBoxP, BorderLayout.CENTER);
		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
	}
	
	/**
	 * Create an empty chart
	 */
	private void createEmptyCharts() {
		// /
		XYSeries series = new XYSeries(resources.getString("chart.cps.NAME"));

		// empty series!
		// Data collection!...only one series per collection for full control!
		XYSeriesCollection data = new XYSeriesCollection(series);

		
		// Axis
		NumberAxis xAxis = new NumberAxis(resources.getString("self.chart.x"));
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(resources.getString("self.chart.y"));
		
		// Renderer
		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);
		// the series index (zero-based)in Collection...always 0 as seen above!
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

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
		
		JFreeChart chart = new JFreeChart(resources.getString("self.chart.NAME"),
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(Bang.bkgColor);
		// ---------------
		cp = new ChartPanel(chart, false, true, true, false, true);
		cp.setMouseWheelEnabled(true);// mouse wheel zooming!
		cp.requestFocusInWindow();
		cp.setPreferredSize(chartDimension);
		cp.setBorder(FrameUtilities.getGroupBoxBorder("", Bang.foreColor));
		cp.setBackground(Bang.bkgColor);		
	}
	
	/**
	 * Create the self-absorbtion chart
	 */
	private void createChart(){
		cp.removeAll();
		
		XYSeries series = new XYSeries(
				resources.getString("chart.self.data.Name"));
		XYSeries fitseries_ascend = new XYSeries(
				resources.getString("chart.self.fit.ascend.Name"));
		XYSeries fitseries_lin = new XYSeries(
				resources.getString("chart.self.fit.lin.Name"));
		
		for (int j = 0; j < massV.size(); j++) {
			series.add(massV.elementAt(j), effV.elementAt(j));
		}
		
		double dlta = xendascend / 10.0;
		int i = 0;
		while (true) {
			i++;
			double xx = 0.0 + (i - 1) * dlta;
			double yy = afit_ascend+bfit_ascend*xx;
			fitseries_ascend.add(xx, yy);
			if (0.0 + (i - 1) * dlta > xendascend+xendascend/10.0) {// x2 always is
															// bigger!
				break;
			}
		}
		
		dlta = (massV.elementAt(massV.size()-1)-xstartlin) / 10.0;
		i = 0;
		while (true) {
			i++;
			double xx = xstartlin -dlta + (i - 1) * dlta;//-dlta=offset!
			double yy = afit_lin+bfit_lin*xx;
			fitseries_lin.add(xx, yy);
			if (xstartlin + (i - 1) * dlta > massV.elementAt(massV.size()-1)) {// x2 always is
															// bigger!
				break;
			}
		}
		
		// Data collection!...only one series per collection for full control!
		XYSeriesCollection data = new XYSeriesCollection(series);
		XYSeriesCollection fitdata1 = new XYSeriesCollection(fitseries_ascend);
		XYSeriesCollection fitdata2 = new XYSeriesCollection(fitseries_lin);

		// Axis
		NumberAxis xAxis = new NumberAxis(resources.getString("self.chart.x"));
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(resources.getString("self.chart.y"));
		
		// Renderer
		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.SHAPES);//StandardXYItemRenderer.LINES);
		// the series index (zero-based)in Collection...always 0 as seen above!
		renderer.setSeriesPaint(0, Color.BLACK);//RED);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		
		// Now the plot:
		XYPlot plot = new XYPlot();
		plot.setDataset(0, data);
		plot.setDomainAxis(0, xAxis);// the axis index;axis
		plot.setRangeAxis(0, yAxis);
		plot.setRenderer(0, renderer);
		
		plot.setDataset(1, fitdata1);
		XYItemRenderer renderer0 = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);
		renderer0.setSeriesPaint(0, Color.RED);
		plot.setRenderer(1, renderer0);

		plot.setDataset(2, fitdata2);
		XYItemRenderer renderer2 = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);
		renderer2.setSeriesPaint(0, Color.BLUE);//RED);
		plot.setRenderer(2, renderer2);

		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// allow chart movement by pressing CTRL and drag with mouse!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		JFreeChart chart = new JFreeChart(resources.getString("self.chart.NAME"),
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(Bang.bkgColor);
		cp.setChart(chart);
	}
	
	/**
	 * Setting up actions!
	 * @param arg0 arg0
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {
			compute();
		} else if (command.equals(ADD_COMMAND)) {
			add();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		}
	}
	
	/**
	 * Add an entry (sample mass and sample-detector efficiency)
	 */
	private void add(){
		double mass = 0.0;
		double eff = 0.0;

		boolean nulneg = false;
		try {

			mass = Convertor.stringToDouble(massTf.getText());
			if (mass <= 0)
				nulneg = true;
			eff = Convertor.stringToDouble(effTf.getText());
			if (eff <= 0)
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
		// ==================
		massV.addElement(mass);
		effV.addElement(eff);

		String s = resources.getString("self.list.nrcrt") + (nDataList + 1)
				+ "; "  
				+ resources.getString("self.list.mass") + mass + "; "
				+ resources.getString("self.list.eff") + eff;
		
		ListUtilities.add(s, dlm);
		ListUtilities.select(nDataList, dataList);

		nDataList++;
		
		massTf.setText("");
		effTf.setText("");
		massTf.requestFocusInWindow();
	}
	
	/**
	 * Delete an entry from list
	 */
	private void delete(){
		if (nDataList != 0) {
			nDataList--;

			int index = ListUtilities.getSelectedIndex(dataList);
			// ListUtilities.remove(index,dlm);
			// ListUtilities.select(nDataList-1,dataList);

			massV.removeElementAt(index);
			effV.removeElementAt(index);

			// now reconstruct list:
			ListUtilities.removeAll(dlm);
			for (int i = 0; i < nDataList; i++) {

				String s = resources.getString("self.list.nrcrt") + (i + 1)
						+ "; "  + resources.getString("self.list.mass")
						+ massV.elementAt(i) + "; "
						+ resources.getString("self.list.eff")
						+ effV.elementAt(i);

				ListUtilities.add(s, dlm);
			}
			ListUtilities.select(nDataList - 1, dataList);
		} 
	}
	
	/**
	 * Perform self-absorbtion computations. It estimates the optimum sample mass and its 
	 * optimum efficiency when self-absorbtion effect has minimum effect.
	 */
	private void compute(){
		if (massV.size() < 2) {
			String title = resources.getString("data.error.title");
			String message = resources.getString("data.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);

			return;
		}
		//======================
		int nData=massV.size();
		////////////////////////////////////
		double[] mv=new double[nData];
		double[] ev=new double[nData];
		for (int i=0;i<nData;i++){
			mv[i]=massV.elementAt(i);
			ev[i]=effV.elementAt(i);
		}
		//------------------
		Sort.qSort2(mv, ev);
		//-------------------------
		massV.removeAllElements();
		effV.removeAllElements();
		for (int i=0;i<nData;i++){
			massV.addElement(mv[i]);
			effV.addElement(ev[i]);
		}
		/////////////////////
		// now reconstruct list:
		ListUtilities.removeAll(dlm);
		for (int i = 0; i < nData; i++) {

			String s = resources.getString("self.list.nrcrt") + (i + 1)
					+ "; "  + resources.getString("self.list.mass")
					+ massV.elementAt(i) + "; "
					+ resources.getString("self.list.eff")
					+ effV.elementAt(i);

			ListUtilities.add(s, dlm);
		}
		ListUtilities.select(nDataList - 1, dataList);
		////////////////////////////////////
		int iBreak=0;
		for (int i=0;i<nData-1;i++){
			iBreak=i;
			//double diff=Math.abs(effV.elementAt(i)-effV.elementAt(i+1))/
			//(effV.elementAt(i)+effV.elementAt(i+1));
			//if (diff<0.05){//10%
			//	break;
			//}
			
			if ((effV.elementAt(i)>effV.elementAt(i+1)-0.1*effV.elementAt(i+1)) && 
					(effV.elementAt(i)<effV.elementAt(i+1)+0.1*effV.elementAt(i+1))){
				break;
			}
			
			if (i==nData-2){
				//not found anything
				iBreak=nData-1;//last!
			}
		}
		//===================
		int n1=0;
		if (iBreak==0){
			n1=2;//it is 0,0 and first vector because could not be 1 point!!!
		} else {
			n1=iBreak+1;//max nData!! if stop at iBreak=1 =>we take 0,0 plus vector index [0]!
		}
		double[] xascend=new double[n1];
		double[] yascend=new double[n1];
		xascend[0]=0.0;
		yascend[0]=0.0;
		for (int i=1;i<n1;i++){
			xascend[i]=massV.elementAt(i-1);
			yascend[i]=effV.elementAt(i-1);
		}
		xendascend=xascend[n1-1];
		//-------------------------------
		int n2 = 0;
		if (iBreak<nData-1){
			n2=nData-iBreak;
		} else {
			//we have not valid data!! take last two!
			n2=2; 
		}
		double[] xlin = new double[n2];
		double[] ylin = new double[n2];
		if (iBreak<nData-1){
			for (int i=0;i<n2;i++){
				xlin[i]=massV.elementAt(iBreak+i);
				ylin[i]=effV.elementAt(iBreak+i);
			}
		} else {
			//we have not valid data!! take last two!
			xlin[0]=massV.elementAt(nData-2);
			ylin[0]=effV.elementAt(nData-2);
			xlin[1]=massV.elementAt(nData-1);
			ylin[1]=effV.elementAt(nData-1);

		}
		xstartlin=xlin[0];
		double[] sig=new double[1];
		sig[0]=1.0;//formal here!
		//====================================
		//TEST
		/*for (int i=0;i<n1;i++){
			System.out.println("asc "+xascend[i]+" ; "+yascend[i]);
		}
		for (int i=0;i<n2;i++){
			System.out.println("lin "+xlin[i]+" ; "+ylin[i]);
		}*/
		
		/*int ndata=2;
		double[] x=new double[2];
		double[] y=new double[2];
		x[0]=0.0;
		y[0]=0.0;
		x[1]=0.040;
		y[1]=0.332;
		ModelingData.fit(x, y, ndata, sig, 0);
		double afit=ModelingData.a_fit;
		double bfit=ModelingData.b_fit;
		
		double ytest=afit+bfit*x[1];
		System.out.println("a "+afit+"; b "+bfit+"; ytest "+ytest);*/
		
		ModelingData.fit(xascend, yascend, n1, sig, 0);
		afit_ascend=ModelingData.a_fit;
		bfit_ascend=ModelingData.b_fit;
		
		ModelingData.fit(xlin, ylin, n2, sig, 0);
		afit_lin=ModelingData.a_fit;
		bfit_lin=ModelingData.b_fit;
		
		double massOptim=(afit_lin-afit_ascend)/(bfit_ascend-bfit_lin);
		double effOptim=afit_ascend+bfit_ascend*massOptim;
		
		massOptimTf.setText(Convertor.doubleToString(massOptim));
		effOptimTf.setText(Convertor.doubleToString(effOptim));
		
		createChart();
	}
}
