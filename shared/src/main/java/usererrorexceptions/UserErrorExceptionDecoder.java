package usererrorexceptions;

public class UserErrorExceptionDecoder {
    public void throwUserErrorException(int code) throws UserErrorException {
        switch (code) {
            case 1 -> throw new WrongPasswordException();
            case 2 -> throw new WrongUsernameException();
            case 3 -> throw new UsernameTakenException();
            case 4 -> throw new GameFullException();
            case 5 -> throw new GameNotFoundException();
            case 6 -> throw new NotLoggedInException();
            case 7 -> throw new IncompleteRequestException();
            default -> throw new RuntimeException("An unknown error occurred.");
        }
    }

    public int getCode(Throwable e) {
        return switch (e) {
            case WrongPasswordException ignored -> 1;
            case WrongUsernameException ignored -> 2;
            case UsernameTakenException ignored -> 3;
            case GameFullException ignored -> 4;
            case GameNotFoundException ignored -> 5;
            case NotLoggedInException ignored -> 6;
            case IncompleteRequestException ignored -> 7;
            default -> 0;
        };
    }

    public String getMessage(Throwable e) {
        return switch (e) {
            case WrongPasswordException ignored -> "wrong password";
            case WrongUsernameException ignored -> "wrong username";
            case UsernameTakenException ignored -> "username is already taken";
            case GameFullException ignored -> "team already assigned";
            case GameNotFoundException ignored -> "game ID not found";
            case NotLoggedInException ignored -> "not logged in";
            case IncompleteRequestException ignored -> "bad request";
            default -> "unknown error";
        };
    }

    public int getHTTPStatusCode(Throwable e) {
        return switch (e) {
            case WrongPasswordException ignored -> 401;
            case WrongUsernameException ignored -> 401;
            case UsernameTakenException ignored -> 403;
            case GameFullException ignored -> 403;
            case GameNotFoundException ignored -> 403;
            case NotLoggedInException ignored -> 401;
            default -> 400;
        };
    }
}
