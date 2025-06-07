<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hash Function Calculator</title>
</head>
<body>
    <h1>Hash Function Calculator</h1>
    <form action="computeHashes" method="POST">
        <label for="inputText">Enter Text:</label>
        <input type="text" id="inputText" name="inputText" required><br><br>

        <label>Choose Hash Algorithm:</label><br>
        <input type="radio" id="md5" name="hashAlgorithm" value="MD5" checked>
        <label for="md5">MD5</label><br>
        <input type="radio" id="sha256" name="hashAlgorithm" value="SHA-256">
        <label for="sha256">SHA-256</label><br><br>

        <button type="submit">Compute Hash</button>
    </form>
</body>
</html>