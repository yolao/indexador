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

public class ExtractorDeTexto <tipoFrecuencia>{
    private String texto;
    private Hashtable<String,Termino> terminos;
    Hashtable<String, Integer> stopWords;
    
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
    public Hashtable<String, Double> obtenerTerminos(String nombreArchivo, long indiceDocumento){
        String [] palabras = texto.split("\\s|\\?|¿|\\.|\\,|:|;|¡|!|/");
        Double value = 0.0;
        Double valueLocal = 0.0;
        // tabla hash que contiene los terminos encontrados en el archivo actual, su capacidad inicial es 500
        // y el Integer es la frecuencia en el documento
        Hashtable <String,Double> terminosLocales = new Hashtable<String, Double>(500);        
        
        //se itera  sobre las palabras encontradas en el texto        
        for (String word : palabras){
            if (!word.isEmpty()){
                word = word.toLowerCase();
                //busco el término en la tablaHash Global
                Termino termino = terminos.get(word);
                //si el término se encontró por primera vez--------------->Agregar los documentos en los que aparece el termino en la instancia del término.,......
                if (termino == null){
                    //se agrega a la colección de términos Locales y Globales
                    terminos.put(word,new Termino(word, 1));
                    terminosLocales.put(word, 1.0);
                }
                //si el término ya existe
                else{
                    //busco el término en la tablaHash local
                    valueLocal = terminosLocales.get(word);
                    // Si se encontró en un archivo diferente al actual
                    if (valueLocal == null){
                        terminosLocales.put(word, 1.0);
                        //se incrementa su frecuencia en la colección                        
                        terminos.get(word).sumarAparicion();
                        
                    }else{ //Ya fue encontrado dentro de este documento y se suma una aparicion mas
                        terminosLocales.remove(word);
                        terminosLocales.put(word,valueLocal+1);
                    }
                }
                terminos.get(word).agregarDocumentoContenedor(indiceDocumento); 
                //System.out.println(word);
            }
        }
        guardarTerminos(terminosLocales, nombreArchivo);
        return terminosLocales;
    }
    
    private void eliminarStopWordsVocabulario () {
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto("vocabulario.txt");
        String [] lineas = new String [(int)lector.getTamanno("vocabulario")];
        String linea = lector.leerLinea(0);   
        String [] termino;
        stopWords= new Hashtable<String,Integer>(150);
        int i=0;
        boolean bandera=false;
        do {
            termino = linea.split(" ");
            if (bandera) {
                if(termino[0].equals("top"))
                    bandera=true;
                stopWords.put(termino[0],0);
                terminos.remove(termino[0]);
                linea = lector.leerSiguienteLinea();
            }
            else{
                lineas[i] = linea;
                i++;
            }

        }while(linea != null);
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        escritor.guardarLineasTexto(lineas,"vocabulario.txt",true);
    }
        
    private void lematizar(Hashtable<String, Termino> tabla, int puntoCorte) {
        String [] listaTerminos = (String [])tabla.keySet().toArray();
        String terminoTemp = listaTerminos[0];
        Termino termino;
        String lematizado;
        int cantTerminos = listaTerminos.length;
        for(int indice=0; indice < cantTerminos; indice++){//se recorren todos los términos
            if(listaTerminos[indice].length()>puntoCorte){//si el termino necesita lematización
                lematizado = listaTerminos[indice].substring(0,puntoCorte);
                if(tabla.containsKey(lematizado)){//si la palabra lematizada concuerda con otra se deben fusionar
                    termino = tabla.get(lematizado);
                    termino.fusionar(tabla.get(listaTerminos[indice]));
                    tabla.remove(termino.getTermino());
                    tabla.put(termino.getTermino(),termino);
                }else{//si no hay concordancia con otro termino ya lematizado
                    termino = tabla.get(listaTerminos[indice]);
                    termino.setTermino(lematizado);
                }
                tabla.remove(listaTerminos[indice]);                
            }
        }
    }
    
    /*Método que lematiza la colección y el vocabulario*/
    public void eliminarStopWordsYLematizar(String rutaColeccion, int cantidadArchivos,int puntoCorte){
        /*Revisar que ya se hayan quitado las stopwords*/
        /*Y guardarlos stopwords dentro de una hasta table*/
        this.eliminarStopWordsVocabulario();
        
        /*lematizar archivo vocabularioSote*/ 
        /*lematizar el hasTable y verificar exista la palabra en la hastable y sumarla*/
        /*Sumar cosas del termino Crear metodo en Termino llamado fusionar*/
        lematizar(this.terminos,puntoCorte);
        /*Quitar StopWords de los documentos*/
        Hashtable <String, Double > documento = new Hashtable<String, Double>(500);
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto();
        String [] terminoTemp;        
        String [] archivo;
        Double frecuenciaTemp;
        Double maxFrec = 0.0; 
        /*Itera en todos los archivos*/
        /*lematizar archivitos*/
        /*Crear hashtable a base de words y apariciones intra documento*/
        /*Guardar mayor frecuencia*/
        
        for(int numDocumento=0;numDocumento<cantidadArchivos;numDocumento++){
            archivo = lector.leerLineasTexto(rutaColeccion+numDocumento+".txt");
            if(this.stopWords.get(archivo[numDocumento])!=null){//Si no es un stopWord
                terminoTemp = archivo[numDocumento].split(" ");        
                if(terminoTemp[0].length() > 5){//si el término ocupa lematizarse
                    terminoTemp[0]=terminoTemp[0].substring(0,puntoCorte);
                }
                
                if(documento.get(terminoTemp[0])!=null){//si el termino lematizado ya está en el documento
                    //Sumar frecuencias
                    frecuenciaTemp = Double.parseDouble(terminoTemp[terminoTemp.length-1])+documento.get(terminoTemp[0]);
                    //Se reemplaza el termino en el documento
                    documento.remove(terminoTemp[0]);
                    documento.put(terminoTemp[0], frecuenciaTemp);                    
                }else{//si el termino no estaba en el documento
                    frecuenciaTemp = Double.parseDouble(terminoTemp[terminoTemp.length-1]);
                    documento.put(terminoTemp[0], frecuenciaTemp);
                } 
                if(frecuenciaTemp>maxFrec)
                    maxFrec = frecuenciaTemp;
            }
            /*al terminar de lematizar todo recorro la hastable calculando frecuencia normalizada
             la del termino entre la mayor*/
            for(int indice=0; indice < cantidadArchivos;indice++){
                terminoTemp = (String []) documento.keySet().toArray();
                frecuenciaTemp = documento.get(terminoTemp[indice]) / maxFrec ;                
                documento.remove(terminoTemp[indice]);
                documento.put(terminoTemp[indice], frecuenciaTemp);
            }
            /*Se llama guardarTerminos del extractor */
            this.guardarTerminos(documento, rutaColeccion+numDocumento+".txt");
        }

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
    private void guardarTerminos(Hashtable <String,Double> terminos, String nombreArchivo) {
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        String termino;
        String [] terminosOrdenados = (String[])terminos.keySet().toArray();
        for (int i = 0; i < size; i++){            
            /*Se almacena el termino " " la frecuencia*/
            escritor.guardarString(terminosOrdenados[i]+" "+terminos.get(terminosOrdenados[i]), nombreArchivo, true);
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
    
        /**
     * Método que permite crear el vocabulario completo de la colección
     * @param ruta Es la ruta en la que se almacenarán los términos encontrados
     * con su frecuencia en la colección
     */
    public void crearVocabularioCompleto(String ruta){
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        Object [] vectorTerminos = terminos.values().toArray();
        ComparadorTerminos c = new ComparadorTerminos();
        Arrays.sort(vectorTerminos, c);        
        String termino;
        for (int i = 0; i < size; i++){
            //termino = llaves.nextElement();
            //escritor.guardarString(termino + " " + terminos.get(termino), ruta + "vocabulario.txt", true);
            escritor.guardarString(((Termino)vectorTerminos[i]).impresionParaVocabulario(), ruta + "vocabulario.txt", true);
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