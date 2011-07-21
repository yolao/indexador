/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.lang.Double;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;

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
       // System.out.close();
        System.err.close();
        
        if (args.length == 0){
            System.out.println("Debe proporcionar la ruta de la colección");
            System.exit(1);
        }
/*
        ManejadorArchivosTexto manejador = new ManejadorArchivosTexto();
        String [] lineas = manejador.leerLineasTexto("urlsM"); 
        String espacios = " ";
        String [] splits = lineas[0].split(" ");
        for(int j=splits[0].length()+1;j<1024;j++){
            espacios+=" ";
        }
        lineas[0]= splits[0] + espacios + splits[splits.length-1]+"      ".substring(0, 6-splits[splits.length-1].length())+"\n";
        manejador.guardarString(lineas[0], "urls.schema", false);
         for(int i = 1 ; i < lineas.length;i++){
            //lineas = manejador.leerLineasTexto("urlsM"); 
            splits = lineas[i].split(" ");
            espacios = " ";
            for(int j=splits[0].length()+1;j<1024;j++){
                espacios+=" ";
            }
            try{
                lineas[i] = splits[0] + espacios + splits[splits.length-1]+"      ".substring(0, 6-splits[splits.length-1].length())+"\n";
            }catch(Exception e){
                System.out.print("error extension");
            }
            manejador.guardarString(lineas[i], "urls.schema", true);
        }*/
        
        
        
        
        /* NULOSSSSSSSSSSSSSSSSSSSSS
        
        String [] splits = lineas[0].split("\u0000");
        
        
        for(int j=splits[0].length()+1;j<1024;j++){
            espacios+=" ";
        }
        lineas[0]=splits[0] + espacios + splits[splits.length-1]+"      ".substring(0, 6-splits[splits.length-1].length())+"\n";
        manejador.guardarString(lineas[0], "urls.schema", false);
        for(int i = 1 ; i < lineas.length;i++){
            //lineas = manejador.leerLineasTexto("urlsM"); 
            splits = lineas[i].split("\u0000");
            espacios = " ";
            for(int j=splits[0].length()+1;j<1024;j++){
                espacios+=" ";
            }////estoy implementando la vara para arreglar el formato delos urls malos...
            try{
                if(null==splits[splits.length-2])
                    System.out.print("splits err");
            }catch(Exception e){
                    System.out.print("splits err");
            }
            try{
                lineas[i] = splits[0]+espacios+ splits[splits.length-1]+"      ".substring(0, 6-splits[splits.length-1].length())+"\n";
            }catch(Exception e){
                System.out.print("error extension");
            }
            manejador.guardarString(lineas[i], "urls.schema", true);
        }*/
        
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
                //Logger.getLogger(ExtractorDeTexto.class.getName()).log(Level.SEVERE, null, ex);
            }                    
        }
        else{
            ruta = rutaColeccion + "/" + carpeta + "/" + "urls.schema";
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
                escritor.guardarStringLn(urls, (rutaNueva + "urls.txt"), false);
            }
            else
                escritor.guardarStringLn(urls, (rutaNueva + "urls.txt"), true);
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
            System.out.println("procesando la carpeta numero "+carpeta+" para sacar los términos");
            if(carpeta != 8 && carpeta != 13){//nos estamos brincando las carpetas que estaban mal hechas
                numArchivoLocal = 0;
                carpetaCompleta = false;
                procesarTerminos(rutaColeccion, carpeta, rutaNueva);
                
                do {
                    ruta = rutaColeccion + "/" + carpeta + "/" + numArchivoLocal;
                    if(carpeta==11||carpeta==14||carpeta==15){
                        ruta+=".html";         
                        archivo = new File(ruta);
                        if (!archivo.exists() || !archivo.isFile()) { 
                            ruta=ruta.substring(0, ruta.length()-5);                     
                        }          
                    }
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
        
        System.out.println("creando el vocabulario txt sin normalizar ni lematizar");
        Termino.setTotalDocumentos(numArchivoGlobal);        
        extractor.crearVocabulario("./indice/");

                /*sacar stopwords
         vocabulario y en archivitos*/
        
        /*Lematizar(extractor) sumando las posibles repeticiones */
        System.gc();
        extractor.eliminarStopWordsYLematizar("./indice/", numArchivoGlobal, 5);
        System.gc();
        /*Crear el vocabulario con todos los campos que pide casa usando el método imprimirParaVocabulario*/        
        extractor.crearVocabularioCompleto("./indice/");
        System.gc();
        /*Crear el posting y norma dentro de extractor*/
        extractor.crearPostingsYNorma("./indice/");
        
    }
}
