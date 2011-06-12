/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

/**
 *
 * @author Aaron
 */
import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ExtractorDeTexto {
    private String texto;
    private Hashtable<String,Integer> terminos;
    
    public ExtractorDeTexto(){
        terminos = new Hashtable<String, Integer> (3000);
    }
    
    public void extraer(String archivo) throws Exception {
        String sourceUrlString = archivo;
        
        if (sourceUrlString.indexOf("file:") == -1) 
            sourceUrlString = "file:" + sourceUrlString;
        
        MicrosoftConditionalCommentTagTypes.register();
        PHPTagTypes.register();
        PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        MasonTagTypes.register();
        Source source = new Source(new URL(sourceUrlString));

        // Call fullSequentialParse manually as most of the source will be parsed.
        source.fullSequentialParse();

        System.out.println("Document title:");
        String title=getTitle(source);
        System.out.println(title == null ? "(none)" : title);

        System.out.println("\nDocument description:");
        String description=getMetaValue(source,"description");
        System.out.println(description == null ? "(none)" : description);

        System.out.println("\nDocument keywords:");
        String keywords=getMetaValue(source,"keywords");
        System.out.println(keywords == null ? "(none)" : keywords);

        System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE elements):\n");
        texto = source.getTextExtractor().setIncludeAttributes(false).toString();
        
        
        System.out.println(texto);
    }
    
    public void obtenerTerminos(){
        String [] palabras = texto.split("\\s|\\?|¿|\\.|\\,|:|;|¡|!");
        Integer value = 0;
        for (String word : palabras){
            if (!word.isEmpty()){
                value = terminos.get(word);
                if (value == null)
                    terminos.put(word, 1);
                else{
                    terminos.remove(word);
                    terminos.put(word, value + 1);
                }
                //System.out.println(word);
            }
        }
    }
    
    public void mostrarTerminos(){
        int size = terminos.size();
        Enumeration<String> llaves = terminos.keys();
        String termino;
        for (int i = 0; i < size; i++){
            termino = llaves.nextElement();
            System.out.println("termino " + (i+1) + " " + termino + " frecuencia " + terminos.get(termino));
        }
}

    private String getTitle(Source source) {
        Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
        if (titleElement == null) 
            return null;
        // TITLE element never contains other tags so just decode it collapsing whitespace:
        return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
    }

    private String getMetaValue(Source source, String key) {
        for (int pos=0; pos<source.length();) {
            StartTag startTag=source.getNextStartTag(pos,"name",key,false);
            if (startTag == null) return null;
            if (startTag.getName().equals(HTMLElementName.META))
                    return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
            pos = startTag.getEnd();
        }
        return null;
    }
}