package dk.jimmikristensen.aaws.domain.asciidoc;

import java.util.HashMap;
import java.util.Map;
import org.asciidoctor.Asciidoctor;
import static org.asciidoctor.Asciidoctor.Factory.create;
import org.asciidoctor.ast.Author;
import org.asciidoctor.ast.DocumentHeader;
import org.asciidoctor.ast.RevisionInfo;
import org.asciidoctor.ast.StructuredDocument;

public class GeneralAsciidocConverter implements AsciidocConverter {
    
    protected Asciidoctor asciidoctor;
    protected DocumentHeader header;
    protected String asciidocString;
    protected AsciidocBackend backend;
    
    public GeneralAsciidocConverter() {
        asciidoctor = create();
    }
    
    @Override
    public DocumentHeader getDocHeader() {
        if (asciidocString == null) { 
            return null; 
        }
        header = asciidoctor.readDocumentHeader(asciidocString);
        return header;
    }
    
    @Override
    public Author getAuthor() {
        if (asciidocString == null) { 
            return null; 
        }
        if (header == null) {
            header = asciidoctor.readDocumentHeader(asciidocString);
        }
        return header.getAuthor();
    }
    
    @Override
    public RevisionInfo getRevisionInfo() {
        if (asciidocString == null) { 
            return null; 
        }
        if (header == null) {
            header = asciidoctor.readDocumentHeader(asciidocString);
        }
        return header.getRevisionInfo();
    }
    
    @Override
    public StructuredDocument getDocument() {
        if (asciidocString == null) { 
            return null; 
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(Asciidoctor.STRUCTURE_MAX_LEVEL, 2);
        return asciidoctor.readDocumentStructure(asciidocString, parameters);
    }
    
    @Override
    public StructuredDocument getDocument(int maxLevel) {
        if (asciidocString == null) { 
            return null; 
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(Asciidoctor.STRUCTURE_MAX_LEVEL, maxLevel);
        return asciidoctor.readDocumentStructure(asciidocString, parameters);
    }
    
    @Override
    public void loadString(String docString) {
        asciidocString = docString;
    }
    
    @Override
    public String convert() {
        if (asciidocString == null) { 
            return null; 
        }

        return asciidoctor.convert(asciidocString, new HashMap<>());
    }

    @Override
    public AsciidocBackend getBackend() {
        return backend;
    }
}