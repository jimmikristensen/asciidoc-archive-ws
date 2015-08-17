package dk.jimmikristensen.aaws.domain.github.exception;

public class GithubLimitReachedException extends Exception {

    public GithubLimitReachedException(String desc) {
        super(desc);
    }
}
