package dk.jimmikristensen.aaws

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import spock.lang.Specification

import static org.junit.Assert.*
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter

class TestAsciidocConversion extends Specification {

    void "Asciidoc converted to html"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase1.adoc"));
        String html = converter.convert();
        
        then:
        html != null;
        html.startsWith("<div id=\"preamble\">") == true;
        html.contains("First Section") == true;
        html.contains("Introduction to AsciiDoc") == false;
        html.length() == 513;
    }
    
    void "get meta data from document"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase2.adoc"));
        String html = converter.convert();
        
        then:
        html != null;
        "doc.writer@asciidoc.org" == converter.getAuthor().getEmail();
        "Doc Writer" == converter.getAuthor().getFullName();
        "First draft" == converter.getRevisionInfo().getRemark();
        "1.0" == converter.getRevisionInfo().getNumber();
        "2013-05-20" == converter.getRevisionInfo().getDate();
        "Sample Document" == converter.getDocHeader().getDocumentTitle().getMain();
        null == converter.getDocHeader().getDocumentTitle().getSubtitle();
        "Sample Document" == converter.getDocHeader().getDocumentTitle().getCombined();
    }
    
    void "convert badly formed document"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase3.adoc"));
        String html = converter.convert();
        
        then:
        null != html;
        null != converter.getAuthor();
        null == converter.getAuthor().getEmail();
        null == converter.getAuthor().getFullName();
        null != converter.getRevisionInfo();
        null == converter.getRevisionInfo().getRemark();
        null == converter.getRevisionInfo().getNumber();
        null == converter.getRevisionInfo().getDate();
        null != converter.getDocHeader();
        null == converter.getDocHeader().getDocumentTitle();
    }
    
    private String getTestCase(String fileName) {
        InputStream is = getClass().getResourceAsStream("/"+fileName);

        String contents = "";
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }

            return buf.toString();

        } catch (IOException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
