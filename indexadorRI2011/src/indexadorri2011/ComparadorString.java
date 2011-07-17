/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

import java.util.Comparator;

/**
 *
 * @author Aaron
 */
public class ComparadorString implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        return ((String)o1).compareToIgnoreCase((String)o2);
    }
    
}
