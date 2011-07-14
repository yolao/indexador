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
                    terminos.get(word).agregarDocumentoContenedor(indiceDocumento); 
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
                        terminos.get(word).agregarDocumentoContenedor(indiceDocumento); 
                        
                    }else{ //Ya fue encontrado dentro de este documento y se suma una aparicion mas
                        terminosLocales.remove(word);
                        terminosLocales.put(word,valueLocal+1);
                    }
                }

                //System.out.println(word);
            }
        }
        guardarTerminos(terminosLocales, nombreArchivo);
        return terminosLocales;
    }
    
    private void eliminarStopWordsVocabulario (String ruta) {
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto(ruta+"vocabulario.txt");
        String [] lineas = new String [terminos.size()/*int lector.getTamanno(ruta+"vocabulario.txt")*/];
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
                
            }
            else{
                lineas[i] = linea;
                i++;
            }
            linea = lector.leerSiguienteLinea();
        }while(linea != null);
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        escritor.guardarLineasTexto(lineas,ruta+"vocabulario.txt",false);
    }
        
    private void lematizar(Hashtable<String, Termino> tabla, int puntoCorte) {
        Object [] listaTerminos = tabla.keySet().toArray();
        String terminoTemp = listaTerminos[0].toString();
        Termino termino;
        String lematizado;
        int cantTerminos = listaTerminos.length;
        for(int indice=0; indice < cantTerminos; indice++){//se recorren todos los términos
            if(listaTerminos[indice].toString().length()>puntoCorte){//si el termino necesita lematización
                lematizado = listaTerminos[indice].toString().substring(0,puntoCorte);
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
    public void eliminarStopWordsYLematizar(String ruta, int cantidadArchivos,int puntoCorte){
        /*Revisar que ya se hayan quitado las stopwords*/
        /*Y guardarlos stopwords dentro de una hasta table*/
        this.eliminarStopWordsVocabulario(ruta);
        
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
        
        for(int numDocumento=0;numDocumento<cantidadArchivos;numDocumento++){//itera sobre los documentos
            archivo = lector.leerLineasTexto(ruta+"/"+numDocumento+".txt");

            for(int numTermino=0;numTermino<archivo.length;numTermino++){//iterar sobre los terminos del documento
                terminoTemp = archivo[numTermino].split(" ");        
                if(this.stopWords.get(terminoTemp[0])==null){//Si el término no es un stopWord                    
                    
                    if(terminoTemp[0].length() > puntoCorte){//si el término ocupa lematizarse                        
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
            }
            /*al terminar de lematizar todo recorro la hashtable calculando frecuencia normalizada
             la del termino entre la mayor*/
            Object [] arregloTerminosObj;
            for(int indice=0; indice < documento.size();indice++){
                arregloTerminosObj = documento.keySet().toArray();
                frecuenciaTemp = documento.get(arregloTerminosObj[indice].toString()) / maxFrec ;                
                documento.remove(arregloTerminosObj[indice].toString());
                documento.put(arregloTerminosObj[indice].toString(), frecuenciaTemp);
            }
            /*Se llama guardarTerminos del extractor */
            this.guardarTerminos(documento, ruta+numDocumento+".txt");
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
        Object [] terminosOrdenados = terminos.keySet().toArray();
        escritor.guardarString(terminosOrdenados[0].toString()+" "+terminos.get(terminosOrdenados[0]), nombreArchivo, false);
        for (int i = 1; i < size; i++){            
            /*Se almacena el termino " " la frecuencia*/
            escritor.guardarString(terminosOrdenados[i].toString()+" "+terminos.get(terminosOrdenados[i]), nombreArchivo, true);
        }
    }
    
    /**
     * Método que permite crear el vocabulario completo de la colección
     * @param ruta Es la ruta en la que se almacenarán los términos encontrados
     * con su frecuencia en la colección
     */
    public void crearVocabulario(String ruta){
        Termino.reiniciarContador();
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        Object [] vectorTerminos = terminos.values().toArray();
        ComparadorTerminos c = new ComparadorTerminos();
        Arrays.sort(vectorTerminos, c);        
        String termino;
        escritor.guardarString(((Termino)vectorTerminos[0]).toString(), ruta + "vocabulario.txt", false);
        
        for (int i = 1; i < size; i++){
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
        Termino.reiniciarContador();
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        Object [] vectorTerminos = terminos.values().toArray();
        ComparadorTerminosAlfabetico c = new ComparadorTerminosAlfabetico();
        Arrays.sort(vectorTerminos, c);        
        String termino;
        escritor.guardarString(((Termino)vectorTerminos[0]).impresionParaVocabulario(), ruta + "vocabulario.txt", false);
        for (int i = 1; i < size; i++){
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

    void crearPostingsYNorma(String ruta) {
        long cantTerminos = terminos.size();
        
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto();
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        Object[] vectTerminos = terminos.keySet().toArray();
        String[] vectDocumentos;
        String archivo;
        int inicioPalabra;
        int inicioFrecuencia;
        Double frecuencia;
        Double [] ws;
        Double [] norma = new Double [terminos.size()];
        for(int i = 0;i<terminos.size();i++) 
            norma[i]=0.0;
        Double w;
        String cadena;
        escritor.guardarString("", ruta+"Postings.schema", false); 
        /*Se recorren todos los terminos de la colección*/
        for(int numTermino=0;numTermino<cantTerminos;numTermino++){
            /*Se sacan los documentos donde aparece este termino*/
            vectDocumentos = terminos.get(vectTerminos[numTermino].toString()).getDocumentosContenedores().split("\n");
            /*Guarda los ws de un termino*/
            ws = new Double [vectDocumentos.length];
            cadena = "";
            /*Se recorren los documentos donde aparece el termino*/
            for(int numDoc=0;numDoc<vectDocumentos.length;numDoc++){
                archivo = lector.leerTodoArchivo(ruta+vectDocumentos[numDoc]+".txt");////////////**************se puede mejorar
                inicioPalabra = archivo.indexOf(vectTerminos[numTermino].toString()+" ");
                /*donde inicia la palabra, se salta la palabra y se salta un espacio en blanco*/
                inicioFrecuencia =inicioPalabra+vectTerminos[numTermino].toString().length()+1;
                long finalFrecuencia = archivo.indexOf("\n",inicioFrecuencia);
                finalFrecuencia = finalFrecuencia == -1? archivo.length():finalFrecuencia;
                                
                frecuencia = Double.parseDouble(archivo.substring(inicioFrecuencia, 
                                                archivo.indexOf("\n",inicioFrecuencia)==-1?archivo.length():archivo.indexOf("\n",inicioFrecuencia)).trim());
                
                //ws[numDoc] = frecuencia * terminos.get(vectTerminos[numTermino]).idf();                         
                w = frecuencia * terminos.get(vectTerminos[numTermino].toString()).idf();
                cadena += vectDocumentos[numDoc] + "       ".substring(0,vectDocumentos[numDoc].length()>7?7:7-vectDocumentos[numDoc].length())
                        + w + "       ".substring(0,w.toString().length()>7?7:7-w.toString().length())+ "\n";
                norma[numDoc] += w*w;            
            }            
            /*Guarda los ids de los doc donde aparece el termino junto con el w en el doc*/          
            escritor.guardarString(cadena, ruta+"Postings.schema", true);            
        }
        /*se termina de calcular la norma y se guarda*/
        cadena = "";        
        for(int i=0;i<terminos.size();i++){
            cadena += Math.sqrt(norma[i])+"\n"; 
        }
        escritor.guardarString(cadena, ruta+"Norma.schema", false);
            
    }
}