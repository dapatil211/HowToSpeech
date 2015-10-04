package Speech;

import java.io.IOException;
import java.util.HashMap;
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

public class RecordServlet extends HttpServlet {
	Map<Long, MicRunnable> threads = new HashMap<Long, MicRunnable>();
	private final static Gson gson = new Gson();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("action");
		if ("start".equals(action)) {
			MicRunnable micRunnable = new MicRunnable();
			Thread micThread = new Thread(micRunnable);
			micThread.start();
			threads.put(micThread.getId(), micRunnable);
			Map<String, Long> retVal = new HashMap<String, Long>();
			retVal.put("user_id", micThread.getId());
			resp.getWriter().write(gson.toJson(retVal));
		}
		else if("stop".equals(action)){
			long id = Long.parseLong(req.getParameter("user_id"));
			threads.get(id).isRunning = false;


double[] pts = {1, 2, 3, 4};
System.out.println(getMovementChart(pts));

			Map<String, String> retVal = new HashMap<String, String>();

			// Set appropriately based on Watson and our own processing
			String speech = "Lorem ipsum"; // Speech text
			String movements = "images/random_walk.png"; // Path to generated movement graph
			String volume = "images/decibels.gif"; // Path to generated volume graph
			String personality = "You're angry"; // Personality report
			String grade = "A-"; // Speech grade
			String details = "Try waving your arms less."; // Suggestions

			retVal.put("speech", speech);
			retVal.put("movement_graph", movements);
			retVal.put("volume_graph", volume);
			retVal.put("personality", personality);
			retVal.put("grade", grade);
			retVal.put("details", details);

			resp.getWriter().write(gson.toJson(retVal));
		}
	}

	String getMovementChart (double[] movPts) {

        // Defining lines
        Line line = Plots.newLine(Data.newData(movPts), Color.newColor("CA3D05"), "Movements");
        line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
        // line.addShapeMarkers(Shape.DIAMOND, Color.newColor("CA3D05"), 12);
        // line.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);

        // Defining chart.
        LineChart chart = GCharts.newLineChart(line);
        chart.setSize(450, 600);
        chart.setTitle("Your Movements", Color.WHITE, 14);
        chart.setGrid(25, 25, 3, 2);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("Nov", "Dec", "Jan", "Feb", "Mar");
        xAxis.setAxisStyle(axisStyle);
        AxisLabels xAxis2 = AxisLabelsFactory.newAxisLabels("2007", "2007", "2008", "2008", "2008");
        xAxis2.setAxisStyle(axisStyle);
        AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("", "25", "50", "75", "100");
        AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Month", 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(Color.WHITE, 14, AxisTextAlignment.CENTER));
        yAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Hits", 50.0);
        yAxis2.setAxisStyle(AxisStyle.newAxisStyle(Color.WHITE, 14, AxisTextAlignment.CENTER));
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
	}
}
