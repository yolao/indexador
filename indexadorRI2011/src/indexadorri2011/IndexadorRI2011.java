/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;


/**
 *
 * @author Aaron
 */
public class IndexadorRI2011 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (args.length == 0){
            System.out.println("Debe proporcionar la ruta de la colección");
            System.exit(1);
        }
        String rutaColeccion = args[0];
        String ruta;
        ExtractorDeTexto extractor = new ExtractorDeTexto();
        //ManejadorArchivosTexto archivo;
        File archivo;
        int numArchivoLocal;
        boolean carpetaCompleta;
        //Recorre las carpetas de la colección
        for (int carpeta = 1; carpeta < 3; carpeta++){
            numArchivoLocal = 0;
            carpetaCompleta = false;
            do {                                
                ruta = rutaColeccion + "/" + carpeta + "/" + numArchivoLocal;
                archivo = new File(ruta);
                if (archivo.exists() && archivo.isFile()) {
                    try{
                        extractor.extraer(ruta);
                        extractor.obtenerTerminos();
                    }
                    catch(Exception ex){
                        Logger.getLogger(ExtractorDeTexto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                    carpetaCompleta = true;
                numArchivoLocal++;
            }while(!carpetaCompleta);
        }
        extractor.mostrarTerminos();
    }
}