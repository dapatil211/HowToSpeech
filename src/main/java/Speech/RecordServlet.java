package Speech;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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
			Map<String, Long> retVal = new HashMap<String, Long>();
			retVal.put("user_id", id);


			// Set appropriately based on Watson and our own processing
			String speech = "Lorem ipsum"; // Speech text
			String movements = "images/random_walk.png"; // Path to generated movement graph
			String volume = "images/decibels.gif"; // Path to generated volume graph
			String personality = "You're angry"; // Personality report
			String grade = "A-"; // Speech grade
			String details = "Try waving your arms less." // Suggestions

			retVal.put("speech", speech);
			retVal.put("movement_graph", movement);
			retVal.put("volume_graph", volume);
			retVal.put("personality", personality);
			retVal.put("grade", grade);
			retVal.put("details", details);

			resp.getWriter().write(gson.toJson(retVal));
		}
	}
}
