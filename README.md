# Moirai Programming Language Example Webservice
The purpose of this project is to provide a minimal demonstration webservice that uses the Moirai Programming Language public interpreter API. There are several TODO comments throughout the code that demonstrate places where the service should be customized and extended.

# Getting Started
Before you begin, clone [The Moirai Programming Language](https://github.com/moirai-lang/moirai-kt) locally. Run the publishToMavenLocal gradle task. Moirai is not published to Maven Central, so this step is required to build the service locally.

Clone this repository, then open it in IntelliJ IDEA or your favorite IDE. Run the gradle task compileTestKotlin, then run the gradle task bootRun. This will start the webservice, listening on port 8080.

Open Postman or your favorite HTTP client. Set the HTTP method to POST, url localhost:8080/execute. Set the request body to raw text. Type 5 + 6, then send the request. The server should respond with the result 11.

# Language Syntax
To learn about the syntax, check out the full [Language Syntax Guide](https://github.com/moirai-lang/moirai-kt/wiki/Language-Syntax-Guide).