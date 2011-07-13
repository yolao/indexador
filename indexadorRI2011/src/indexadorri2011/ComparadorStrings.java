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
public class ComparadorStrings implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        return o2.toString().compareToIgnoreCase(o1.toString());
    }
 
}
