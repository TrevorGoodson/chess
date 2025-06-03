import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

public class ServerFacade {
    private static final String SERVER_URL = "http//localhost:8080";

    public void createGame() {

    }

    public void clear() {

    }

    public void joinGame() {

    }

    public void listGames() {

    }

    public void login() {

    }

    public void logout() {

    }

    public void register() {

    }

    private <T> T makeHTTPRequest(String httpMethod, String path, Object requestBody, Class<T> responseType) throws ResponseException {
        try {
            URL url = (new URI(SERVER_URL + path)).toURL();
            var httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod(httpMethod);
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
