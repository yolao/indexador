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
    private static long totalTerminos;
    private static long totalDocumentos;
    private String termino;
    /*contiene la cantidad de documentos en los que aparece el termino*/
    private int aparicionesEnDocumentos;
    private static long contador;

    public Termino(String termino, int aparicionesEnDocumentos){
        this.termino=termino;
        this.aparicionesEnDocumentos=aparicionesEnDocumentos;
    }
    
    public String toString(){
        int tamano = termino.length();
        long tempCont = contador;
        contador += aparicionesEnDocumentos;
        return (tamano > 30? termino.substring(0, 29) : termino)
/*termino*/     + "                               ".substring(0,tamano>30? 0 : 29-termino.length())
/*idf*/         + Math.log(totalDocumentos/this.aparicionesEnDocumentos) 
                + "                               ".substring(0,tamano>10? 0 : 9-termino.length())
/*CantDocus*/   + this.aparicionesEnDocumentos
                + "                               ".substring(0,tamano>10? 0 : 9-termino.length())
/*posInicial*/  + tempCont;
        
    }
    /**
     * @return el termino
     */
    public String getTermino() {
        return termino;
    }

    public void restablecerTotalTerminos(){
        totalTerminos = 0;
    }
    public void sumarTotalTerminos(){
        totalTerminos++;
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
        return aparicionesEnDocumentos;
    }
    public void int reiniciarContador(){
        contador = 0;
    }
    /**
     * @param aparicionesEnDocumentos la cantidad de documentos en las que aparece el término en la coleccion
     */
    public void setAparicionesEnDocumentos(int aparicionesEnDocumentos) {
        this.aparicionesEnDocumentos = aparicionesEnDocumentos;
    }  
    
    public void sumarAparicion(){
        aparicionesEnDocumentos++;
    }
}
