package dk.jimmikristensen.aaws.domain.asciidoc;

import org.asciidoctor.ast.Author;
import org.asciidoctor.ast.DocumentHeader;
import org.asciidoctor.ast.RevisionInfo;
import org.asciidoctor.ast.StructuredDocument;

public interface AsciidocConverter {
    public DocumentHeader getDocHeader();
    public Author getAuthor();
    public RevisionInfo getRevisionInfo();
    public StructuredDocument getDocument();
    public StructuredDocument getDocument(int maxLevel);
    public void loadString(String docString);
    public String convert();
    public AsciidocBackend getBackend();
}
