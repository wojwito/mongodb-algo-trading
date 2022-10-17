package exe;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.miginfocom.swing.MigLayout;

/**
 * This class stores all the GUI elements - charts, buttons, panels etc.
 */

public class BuilderTest extends JFrame{
	
	private JFrame frmDigitalAssetTrade;
	
	MongoDBTradeSimulator newInstance = new MongoDBTradeSimulator();
	
	public static JTextArea tradesTextArea = new JTextArea();
	public static XYSeries prices = new XYSeries("Price");
	public static XYSeries mAvgs = new XYSeries("M Avg");
	public static XYSeries nAvgs = new XYSeries("N Avg");
	
	public static JSpinner mSpinner = new JSpinner();
	public static JSpinner nSpinner = new JSpinner();
	public static JSpinner notValueSpinner = new JSpinner();


	public static void main(String[] args) throws InterruptedException 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{ 
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					BuilderTest window = new BuilderTest();
					window.frmDigitalAssetTrade.setVisible(true);
					
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		
	}
	
	/**
	 * Create the application.
	 */
	public BuilderTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frmDigitalAssetTrade = new JFrame();
		frmDigitalAssetTrade.setTitle("MongoDB Trade Simulator");
		frmDigitalAssetTrade.setBounds(100, 100, 1125, 715);
		frmDigitalAssetTrade.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setBorder(new TitledBorder(null, "Config", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane tradesPane = new JScrollPane();
		tradesPane.setViewportBorder(new TitledBorder(null, "Trades", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel = new ChartPanel(chart);
		
		
		chartPanel.setBorder(new TitledBorder(null, "Chart", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(frmDigitalAssetTrade.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(menuPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tradesPane, GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE))
				.addComponent(chartPanel, GroupLayout.DEFAULT_SIZE, 1109, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addComponent(chartPanel, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(tradesPane, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
						.addComponent(menuPanel, GroupLayout.PREFERRED_SIZE, 171, Short.MAX_VALUE))
					.addContainerGap())
		);	

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
		tradesTextArea.setEditable(false);
		
		DefaultCaret caret = (DefaultCaret) tradesTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		tradesPane.setViewportView(tradesTextArea);
		menuPanel.setLayout(new MigLayout("", "[][]", "[][][][][][][][][][][][][][][][][][][][][]"));
		
		JLabel mLabel = new JLabel("M (minutes)");
		menuPanel.add(mLabel, "cell 0 0");
		
		mSpinner.setValue(5);
		menuPanel.add(mSpinner, "cell 1 0");
		
		JLabel nLabel = new JLabel("N (minutes)");
		menuPanel.add(nLabel, "cell 0 1");
		
		nSpinner.setValue(60);
		menuPanel.add(nSpinner, "cell 1 1");
		
		JLabel balLabel = new JLabel("Balance");
		menuPanel.add(balLabel, "cell 0 2");
		
		JSpinner balSpinner = new JSpinner();
		balSpinner.setValue(Integer.valueOf(1000));
		menuPanel.add(balSpinner, "cell 1 2");
		
		JLabel notValLabel = new JLabel("Notional value");
		menuPanel.add(notValLabel, "cell 0 3");
		
		notValueSpinner.setValue(Integer.valueOf(20));
		menuPanel.add(notValueSpinner, "cell 1 3");
		
		JButton stopButton = new JButton(" Stop");
		stopButton.setHorizontalAlignment(SwingConstants.RIGHT);
		stopButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				newInstance.myWorker.cancel(true);
			}
		});
		
		JButton runButton = new JButton("  Run ");
		menuPanel.add(runButton, "cell 0 5");
		
		runButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				newInstance = new MongoDBTradeSimulator();
				try 
				{
					newInstance.runApp();
				} 
				catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		menuPanel.add(stopButton, "cell 1 5");
		
		JButton aboutButton = new JButton("About");
		aboutButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JOptionPane.showMessageDialog(frmDigitalAssetTrade,"MongoDB Trade Simulator by Wojciech Witoszynski\n");
				
			}
		});
		menuPanel.add(aboutButton, "cell 0 6");
		
		JButton exitButton = new JButton(" Exit ");
		exitButton.setHorizontalAlignment(SwingConstants.RIGHT);
		exitButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
		menuPanel.add(exitButton, "cell 1 6");
		frmDigitalAssetTrade.getContentPane().setLayout(groupLayout);
	}
	
    private XYDataset createDataset() {

        prices = new XYSeries("Price");
        mAvgs = new XYSeries("M Avg");
        nAvgs = new XYSeries("N Avg");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(prices);
        dataset.addSeries(mAvgs);
        dataset.addSeries(nAvgs);
        
        return dataset;
    }
    
    private JFreeChart createChart(final XYDataset dataset) {

    	JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Moving Average Chart",
                "Time",
                "Price ($)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
    	
        DateAxis domain = (DateAxis) plot.getDomainAxis();
        domain.setDateFormatOverride(DateFormat.getDateTimeInstance());
        domain.setAutoRange(true);
        

        var renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(false);
        renderer.setSeriesPaint(0,Color.MAGENTA);
        renderer.setSeriesPaint(1,Color.BLUE);
        renderer.setSeriesPaint(2,Color.RED);

        plot.setRenderer(renderer);
        //plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        chart.getLegend().setFrame(BlockBorder.NONE);

        return chart;
    }
}
