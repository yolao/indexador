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
public class ComparadorTerminos implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        return ((Termino)o2).getAparicionesEnDocumentos()-((Termino)o1).getAparicionesEnDocumentos();
    }
    
}
