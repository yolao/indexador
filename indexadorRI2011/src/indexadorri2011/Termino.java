/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

/**
 *
 * @author TOTAN
 */
public class Termino {
    /*Contiene el termino*/
//    private static long totalTerminos;
    private static long totalDocumentos;
    /*Contiene los indices de los documentos que continen el termino, para crear el postings*/
    private String documentosContenedores;
    private String termino;
    /*contiene la cantidad de documentos en los que aparece el termino*/
    private int aparicionesEnColeccion;
    /*Lleva la cuenta de cual es la posición del primer documento en el que 
     aparece el termino en el documento de postings... el control se lleva en 
     el método toString*/
    private static long contador;

    public Termino(String termino, int aparicionesEnDocumentos){
        this.termino=termino;
        this.documentosContenedores="";
        this.aparicionesEnColeccion=aparicionesEnDocumentos;
    }
    
    public void agregarDocumentoContenedor(long indiceDocumento){
        documentosContenedores += "\n"+indiceDocumento;
    }
    
    public String getDocumentosContenedores(){
        return this.documentosContenedores;
    }

    public void fusionar(Termino terminoAFusionar){
        //TODO
    
    }
    
    
    public String toString(){
        int tamano = termino.length();        
        return (tamano > 30? termino.substring(0, 29) : termino)
/*termino*/     + "                               ".substring(0,tamano>30? 0 : 29-termino.length())
/*CantDocus*/   + aparicionesEnColeccion;       
        
    }
    
    public String impresionParaVocabulario(){
        int tamano = termino.length();
        long tempCont = contador;
        contador += aparicionesEnColeccion;
        String idf = Math.log(totalDocumentos/this.aparicionesEnColeccion)+"";
        String apariciones = aparicionesEnColeccion+"";
        return (tamano > 30? termino.substring(0, 29) : termino)
/*termino*/     + "                               ".substring(0,tamano>30? 0 : 29-termino.length())
/*idf*/         + idf
                + "                               ".substring(0,tamano>10? 0 : 9-idf.length())
/*CantDocus*/   + apariciones
                + "                               ".substring(0,tamano>10? 0 : 9-apariciones.length())
/*posInicial*/  + tempCont;       
        
    }
    /**
     * @return el termino
     */
    public String getTermino() {
        return termino;
    }
    
    public double idf(){
        return Math.log(totalDocumentos/this.aparicionesEnColeccion);
    }
    /**
     * @param termino el término
     */
    public void setTermino(String termino) {
        this.termino = termino;
    }

    public void setTotalDocumentos(long total){
        totalDocumentos = total;
    }
    /**
     * @return las cantidad de documentos en la que aparecio el término
     */
    public int getAparicionesEnDocumentos() {
        return aparicionesEnColeccion;
    }////////////////////////////////////////////////////////---------------------------------buscar donde poner los contadores de termino....
    
    public static void reiniciarContador(){
        contador = 0;
    }
    /**
     * @param aparicionesEnColeccion la cantidad de documentos en las que aparece el término en la coleccion
     */
    public void setAparicionesEnDocumentos(int aparicionesEnDocumentos) {
        this.aparicionesEnColeccion = aparicionesEnDocumentos;
    }  
    
    public void sumarAparicion(){
        aparicionesEnColeccion++;
    }
}
