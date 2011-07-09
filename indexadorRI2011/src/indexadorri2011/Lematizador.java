/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

/**
 *
 * @author TOTAN
 */
public class Lematizador {    
    public void lematizar(int puntoCorte, Termino[] listaTerminos){
        for(int indice = listaTerminos.length-1; indice>-1;indice++){
            listaTerminos[indice].setTermino(listaTerminos[indice].getTermino().substring(0, puntoCorte));
        }    
    }
}
