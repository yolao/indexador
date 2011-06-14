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
    private String termino;
    /*contiene la cantidad de documentos en los que aparece el termino*/
    private int aparicionesEnDocumentos;

    public Termino(String termino, int aparicionesEnDocumentos){
        this.termino=termino;
        this.aparicionesEnDocumentos=aparicionesEnDocumentos;
    }
    
    public String toString(){
        int tamano = termino.length();
        return (tamano > 30? termino.substring(0, 29) : termino)
                + "                               ".substring(0,tamano>30? 0 : 29-termino.length())
                + this.aparicionesEnDocumentos;
    }
    /**
     * @return el termino
     */
    public String getTermino() {
        return termino;
    }

    /**
     * @param termino el término
     */
    public void setTermino(String termino) {
        this.termino = termino;
    }

    /**
     * @return las cantidad de documentos en la que aparecio el término
     */
    public int getAparicionesEnDocumentos() {
        return aparicionesEnDocumentos;
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
