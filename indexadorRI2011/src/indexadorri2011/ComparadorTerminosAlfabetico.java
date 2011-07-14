/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

import java.util.Comparator;

/**
 *
 * @author TOTAN
 */
public class ComparadorTerminosAlfabetico implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        return ((Termino)o2).getTermino().compareToIgnoreCase(((Termino)o1).toString());
    }
 
}
