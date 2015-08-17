package dk.jimmikristensen.aaws.domain.github.exception;

public class GithubHttpErrorException extends Exception {

    public GithubHttpErrorException(String desc) {
        super(desc);
    }
}
