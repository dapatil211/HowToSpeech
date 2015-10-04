package Speech;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;

import com.googlecode.charts4j.Color.*;
import com.googlecode.charts4j.UrlUtil.normalize;

public class RecordServlet extends HttpServlet {
	Map<Long, MicRunnable> threads = new HashMap<Long, MicRunnable>();
	private final static Gson gson = new Gson();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("action");
		if ("start".equals(action)) {
			Utilities.executeMyo();
			MicRunnable micRunnable = new MicRunnable();
			Thread micThread = new Thread(micRunnable);
			micThread.start();
			threads.put(micThread.getId(), micRunnable);
			Map<String, Long> retVal = new HashMap<String, Long>();
			retVal.put("user_id", micThread.getId());
			resp.getWriter().write(gson.toJson(retVal));
		}
		else if("stop".equals(action)){
			Utilities.stopMyo();
			Utilities.parseMyoData();
			double[] movement = Utilities.getMovementGraphArray();
			long id = Long.parseLong(req.getParameter("user_id"));
			MicRunnable micRunnable = threads.get(id);
			micRunnable.finish();
			List<Integer> tempVols = micRunnable.volumes;
			double[] volumes = new double[tempVols.size()];
			for(int i = 0; i < tempVols.size(); i ++){
				volumes[i] = tempVols.get(i);
			}

//	double[] pts = {0, 1, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 2, 2, 2, 1, 0, 1, 1, 2, 2};
//	double[] pts2 = {4, 28, 46, 38, 36, 38, 40, 29, 10, 8, 14, 15, 11, 11};
	
	
	// Hacky solution: scale the data up to match the graph scales, which I have trouble changing
	for (int i = 0; i < movement.length; ++i)
	{
		movement[i] *= 50;
	}
	
	for (int i = 0; i < volumes.length; ++i)
	{
		volumes[i] *= 2;
	}

			Map<String, String> retVal = new HashMap<String, String>();

			// Set appropriately based on Watson and our own processing
			String speech = micRunnable.speech; //TODO // Speech text
			String tone = "You're angry"; // Personality report
			String volumeGrade = Utilities.numericToLetterGrade(Utilities.volumeGrader(tempVols)); // Speech grade
			String movementGrade = Utilities.numericToLetterGrade(Utilities.movementGrade());
			String details = "Try waving your arms less."; // Suggestions

			retVal.put("speech", speech);
			retVal.put("movement_graph", getMovementChart(movement));
			retVal.put("volume_graph", getVolumeChart(volumes));
			retVal.put("tone", tone);
			retVal.put("grade", movementGrade);
			retVal.put("details", details);

			resp.getWriter().write(gson.toJson(retVal));
		}
	}

<<<<<<< HEAD
	String getMovementChart (int[] movPts) {

        // Defining lines
        Line line = Plots.newLine(Data.newData(movPts), Color.newColor("CA3D05"), "Movements");
        line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
        // line.addShapeMarkers(Shape.DIAMOND, Color.newColor("CA3D05"), 12);
        // line.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);

        // Defining chart.
        LineChart chart = GCharts.newLineChart(line);
        chart.setSize(450, 800);
        chart.setTitle("Your Movements", WHITE, 14);
        chart.setGrid(25, 25, 3, 2);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("Nov", "Dec", "Jan", "Feb", "Mar");
        xAxis.setAxisStyle(axisStyle);
        AxisLabels xAxis2 = AxisLabelsFactory.newAxisLabels("2007", "2007", "2008", "2008", "2008");
        xAxis2.setAxisStyle(axisStyle);
        AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("", "25", "50", "75", "100");
        AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Month", 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        yAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Hits", 50.0);
        yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        yAxis2.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(xAxis);
        chart.addXAxisLabels(xAxis2);
        chart.addXAxisLabels(xAxis3);
        chart.addYAxisLabels(yAxis);
        chart.addYAxisLabels(yAxis2);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();

        System.err.println(url);

        return url;
=======
	String getMovementChart (double[] movPts) {
		// Defining lines
		Line line = Plots.newLine(Data.newData(movPts), Color.newColor("CA3D05"), "Movements");
		line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));

		// Defining chart.
		LineChart chart = GCharts.newLineChart(line);
		chart.setSize(870, 300);
		chart.setTitle("Your Movements", Color.WHITE, 14);
		chart.setGrid(100, 50, 1, 1);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER);
		AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("Time");
		xAxis.setAxisStyle(axisStyle);

		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("None", "Small", "Large");
		yAxis.setAxisStyle(axisStyle);

		// Adding axis info to chart.
		chart.addXAxisLabels(xAxis);
		chart.addYAxisLabels(yAxis);

		// Defining background and chart fills.
		chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
		LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
		fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
		chart.setAreaFill(fill);
		String url = chart.toURLString();

		System.err.println(url);

		return url;
	}

	String getVolumeChart (double[] volPts) {
		// Defining lines
		Line line = Plots.newLine(Data.newData(volPts), Color.newColor("CA3D05"), "Volume");
		line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));

		// Defining chart.
		LineChart chart = GCharts.newLineChart(line);
		chart.setSize(870, 300);
		chart.setTitle("Your Voume", Color.WHITE, 14);
		chart.setGrid(100, 25, 1, 1);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER);
		AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("Time");
		xAxis.setAxisStyle(axisStyle);

		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("Volume");
		yAxis.setAxisStyle(axisStyle);

		// Adding axis info to chart.
		chart.addXAxisLabels(xAxis);
		chart.addYAxisLabels(yAxis);

		// Defining background and chart fills.
		chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
		LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
		fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
		chart.setAreaFill(fill);
		String url = chart.toURLString();

		System.err.println(url);

		return url;
>>>>>>> 5054388c573fa0f5891a7c4a31dfa8ab4423e13c
	}
}
