<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>


<html>
<head>
    <title>Distributed Systems Class Clicker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <h1>Distributed Systems Class Clicker</h1>
    <%
        // Retrieve results from request attribute
        Map<String, Integer> results = (Map<String, Integer>) request.getAttribute("results");

        if (results == null || results.isEmpty()) {
    %>
        <p>There are currently no results.</p>
    <%
    } else { %>
    <div>The results from the survey are as follows</div>
        <%for (Map.Entry<String, Integer> entry : results.entrySet()) {
    %>
    <p><%= entry.getKey() %>: <%= entry.getValue() %></p>
    <%
            }
        }
    %>
</body>
</html>
