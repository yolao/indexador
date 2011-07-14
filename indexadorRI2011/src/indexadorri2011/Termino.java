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

    public void fusionar(Termino termino){
        contador = 0;
        this.aparicionesEnColeccion += termino.aparicionesEnColeccion;
        String [] contenedoresLocal = documentosContenedores.split("\n");
        String [] contenedoresFusionar = termino.documentosContenedores.split("\n");
        int indiceLocal =0;
        int indiceFusionar=0;
        int valorTempLocal = 0;
        int valorTempFusionar = 0;
        String contenedoresFusionados = new String();
        valorTempLocal = Integer.parseInt(contenedoresLocal[indiceLocal]);
        valorTempFusionar = Integer.parseInt(contenedoresFusionar[indiceFusionar]);
        while(indiceLocal < contenedoresLocal.length || indiceFusionar < contenedoresFusionar.length){
            /*if(indiceLocal<contenedoresLocal.length)
                valorTempLocal = Integer.parseInt(contenedoresLocal[indiceLocal]);
            //if(indiceFusionar<contenedoresFusionar.length)
                valorTempFusionar = Integer.parseInt(contenedoresFusionar[indiceFusionar]);*/
            if(indiceLocal < contenedoresLocal.length && valorTempLocal < valorTempFusionar){
                contenedoresFusionados += contenedoresLocal[indiceLocal] +"\n";
                indiceLocal++;         
                if(indiceLocal<contenedoresLocal.length)
                    valorTempLocal = Integer.parseInt(contenedoresLocal[indiceLocal]);
            }else if(indiceFusionar<contenedoresFusionar.length && valorTempLocal > valorTempFusionar){
                contenedoresFusionados += contenedoresFusionar[indiceLocal] +"\n";                
                indiceFusionar++;
                if(indiceFusionar<contenedoresFusionar.length)
                    valorTempFusionar = Integer.parseInt(contenedoresFusionar[indiceLocal]);
            }else{
                contenedoresFusionados += contenedoresLocal[indiceLocal] +"\n";
                indiceFusionar++;
                indiceLocal++;             
                if(indiceLocal<contenedoresLocal.length)
                    valorTempLocal = Integer.parseInt(contenedoresLocal[indiceLocal]);
            }
        }
        this.documentosContenedores = contenedoresFusionados;
    }
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
        if(idf.length()>7)
            idf = idf.substring(0,7);
        String apariciones = aparicionesEnColeccion+"";
        return  termino
/*termino 0-4*/ + "                               ".substring(0,5-termino.length())/**/
/*idf 6-12*/    + idf
                + "                               ".substring(0,8-idf.length())
/*CantDocu14-19*/+ apariciones
                + "                               ".substring(0,7-apariciones.length())
/*posInicial*/  + tempCont
                + "                               ".substring(0,10-apariciones.length());       
        
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
