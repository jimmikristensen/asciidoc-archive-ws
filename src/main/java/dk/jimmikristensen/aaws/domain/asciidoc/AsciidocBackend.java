package dk.jimmikristensen.aaws.domain.asciidoc;

public enum AsciidocBackend {
    
    HTML5("html5");
    
    private final String backend;
    
    private AsciidocBackend(String backend) {
        this.backend = backend;
    }
    
    public String getbackend() {
        return backend;
    }
    
    @Override
    public String toString() {
        return backend;
    }
    
}
