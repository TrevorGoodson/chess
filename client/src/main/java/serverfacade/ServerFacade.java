package serverfacade;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.stream.Collectors;
import requestresultrecords.*;
import usererrorexceptions.*;

import static java.lang.Integer.parseInt;

public class ServerFacade {
    private static final String SERVER_URL = "http://localhost:";
    private final String port;

    public ServerFacade(int port) {
        this.port = port + "";
    }

    public static void main(String[] args) {
        try {
            new ServerFacade(8080).clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UserErrorException {
        var partialRequest = new PartialCreateGameRequest(createGameRequest.gameName());
        return makeHTTPRequest("POST",
                               "game",
                               partialRequest,
                               createGameRequest.authToken(),
                               CreateGameResult.class);
    }

    public void clear() throws UserErrorException {
        makeHTTPRequest("DELETE", "db", null, null, null);
    }

    public Record handleRequest(Record request) throws UserErrorException {
        return null;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws UserErrorException {
        String color = switch (joinGameRequest.playerColor()) {
            case WHITE -> "WHITE";
            case BLACK -> "BLACK";
        };
        var partialRequest = new PartialJoinGameRequest(color, joinGameRequest.gameID());
        return makeHTTPRequest("PUT",
                               "game",
                               partialRequest,
                               joinGameRequest.authToken(),
                               JoinGameResult.class);
    }

    public ListResult listGames(ListRequest listRequest) throws UserErrorException {
        return makeHTTPRequest("GET",
                               "game",
                               null,
                               listRequest.authToken(),
                               ListResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws UserErrorException {
        return makeHTTPRequest("POST",
                               "session",
                               loginRequest,
                               null,
                               LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest) throws UserErrorException {
        makeHTTPRequest("DELETE",
                        "session",
                        null,
                        logoutRequest.authToken(),
                        null);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws UserErrorException {
        return makeHTTPRequest("POST",
                               "user",
                               registerRequest,
                               null,
                               RegisterResult.class);
    }

    protected <T> T makeHTTPRequest(String httpMethod,
                                    String path,
                                    Object requestBody,
                                    String authToken,
                                    Class<T> responseType) throws UserErrorException {
        try {
            URL url = (new URI("http" + getServerUrl() + port + "/" + path)).toURL();
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

    private void ensureSuccessful(HttpURLConnection httpConnection) throws IOException,
                                                                           UserErrorException {
        int status = httpConnection.getResponseCode();
        if (status / 100 == 2) {
            return;
        }

        try (InputStream errorResponseStream = httpConnection.getErrorStream();
             InputStreamReader errorResponseReader = new InputStreamReader(errorResponseStream);
             BufferedReader errorResponseBuffReader = new BufferedReader(errorResponseReader)
        ) {
            String errorResponse = errorResponseBuffReader.lines().collect(Collectors.joining("/n"));
            if (errorResponse.trim().isEmpty()) {
                throw new UserErrorException("Unknown failure:" + status);
            }
            ErrorMessage errorMessage = new Gson().fromJson(errorResponse, ErrorMessage.class);
            new UserErrorExceptionDecoder().throwUserErrorException(errorMessage.code());
        }
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

    public static String getServerUrl() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load server.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            return props.getProperty("server.url");
        } catch (Exception ex) {
            throw new RuntimeException("unable to process server.properties", ex);
        }
    }
}
