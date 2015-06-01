package dk.jimmikristensen.aaws.domain.asciidoc;

import java.util.HashMap;
import java.util.Map;

public class HtmlAsciidocConverter extends GeneralAsciidocConverter {
    
    @Override
    public String convert() {
        if (asciidocString == null) { 
            return null; 
        }
        
        backend = AsciidocBackend.HTML5;
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("backend", backend.getbackend());
        attributes.put("source-highlighter", "highlightjs");
        
        Map<String, Object> options = new HashMap<>();
        options.put("attributes", attributes);

        return asciidoctor.convert(asciidocString, options);
    }
    
}
