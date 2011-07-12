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
     * D:\UCR\I-2011\RI\tareas\tarea7\indexadorRI2011\coleccion
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (args.length == 0){
            System.out.println("Debe proporcionar la ruta de la colección");
            System.exit(1);
        }
        
        crearArchivoTerminos(args);

    }
    
    public static void copiarArchivoUrls(String rutaColeccion, int carpeta, String rutaNueva){
        ManejadorArchivosTexto lector; 
        String urls = "";
        File archivo;
        String ruta = rutaColeccion + "/" + carpeta + "/" + "urls";
        archivo = new File(ruta);
        if (archivo.exists() && archivo.isFile()) {
            try{
                lector = new ManejadorArchivosTexto();
                urls = lector.leerTodoArchivo(ruta);
            }
            catch(Exception ex){
                Logger.getLogger(ExtractorDeTexto.class.getName()).log(Level.SEVERE, null, ex);
            }                    
        }
        else{
            ruta = rutaColeccion + "/" + carpeta + "/" + "urls.txt";
            archivo = new File(ruta);
            if (archivo.exists() && archivo.isFile()) {
                try{
                    lector = new ManejadorArchivosTexto();
                    urls = lector.leerTodoArchivo(ruta);
                }
                catch(Exception ex){
                    Logger.getLogger(ExtractorDeTexto.class.getName()).log(Level.SEVERE, null, ex);
                }                    
            }
        }
        if(urls.length() > 0){
            // guarda en el archivo de URLS los urls recien leidos...
            ManejadorArchivosTexto escritor = new ManejadorArchivosTexto();
            // La primera vez crea el archivo, desp solo agrega.
            if(carpeta == 1){
                escritor.guardarString(urls, (rutaNueva + "urls.txt"), false);
            }
            else
                escritor.guardarString(urls, (rutaNueva + "urls.txt"), true);
        }
    }

    private static void crearArchivoTerminos(String[] args) {
        String rutaColeccion = args[0];
        String ruta;
        String rutaNueva = "./coleccionCompleta/";
        File dir = new File(rutaNueva);
        if (!dir.exists())
            dir.mkdir();
        ExtractorDeTexto extractor = new ExtractorDeTexto();
        //ManejadorArchivosTexto archivo;
        File archivo;
        int numArchivoLocal;
        int numArchivoGlobal = 0;
        boolean carpetaCompleta;
        //Recorre las carpetas de la colección
        for (int carpeta = 1; carpeta < 19; carpeta++){
            // La carpeta 8 esta mal hecha, hay q brincarsela.
            if(carpeta != 8){
                numArchivoLocal = 0;
                carpetaCompleta = false;
                copiarArchivoUrls(rutaColeccion, carpeta, rutaNueva);
                do {
                    ruta = rutaColeccion + "/" + carpeta + "/" + numArchivoLocal;
                    archivo = new File(ruta);
                    if (archivo.exists() && archivo.isFile()) {
                        try{
                            extractor.extraer(ruta);
                            extractor.obtenerTerminos(rutaNueva + numArchivoGlobal + ".txt");
                            numArchivoGlobal++;
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
        }
        extractor.crearVocabulario(rutaNueva);    
    }
}
