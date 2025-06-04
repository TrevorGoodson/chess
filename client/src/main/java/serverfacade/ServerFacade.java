package serverfacade;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.stream.Collectors;
import exceptions.*;
import requestresultrecords.CreateGameRequest;
import requestresultrecords.CreateGameResult;
import requestresultrecords.RegisterRequest;
import requestresultrecords.RegisterResult;
import usererrorexceptions.NotLoggedInException;

public class ServerFacade {
    private static final String SERVER_URL = "http://localhost:";
    private final String port;

    public ServerFacade(int port) {
        this.port = port + "";
    }

    public static void main(String[] args) {
        try {
            new ServerFacade(8080).clear();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException, NotLoggedInException {
        record PartialRequest(String gameName) {}
        return makeHTTPRequest("POST",
                               "game",
                               new PartialRequest(createGameRequest.gameName()),
                               createGameRequest.authToken(),
                               CreateGameResult.class);
    }

    public void clear() throws ResponseException {
        makeHTTPRequest("DELETE", "db", null, null, null);
    }

    public void joinGame() {

    }

    public void listGames() {

    }

    public void login() {

    }

    public void logout() {

    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        return makeHTTPRequest("POST",
                               "user",
                               registerRequest,
                               null,
                               RegisterResult.class);
    }

    private <T> T makeHTTPRequest(String httpMethod, String path, Object requestBody, String authToken, Class<T> responseType) throws ResponseException {
        try {
            URL url = (new URI(SERVER_URL + port + "/" + path)).toURL();
            var httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setRequestMethod(httpMethod);
            authorize(authToken, httpConnection);
            addBody(requestBody, httpConnection);

            httpConnection.connect();
            ensureSuccessful(httpConnection);

            return readBody(httpConnection, responseType);
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("Bad path", e);
        } catch (IOException e) {
            throw new RuntimeException("Connection failed", e);
        }
    }

    private static void authorize(String authToken, HttpURLConnection httpConnection) {
        if (authToken == null) {
            return;
        }
        httpConnection.addRequestProperty("Authorization", authToken);
    }

    private static void addBody(Object requestBody, HttpURLConnection httpConnection) throws IOException {
        if (requestBody == null) {
            return;
        }
        httpConnection.setDoOutput(true);
        httpConnection.addRequestProperty("Content-Type", "application/json");
        String requestBodyJSON = new Gson().toJson(requestBody);
        try (OutputStream outputStream = httpConnection.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)
        ) {
            writer.write(requestBodyJSON);
        }
    }

    private void ensureSuccessful(HttpURLConnection httpConnection) throws IOException, ResponseException {
        int status = httpConnection.getResponseCode();
        if (status / 100 == 2) {
            return;
        }

        try (InputStream errorResponseStream = httpConnection.getErrorStream();
             InputStreamReader errorResponseReader = new InputStreamReader(errorResponseStream);
             BufferedReader errorResponseBuffReader = new BufferedReader(errorResponseReader)
        ) {
            String errorResponse = errorResponseBuffReader.lines().collect(Collectors.joining("/n"));
            if (!errorResponse.trim().isEmpty()) {
                throw new ResponseException(status + errorResponse);
            }
        }

        throw new ResponseException("Unknown failure:" + status);

    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            return null;
        }
        T response = null;
        try (InputStream responseBodyStream = http.getInputStream();
             InputStreamReader responseBodyReader = new InputStreamReader(responseBodyStream);
             BufferedReader responseBodyBuffReader = new BufferedReader(responseBodyReader)
        ) {
            String responseBody = responseBodyBuffReader.lines().collect(Collectors.joining("\n"));
            if (!responseBody.trim().isEmpty()) {
                response = new Gson().fromJson(responseBody, responseClass);
            }
        }
        return response;
    }
}
