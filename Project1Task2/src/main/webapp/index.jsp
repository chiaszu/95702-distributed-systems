<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Distributed Systems Class Clicker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <h1>Distributed Systems Class Clicker</h1>
    <%
        String lastAnswer = (String) session.getAttribute("lastAnswer");
        if (lastAnswer != null) {
    %>
    <div>Your "<%= lastAnswer %>" has been registered</div>
    <%
        }
    %>

    <div>Submit your answer to the current question:</div>
    <form action="submitAnswer" method="POST">
        <input type="radio" id="optionA" name="answer" value="A">
        <label for="optionA">A</label><br>

        <input type="radio" id="optionB" name="answer" value="B">
        <label for="optionB">B</label><br>

        <input type="radio" id="optionC" name="answer" value="C">
        <label for="optionC">C</label><br>

        <input type="radio" id="optionD" name="answer" value="D">
        <label for="optionD">D</label><br><br>

        <button type="submit">Submit</button>
    </form>
</body>
</html>