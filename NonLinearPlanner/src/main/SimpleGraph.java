package main;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.BasicStroke;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class SimpleGraph extends ApplicationFrame {
	public JFreeChart xylineChart;

	public SimpleGraph(String applicationTitle, String chartTitle, String xLabel, String yLabel, double[] x1, double[] y1) {
		super(applicationTitle);
		xylineChart = ChartFactory.createXYLineChart(chartTitle, xLabel, yLabel, createDataset(x1, y1),
				PlotOrientation.VERTICAL, true, true, false);

		final XYPlot plot = xylineChart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.YELLOW);
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f));
		plot.setRenderer(renderer);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);

	}

	private XYDataset createDataset(double[] x, double[] y) {
		final XYSeries line1 = new XYSeries("Data");

		int index = 0;
		for (double num : x) {
			line1.add(num, y[index]);
			index += 1;
		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(line1);
		return dataset;
	}

	private static void saveJPEG(SimpleGraph chart) throws IOException {
		int width = 960; /* Width of the image */
		int height = 720; /* Height of the image */
		File XYChart = new File("images/XYLineChart.jpeg");
		ChartUtilities.saveChartAsJPEG(XYChart, chart.xylineChart, width, height);
	}
	
	public void saveJPEG() throws IOException {
		int width = 960; /* Width of the image */
		int height = 720; /* Height of the image */
		File XYChart = new File("./images/XYLineChart.jpeg");
		ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);
	}
	
	private static void displayPlot(SimpleGraph chart) {
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}
	
	public void displayPlot() {
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
	/*public static void main(String[] args) throws IOException {
		double[] ytest = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		double[] xtest = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		
		SimpleGraph chart = new SimpleGraph("TestGraph", "StupidTest", "xValues", "yValues", xtest, ytest);
		displayPlot(chart);
		//saveJPEG(chart);
	}*/
}