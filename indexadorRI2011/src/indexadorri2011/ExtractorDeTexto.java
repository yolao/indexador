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
import java.lang.reflect.Array;

public class ExtractorDeTexto {
    private String texto;
    private Hashtable<String,Termino> terminos;
    
    public ExtractorDeTexto(){
        terminos = new Hashtable<String, Termino> (3000);
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

        /*System.out.println("Document title:");
        String title=getTitle(source);
        System.out.println(title == null ? "(none)" : title);

        System.out.println("\nDocument description:");
        String description=getMetaValue(source,"description");
        System.out.println(description == null ? "(none)" : description);

        System.out.println("\nDocument keywords:");
        String keywords=getMetaValue(source,"keywords");
        System.out.println(keywords == null ? "(none)" : keywords);

        System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE elements):\n");*/
        texto = source.getTextExtractor().setIncludeAttributes(false).toString();
        
        
        //System.out.println(texto);
    }
    
    /**
     * Método que permite obtener los términos presentes en un documento en particular
     * @param nombreArchivo Es el nombre del archivo que se asignará al archivo 
     * que contenga los términos indexables del archivo que se esta procesando. 
     */
    public void obtenerTerminos(String nombreArchivo){
        String [] palabras = texto.split("\\s|\\?|¿|\\.|\\,|:|;|¡|!|/");
        Integer value = 0;
        Integer valueLocal = 0;
        // tabla hash que contiene los terminos encontrados en el archivo actual, su capacidad inicial es 500
        Hashtable <String,Integer> terminosLocales = new Hashtable<String, Integer>(500);        
        
        //se itera  sobre las palabras encontradas en el texto        
        for (String word : palabras){
            if (!word.isEmpty()){
                word = word.toLowerCase();
                //busco el término en la tablaHash Global
                Termino termino = terminos.get(word);
                //si el término se encontró por primera vez
                if (termino == null){
                    //se agrega a la colección de términos Locales y Globales
                    terminos.put(word,new Termino(word, 1));
                    terminosLocales.put(word, terminosLocales.size() + 1);
                }
                //si el término ya existe
                else{       
                    //busco el término en la tablaHash local
                    valueLocal = terminosLocales.get(word);
                    // Si se encontró en un archivo diferente al actual
                    if (valueLocal == null){
                        terminosLocales.put(word, terminosLocales.size() + 1);
                        //se incrementa su frecuencia en la colección                        
                        terminos.get(word).sumarAparicion();
                        /*terminos.remove(word);
                        terminos.put(word,new Termino(word, value + 1));*/
                    }
                }
                //System.out.println(word);
            }
        }
        guardarTerminos(terminosLocales, nombreArchivo);
    }
    
    /**
     * Método que retorna un arreglo con los terminos que se encuentren en la 
     * tabla hash terminos, ordenados segun frecuencia
     * @return Termino[] arreglo de terminos ordenados por frecuencia
     */
    public Termino[] ordenarTerminos(){
        //contiene los terminos para ordenarlos desendentemente con respecto a las frecuencias        
        Termino [] arregloTerminos;
        arregloTerminos = (Termino[]) terminos.values().toArray();
        ComparadorTerminos c = new ComparadorTerminos();
        
        Arrays.sort(arregloTerminos, c);
        
        return arregloTerminos;
        
    }
    
    /**
     * Método que permite almacenar los términos encontrados en el archivo procesado
     * en un archivo de texto plano con cada término indexable en una linea aparte.
     * @param terminos Es la tabla hash que posee la totalidad de los términos encontrados.
     * @param nombreArchivo Es el nombre que se asignará al archivo que almacenará
     * los términos indexables para el archivo procesado.
     */
    private void guardarTerminos(Hashtable <String,Integer> terminos, String nombreArchivo) {
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        Enumeration<String> llaves = terminos.keys();
        String termino;
        for (int i = 0; i < size; i++){
            termino = llaves.nextElement();
            escritor.guardarString(termino, nombreArchivo, true);
            //System.out.println(termino + " " + nombreArchivo);
        }
    }
    
    /**
     * Método que permite crear el vocabulario completo de la colección
     * @param ruta Es la ruta en la que se almacenarán los términos encontrados
     * con su frecuencia en la colección
     */
    public void crearVocabulario(String ruta){
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        Object [] vectorTerminos = terminos.values().toArray();
        ComparadorTerminos c = new ComparadorTerminos();
        Arrays.sort(vectorTerminos, c);
        String termino;
        for (int i = 0; i < size; i++){
            //termino = llaves.nextElement();
            //escritor.guardarString(termino + " " + terminos.get(termino), ruta + "vocabulario.txt", true);
            escritor.guardarString(((Termino)vectorTerminos[i]).toString(), ruta + "vocabulario.txt", true);
            //System.out.println("termino " + (i+1) + " " + termino + " frecuencia " + terminos.get(termino));
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