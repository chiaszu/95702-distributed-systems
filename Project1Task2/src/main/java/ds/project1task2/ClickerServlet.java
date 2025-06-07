package ds.project1task2;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/submitAnswer", "/getResults"})
public class ClickerServlet extends HttpServlet {
    ClickerModel clickerModel = null;

    @Override
    public void init() {
        clickerModel = new ClickerModel();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/submitAnswer".equals(servletPath)) {
            handleVotes(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/getResults".equals(servletPath)) {
            displayResults(request, response);
        }
    }

    private void handleVotes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String selectedAnswer = request.getParameter("answer");

        if (selectedAnswer != null && !selectedAnswer.isEmpty()) {
            // Store last submitted answer in session
            HttpSession session = request.getSession();
            session.setAttribute("lastAnswer", selectedAnswer);

            // Update vote count in clickerModel
            clickerModel.addVote(selectedAnswer);
        }


        // Redirect back to index.jsp
        response.sendRedirect("index.jsp");
    }

    private void displayResults(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Get session
        HttpSession session = request.getSession();

        // Get the results from the model (sorted alphabetically)
        Map<String, Integer> results = clickerModel.getResults();

        // Pass results to the JSP page
        request.setAttribute("results", results);

        // remove "lastAnswer" so it does not show on index.jsp again
        session.removeAttribute("lastAnswer");

        // Forward request to result.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
        dispatcher.forward(request, response);

        // Clear votes after displaying results
        clickerModel.clearResults();
    }
}
