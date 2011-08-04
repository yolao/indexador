/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

/**
 *
 * @author Aaron
 */
import java.util.logging.Level;
import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.Array;
import java.text.DecimalFormat;

public class ExtractorDeTexto <tipoFrecuencia>{
    private String texto;
    private Hashtable<String,Termino> terminos;
    Hashtable<String, Integer> stopWords;
    
    //ThreadEscritor hiloEscritor;
    String cadenaEscribir;
    
    public ExtractorDeTexto(){
        terminos = new Hashtable<String, Termino> (3000);
        //hiloEscritor = new ThreadEscritor();
        //hiloEscritor.setFinalizarHilo(false);       
        //hiloEscritor.start();
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
        String [] palabras = texto.split("([^a-zA-ZáéíóúÁÉÍÓÚ])");//viejo ---->>split("\\s|\\?|¿|\\.|\\,|:|;|¡|!|/");áéíóúÁÉÍÓÚ
        Double valueLocal = 0.0;
        // tabla hash que contiene los terminos encontrados en el archivo actual, su capacidad inicial es 500
        // y el Integer es la frecuencia en el documento
        Hashtable <String,Double> terminosLocales = new Hashtable<String, Double>(500);        
        
        //se itera  sobre las palabras encontradas en el texto        
        for (String word : palabras){
            if (!word.isEmpty()){

                try{
                    word = word.toLowerCase();                
                    char [] palabra = word.toCharArray();
                    for(int i=0;i<word.length();i++){
                        switch(palabra[i]){
                            case 'á':
                                palabra[i]='a';
                                break;
                            case 'é':
                                palabra[i]='e';
                                break;
                            case 'í':
                                palabra[i]='i';
                                break;
                            case 'ó':
                                palabra[i]='o';
                                break;
                            case 'ú':
                                palabra[i]='u';
                                break;
                            case 'ñ':
                                palabra[i]='n';
                                break;
                        }
                    }
                    word=new String (palabra);
                }catch(Exception e){
                    System.out.print("callose!!!!");
                }
                //busco el término en la tablaHash Global
                /*if(word.equalsIgnoreCase("voleibol")){
                    System.out.print("fallo voleibol");
                }*/
                //si el término se encontró por primera vez--------------->Agregar los documentos en los que aparece el termino en la instancia del término.,......
                if (!terminos.containsKey(word)){
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
        /*se guardan los documentos sin normalizar*/
        guardarTerminos(terminosLocales, nombreArchivo,false);
        return terminosLocales;
    }
    
    private void eliminarStopWordsVocabulario (String ruta) {
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto(ruta+"vocabulario.txt");
        String [] lineas = new String [terminos.size()/*int lector.getTamanno(ruta+"vocabulario.txt")*/];
        String linea = lector.leerLinea(0);
        String [] termino;
        stopWords= new Hashtable<String,Integer>(150);
        int i=0;
        boolean stopWord=true;
        do {
            termino = linea.split(" ");
            if (stopWord) {
                if(termino[0].equals("top"))
                    stopWord=false;
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
        guardarStopWords(stopWords, ruta+"stopwords");
    }
        
    private void lematizar(int puntoCorte) {
        Object [] listaTerminos = terminos.keySet().toArray();
        Termino termino;
        String lematizado;
        //int fusionados=0;
        int cantTerminos = listaTerminos.length;
        /*ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        escritor.guardarStringLn("lematizado\toriginal\tcon quien se fusionó","palabrasFusionadas.txt", false);*/
        for(int indice=0; indice < cantTerminos; indice++){//se recorren todos los términos
            
            if(listaTerminos[indice].toString().length()>puntoCorte){//si el termino necesita lematización
                lematizado = listaTerminos[indice].toString().substring(0,puntoCorte);
                /*if(listaTerminos[indice].toString().equalsIgnoreCase("voleibol")){
                    System.out.print("caso voleibol");
                }*/
                if(terminos.containsKey(lematizado)){//si la palabra lematizada concuerda con otra se deben fusionar
                    termino = terminos.get(lematizado);
                    //escritor.guardarStringLn(lematizado + "\t" + listaTerminos[indice].toString()+"\t"+terminos.get(lematizado).toString(), "palabrasFusionadas.txt",true);
                    termino.fusionar(terminos.get(listaTerminos[indice]));
                    //fusionados++;
                    terminos.remove(termino.getTermino());
                    terminos.put(termino.getTermino(),termino);
                    terminos.remove(listaTerminos[indice]);                
                }else{//si no hay concordancia con otro termino ya lematizado
                    termino = terminos.get(listaTerminos[indice]);
                    termino.setTermino(lematizado);
                    terminos.remove(listaTerminos[indice]);
                    terminos.put(termino.getTermino(), termino);                    
                }
                
            }
        }
        //System.out.println("Cantidad de archivos fusionados " + fusionados);
        
    }
    
    /*Método que lematiza la colección y el vocabulario*/
    public void eliminarStopWordsYLematizar(String ruta, int cantidadArchivos,int puntoCorte){
        /*Revisar que ya se hayan quitado las stopwords*/
        /*Y guardarlos stopwords dentro de una hasta table*/
        System.out.println("eliminando stopwords vocabulario");
        this.eliminarStopWordsVocabulario(ruta);
        
        /*lematizar archivo vocabularioSote*/ 
        /*lematizar el hasTable y verificar exista la palabra en la hastable y sumarla*/
        /*Sumar cosas del termino Crear metodo en Termino llamado fusionar*/
        System.out.println("lematizando vocabulario y fusionando terminos con sus frecuencias....");
        lematizar(puntoCorte);
        /*Quitar StopWords de los documentos*/
        Hashtable <String, Double > documento;
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto();
        String [] terminoTemp;        
        String [] archivo;
        Double frecuenciaTemp;
        Double maxFrec; 

        /*Itera en todos los archivos*/
        /*lematizar archivitos*/
        /*Crear hashtable a base de words y apariciones intra documento*/
        /*Guardar mayor frecuencia*/
        System.out.println("lematizando todos los documentos");
        documento = new Hashtable<String, Double>(500);
        for(int numDocumento=0;numDocumento<cantidadArchivos;numDocumento++){//itera sobre los documentos
            documento.clear();
            System.out.println("lematizando el documento y quitando stopwords"+numDocumento+"/"+cantidadArchivos);
            archivo = lector.leerLineasTexto(ruta+"/"+numDocumento+".txt");
            maxFrec = 0.0;             
            
         
            
            for(int numTermino=0;numTermino<archivo.length;numTermino++){//iterar sobre los terminos del documento
                terminoTemp = archivo[numTermino].split(" ");        
                if( !terminoTemp[0].equals("") && !this.stopWords.containsKey(terminoTemp[0]) ){//Si el término no es un stopWord                    
                    
                    if(terminoTemp[0].length() > puntoCorte){//si el término ocupa lematizarse                        
                        terminoTemp[0]=terminoTemp[0].substring(0,puntoCorte);
                    }
                    if (!this.stopWords.containsKey(terminoTemp[0])){

                        if(documento.containsKey(terminoTemp[0])){//si el termino lematizado ya está en el documento
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
            }
            System.out.println("Normalizando la frecuencia en la hashtable y guardando");
            /*al terminar de lematizar todo recorro la hashtable calculando frecuencia normalizada
             la del termino entre la mayor*/
            Object [] arregloTerminosObj;
            for(int indice=0; indice < documento.size();indice++){
                arregloTerminosObj = documento.keySet().toArray();
                frecuenciaTemp = documento.get(arregloTerminosObj[indice].toString()) / maxFrec ;                
                documento.remove(arregloTerminosObj[indice].toString());
                documento.put(arregloTerminosObj[indice].toString(), frecuenciaTemp);                
            }

            /*Se llama guardarTerminos del extractor con los terminos normalizados*/
            this.guardarTerminos(documento, ruta + numDocumento+".txt", true);
        }
        this.stopWords=null;
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
    
    private void guardarStopWords(Hashtable <String,Integer> terminos, String nombreArchivo) {
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        //String termino;
        Object [] terminosOrdenados = terminos.keySet().toArray();
        escritor.crearArchivo(nombreArchivo, terminosOrdenados[0].toString());
        //escritor.guardarStringLn(terminosOrdenados[0].toString(), nombreArchivo, false);
        String cadena = "";
        cadenaEscribir = "";
        for (int i = 1; i < size; i++){
            /*Se almacena el termino " " la frecuencia*/
            //escritor.guardarStringLn(terminosOrdenados[i].toString(), nombreArchivo, true);
            cadena += terminosOrdenados[i].toString() + "\n";
        }
        //while (hiloEscritor.isEscribir());        
        //cadenaEscribir = cadena;
       /* hiloEscritor.setArchivo(nombreArchivo);
        hiloEscritor.setTexto(cadenaEscribir);
        hiloEscritor.setEscribir(true);*/
        //hiloEscritor.escribir(cadena,nombreArchivo);
        escritor.agregarAlArchivo(cadena);
        escritor.cerrarArchivo();
    }
    
    /**
     * Método que permite almacenar los términos encontrados en el archivo procesado
     * en un archivo de texto plano con cada término indexable en una linea aparte.
     * @param terminos Es la tabla hash que posee la totalidad de los términos encontrados.
     * @param nombreArchivo Es el nombre que se asignará al archivo que almacenará
     * los términos indexables para el archivo procesado.
     */
    private void guardarTerminos(Hashtable <String,Double> terminos, String nombreArchivo,boolean lematizados) {
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        //String termino;
        
        Object [] terminosOrdenados = terminos.keySet().toArray();
        String espaciosTermino = " ";
        String espaciosFrecuencia = "";
        String frecuencia = "";
        int length;

        try{
            //El primero crea el archivo desde 0
            frecuencia = terminos.get(terminosOrdenados[0]).toString();
            if(lematizados){
                length = terminosOrdenados[0].toString().length();
                espaciosTermino = length > 5?" ":"      ".substring(0,6-length);
                length = frecuencia.toString().length();
                espaciosFrecuencia = length > 7?"":"       ".substring(0,7-length);
                frecuencia = length > 7?frecuencia.substring(0,7):frecuencia;
            }
            escritor.crearArchivo(nombreArchivo, terminosOrdenados[0].toString()+espaciosTermino+frecuencia+espaciosFrecuencia+"\n");
        }catch(Exception e){
            escritor.crearArchivo(nombreArchivo,"");
        }
        String cadena = "";
        //cadenaEscribir = "";
        for (int i = 1; i < size; i++){
            /*Se almacena el termino " " la frecuencia*/
            frecuencia = terminos.get(terminosOrdenados[i]).toString();
            if(lematizados){
                length = terminosOrdenados[i].toString().length();
                espaciosTermino = length > 5?" ":"      ".substring(0,6-length);
                length = frecuencia.toString().length();
                espaciosFrecuencia = length > 7?"":"       ".substring(0,7-length);
                frecuencia = length > 7?frecuencia.substring(0,7):frecuencia;
            }else {
                espaciosTermino = " ";
                espaciosFrecuencia = "";
            }
            cadena += terminosOrdenados[i].toString() + espaciosTermino + frecuencia + espaciosFrecuencia + "\n";
        }
        escritor.agregarAlArchivo(cadena);       
        escritor.cerrarArchivo();
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
        int cantTerminosEscribir = 0;
        Object [] vectorTerminos = terminos.values().toArray();
        ComparadorTerminos c = new ComparadorTerminos();
        Arrays.sort(vectorTerminos, c);        
        escritor.crearArchivo(ruta + "vocabulario.txt",  ((Termino)vectorTerminos[0]).toString()+"\n");
        String cadena = "";
        
        for (int i = 1; i < size; i++, cantTerminosEscribir++){
            cadena += ((Termino)vectorTerminos[i]).toString() + "\n";
            if (cantTerminosEscribir == 6000 || i + 1 == size){
                escritor.agregarAlArchivo(cadena);
                cadena = "";
                cantTerminosEscribir = 0;
            }
        }
        escritor.cerrarArchivo();
    }
    
        /**
     * Método que permite crear el vocabulario completo de la colección
     * @param ruta Es la ruta en la que se almacenarán los términos encontrados
     * con su frecuencia en la colección
     */
    public void crearVocabularioCompleto(String ruta){
        System.out.println("Creando el vocabulario completo");
        Termino.reiniciarContador();
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        int size = terminos.size();
        Object [] vectorTerminos = terminos.values().toArray();
        String cadena = "";
        cadenaEscribir = "";

        ComparadorTerminosAlfabetico c = new ComparadorTerminosAlfabetico();
        System.out.println("ordenando todos los terminos");
        Arrays.sort(vectorTerminos, c);
        System.out.println("guardando terminos ordenados en el vocabulario.squema");        
        escritor.crearArchivo(ruta + "vocabulario.schema", ((Termino)vectorTerminos[0]).impresionParaVocabulario()+"\n" );
        int cantTerminosEscribir = 0;
        
        for (int i = 1; i < size; i++, cantTerminosEscribir++){
            cadena += ((Termino)vectorTerminos[i]).impresionParaVocabulario() + "\n";
            if (cantTerminosEscribir == 3000 || i + 1 == size){
                escritor.agregarAlArchivo(cadena);
                cantTerminosEscribir = 0;
                cadena = "";
            }
        }
        escritor.cerrarArchivo();
    }

    void crearPostingsYNorma(String ruta) {
        long cantTerminos = terminos.size();
        System.out.println("Creando postings y norma");
        ManejadorArchivosTexto lector = new ManejadorArchivosTexto();
        ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
        Object[] vectTerminos = terminos.keySet().toArray();
        //Se debe ordenar si se ordeno en el vocabulario.
        ComparadorString c = new ComparadorString();
        Arrays.sort(vectTerminos, c);
        String[] vectDocumentos;
        String archivo;        
        int inicioPalabra;
        int inicioFrecuencia=5;
        int finalFrecuencia=11;
        Double frecuencia;        
        Double [] norma = new Double [(int)Termino.getTotalDocumentos()];
        DecimalFormat df = new DecimalFormat("0.00000");
        
        for(int i = 0;i<norma.length;i++) 
            norma[i]=0.0;
        Double w;
        String cadena;
        escritor.crearArchivo(ruta+"Postings.schema", "");
        /*Se recorren todos los terminos de la colección*/
        cadena = "";
        String wString;
        for(int numTermino=0;numTermino<cantTerminos;numTermino++){
            /*Se sacan los documentos donde aparece este termino*/            
            vectDocumentos = terminos.get(vectTerminos[numTermino].toString()).getDocumentosContenedores().split("\n");
            /*Guarda los ws de un termino*/           
            /*Se recorren los documentos donde aparece el termino*/
            for(int numDoc=0;numDoc<vectDocumentos.length;numDoc++){                
                try {
                    archivo = lector.leerTodoArchivo(ruta+vectDocumentos[numDoc]+".txt",null);
                    inicioPalabra = archivo.indexOf(vectTerminos[numTermino].toString()+"      ".substring(0,6-vectTerminos[numTermino].toString().length()));
                    /*donde inicia la palabra, se salta la palabra y se salta un espacio en blanco*/
                    inicioFrecuencia = inicioPalabra + 6;//vectTerminos[numTermino].toString().length()+1;
                    finalFrecuencia = inicioFrecuencia + 7;//archivo.indexOf("\n",inicioFrecuencia);
                    frecuencia=0.0;
                    try{
                        frecuencia = Double.parseDouble(archivo.substring(inicioFrecuencia, finalFrecuencia).trim());
                    }catch(Exception e){
                        System.out.print("error de frecuencia");
                    }
                    if (frecuencia == 0)
                        System.out.println(numDoc);                
                    w = frecuencia * terminos.get(vectTerminos[numTermino].toString()).idf();
                    wString = df.format(w);
                    wString = wString.substring(0,7);
                    cadena += vectDocumentos[numDoc] + "       ".substring(0,vectDocumentos[numDoc].toString().length()>7?0:7-vectDocumentos[numDoc].length())
                           + wString + "\n";
                    norma[Integer.parseInt(vectDocumentos[numDoc])] += w*w;
                    if (cadena.length() >= 18000 || (numTermino + 1 == cantTerminos && numDoc + 1 == vectDocumentos.length)){
                        escritor.agregarAlArchivo(cadena);
                        System.out.println(numTermino + " / " + cantTerminos);
                        cadena = "";
                    }
                }
                /*Guarda los ids de los doc donde aparece el termino junto con el w en el doc*/ 
                catch (IOException ex) {
                    java.util.logging.Logger.getLogger(ExtractorDeTexto.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
            /*Guarda los ids de los doc donde aparece el termino junto con el w en el doc*/
        }
        /*se termina de calcular la norma y se guarda*/
        cadena = "";
        String normaDocu;
        
        for(int i=0;i<norma.length;i++){
            normaDocu = df.format(Math.sqrt(norma[i]));
            cadena += (normaDocu.substring(0,7))+"\n"; 
        }
        escritor.cerrarArchivo();
        System.out.println("guardar Norma");
        escritor.crearArchivo( ruta+"Norma.schema", cadena);
        escritor.cerrarArchivo();
    }
}