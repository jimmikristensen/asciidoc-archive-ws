package dk.jimmikristensen.aaws.domain;

import java.util.HashMap;
import java.util.Map;
import org.asciidoctor.Asciidoctor;
import static org.asciidoctor.Asciidoctor.Factory.create;
import org.asciidoctor.ast.Author;
import org.asciidoctor.ast.DocumentHeader;
import org.asciidoctor.ast.RevisionInfo;
import org.asciidoctor.ast.StructuredDocument;

public class AsciidocConverter {
    
    private final Asciidoctor asciidoctor;
    private DocumentHeader header;
    private String asciidocString;
    
    public AsciidocConverter() {
        asciidoctor = create();
    }
    
    public DocumentHeader getDocHeader() {
        if (asciidocString == null) { 
            return null; 
        }
        header = asciidoctor.readDocumentHeader(asciidocString);
        return header;
    }
    
    public Author getAuthor() {
        if (asciidocString == null) { 
            return null; 
        }
        if (header == null) {
            header = asciidoctor.readDocumentHeader(asciidocString);
        }
        return header.getAuthor();
    }
    
    public RevisionInfo getRevisionInfo() {
        if (asciidocString == null) { 
            return null; 
        }
        if (header == null) {
            header = asciidoctor.readDocumentHeader(asciidocString);
        }
        return header.getRevisionInfo();
    }
    
    public StructuredDocument getDocument() {
        if (asciidocString == null) { 
            return null; 
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(Asciidoctor.STRUCTURE_MAX_LEVEL, 2);
        return asciidoctor.readDocumentStructure(asciidocString, parameters);
    }
    
    public StructuredDocument getDocument(int maxLevel) {
        if (asciidocString == null) { 
            return null; 
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(Asciidoctor.STRUCTURE_MAX_LEVEL, maxLevel);
        return asciidoctor.readDocumentStructure(asciidocString, parameters);
    }
    
    public void loadString(String docString) {
        asciidocString = docString;
    }
    
    public String getHtml() {
        if (asciidocString == null) { 
            return null; 
        }
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("backend", "html5");
        
        Map<String, Object> options = new HashMap<>();
        options.put("attributes", attributes);        

        return asciidoctor.convert(asciidocString, options);
    }
}