/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.lang.Double;


/**
 *
 * @author Aaron
 */
public class IndexadorRI2011 {
    
    
    private static String rutaNueva = "./indice/";
    /**
     * @param args the command line arguments
     * D:\UCR\I-2011\RI\tareas\tarea7\indexadorRI2011\coleccion
     */
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Debe proporcionar la ruta de la colección");
            System.exit(1);
        }
        crearIndiceInvertido(args);
    }
    
    public static void procesarTerminos(String rutaColeccion, int carpeta, String rutaNueva){
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

    private static void crearIndiceInvertido(String[] args) {
        String rutaColeccion = args[0];
        String ruta;

        File dir = new File(rutaNueva);
        if (!dir.exists())
            dir.mkdir();
        ExtractorDeTexto extractor = new ExtractorDeTexto();
        //ManejadorArchivosTexto archivo;
        File archivo;
        Termino.reiniciarContador();
        int numArchivoLocal;
        int numArchivoGlobal = 0;
        boolean carpetaCompleta;
        //Recorre las carpetas de la colección
        for (int carpeta = 1; carpeta < 19; carpeta++){
            // La carpeta 8 esta mal hecha, hay q brincarsela.
            if(carpeta != 8){
                numArchivoLocal = 0;
                carpetaCompleta = false;
                procesarTerminos(rutaColeccion, carpeta, rutaNueva);
                do {
                    ruta = rutaColeccion + "/" + carpeta + "/" + numArchivoLocal;
                    archivo = new File(ruta);
                    if (archivo.exists() && archivo.isFile()) {
                        try{
                            extractor.extraer(ruta);
                            extractor.obtenerTerminos(rutaNueva + numArchivoGlobal + ".txt",numArchivoGlobal);
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
        
        extractor.crearVocabulario("./indice/");    

                /*sacar stopwords
         vocabulario y en archivitos*/
        
        /*Lematizar(extractor) sumando las posibles repeticiones */
        extractor.eliminarStopWordsYLematizar("./indice/", numArchivoGlobal, 5);
        /*Crear el vocabulario con todos los campos que pide casa usando el método imprimirParaVocabulario*/
        extractor.crearVocabularioCompleto("./indice/");
        /*Crear el posting y norma dentro de extractor*/
        extractor.crearPostingsYNorma("./indice/");
        
    }
}
