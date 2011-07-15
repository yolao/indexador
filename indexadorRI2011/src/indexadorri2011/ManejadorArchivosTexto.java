package indexadorri2011;

// Código reutilizado. Fuente Pablo López Gutiérrez

import java.io.FileNotFoundException;
import java.io.FileReader; // clase para lectura de archivos de texto
import java.io.FileWriter; // clase para escritura de archivos de texto
import java.io.BufferedReader; // clase para lectura de flujos de texto en forma agrupada (arreglos, l�neas, etc.)
import java.io.File;
import java.io.PrintWriter; // clase para conversi�n de objetos con formato a un flujo de salida de texto
import java.io.IOException; // excepciones generadas por manipulaci�n de archivos
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser; // componente gr�fico para la selecci�n de un archivo

class ManejadorArchivosTexto {
  public static final int MODO_LECTURA = 1;
  public static final int MODO_ESCRITURA = 2;
  private final String TEXTO_BOTON_ABRIR_ARCHIVO = "Leer de archivo";
  private final String TEXTO_BOTON_GUARDAR_ARCHIVO = "Guardar en archivo";
  private BufferedReader flujoEntrada;
  private PrintWriter flujoSalida;
  private JFileChooser selector;
  private String archivo;
  private int numeroDeLinea;
  
  public ManejadorArchivosTexto() {
    flujoEntrada = null;
    flujoSalida = null;
    selector = null;
    archivo = null;
    numeroDeLinea = 0;
  }
  
  public ManejadorArchivosTexto(String elNombreArchivo){    
    flujoSalida = null;
    selector = null;
    numeroDeLinea = 0;
    archivo = elNombreArchivo;    
        try {
            flujoEntrada = new BufferedReader(new FileReader(archivo));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
        }
  }

  public boolean eliminarArchivo(){
    File f = new File(this.archivo);
    return f.delete();
  }

  public boolean eliminarArchivo(String nombreArchivo){
    File f = new File(nombreArchivo);
    return f.delete();
  }
  
  /**
   * lee la linea indicada por paramentro y deja el puntero del lector ahi para que
   * pueda ser utilizado el método leerSiguienteLinea
   * @return 
   */
  public String leerLinea(int numLinea){
    String linea=null;
    if(archivo!=null){
            try {
                for(int i=0;i<numLinea;i++)
                    flujoEntrada.readLine();
                linea = flujoEntrada.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    return linea;
  }
  
  public String leerTodoArchivo(String elNombreArchivo){
    String lasLineas = "";
    archivo = elNombreArchivo;

    if (elNombreArchivo == null) {
      elNombreArchivo = this.obtenerNombreArchivo(MODO_LECTURA);
    }
    
    if (elNombreArchivo!=null) {
      try {
        flujoEntrada = new BufferedReader(new FileReader(elNombreArchivo));
        //System.out.println("en manejador");
        String linea = flujoEntrada.readLine();
        while (linea != null) {
          lasLineas += linea + "\n";
          linea = flujoEntrada.readLine();
        }
      } catch (IOException excepcion) {}
      
      try {  // Cierra el flujo de entrada y libera cualquier recurso del sistema asociado con �l
        if (flujoEntrada != null) {
          flujoEntrada.close();
        } 
      } catch (IOException excepcion) {}
    }    
    lasLineas = lasLineas.substring(0, lasLineas.length()-1);
    return lasLineas;
  }
  
  public void setNumeroDeLinea(int numeroDeLinea){
      this.numeroDeLinea = numeroDeLinea;
      leerLinea(numeroDeLinea - 1);
  }
  
  public int getNumeroDeLinea(){
      return numeroDeLinea;
  }
    
  /**
   * Lee la siguiente linea a partir del donde se hizo la ultima lectura
   * @return la linea leida
   */
  public String leerSiguienteLinea(){
    String linea=null;
    if(archivo!=null){
            try {
                linea = flujoEntrada.readLine();
                numeroDeLinea++;
            } catch (IOException ex) {
                Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    return linea;
  }
  // Lee de archivo un grupo de l�neas de texto
  public String[] leerLineasTexto(String elNombreArchivo) {
    String[] lasLineas = null;
    archivo = elNombreArchivo;

    if (elNombreArchivo == null) {
      elNombreArchivo = this.obtenerNombreArchivo(MODO_LECTURA);
    }
    
    if (elNombreArchivo!=null) {
      try {
        flujoEntrada = new BufferedReader(new FileReader(elNombreArchivo));
        //System.out.println("en manejador");
        lasLineas = new String[0];
        String linea = flujoEntrada.readLine();
        while (linea != null) {
          lasLineas = this.agrandarArregloLineas(lasLineas);
          lasLineas[lasLineas.length-1] = linea;
          linea = flujoEntrada.readLine();
        }
      } catch (IOException excepcion) {}
      
      try {  // Cierra el flujo de entrada y libera cualquier recurso del sistema asociado con �l
        if (flujoEntrada != null) {
          flujoEntrada.close();
        } 
      } catch (IOException excepcion) {}
    }    
    return lasLineas;
  }
  
  public String[] agrandarArregloLineas(String[] elArreglo) {
    String[] nuevoArreglo = new String[elArreglo.length + 1];
    for (int indice=0;indice<elArreglo.length;indice++) {
      nuevoArreglo[indice] = elArreglo[indice];
    }
    return nuevoArreglo;
  }

  // Guarda en archivo un grupo de líneas de texto
  public boolean guardarLineasTexto(String[] lasLineas, String elNombreArchivo, boolean append) {
    boolean guardoExitoso=true;

    if ((elNombreArchivo == null)&&(lasLineas!=null)) {
      elNombreArchivo = this.obtenerNombreArchivo(MODO_ESCRITURA);
    }

    if ((elNombreArchivo!=null) && (lasLineas!=null)) {
      try {
        flujoSalida = new PrintWriter(new FileWriter(elNombreArchivo, append));

        
        for (int indice=0;indice<lasLineas.length;indice++) {
            if (lasLineas[indice] != null)
                flujoSalida.println(lasLineas[indice]);
        }
      } catch(IOException excepcion) {
        guardoExitoso = false;
      }

      if (flujoSalida!=null) { // Cierra el flujo de salida y libera cualquier recurso del sistema asociado con �l
        flujoSalida.close();
      }
    }else {
      guardoExitoso = false;
    }
    return guardoExitoso;
  }
  
  public long getTamanno(String name){
      File file = new File(name);      
      if (!file.exists() || !file.isFile()) {
          return 0;
      }
      return file.length();
  }

    // Guarda en archivo un grupo de líneas de texto
  public boolean guardarStringLn(String texto, String elNombreArchivo, boolean append) {
    boolean guardoExitoso=true;

    if ((elNombreArchivo == null)&&(texto!=null)) {
      elNombreArchivo = this.obtenerNombreArchivo(MODO_ESCRITURA);
    }

    if ((elNombreArchivo!=null) && (texto!=null)) {
      try {
        flujoSalida = new PrintWriter(new FileWriter(elNombreArchivo, append));

        flujoSalida.println(texto);

      } catch(IOException e) {
        guardoExitoso = false;
      }

      if (flujoSalida!=null) { // Cierra el flujo de salida y libera cualquier recurso del sistema asociado con �l
        flujoSalida.close();
      }
    }else {
      guardoExitoso = false;
    }
    return guardoExitoso;
  }
    // Guarda en archivo un grupo de líneas de texto
  public boolean guardarString(String texto, String elNombreArchivo, boolean append) {
    boolean guardoExitoso=true;

    if ((elNombreArchivo == null)&&(texto!=null)) {
      elNombreArchivo = this.obtenerNombreArchivo(MODO_ESCRITURA);
    }

    if ((elNombreArchivo!=null) && (texto!=null)) {
      try {
        flujoSalida = new PrintWriter(new FileWriter(elNombreArchivo, append));

        flujoSalida.print(texto);

      } catch(IOException e) {
        guardoExitoso = false;
      }

      if (flujoSalida!=null) { // Cierra el flujo de salida y libera cualquier recurso del sistema asociado con �l
        flujoSalida.close();
      }
    }else {
      guardoExitoso = false;
    }
    return guardoExitoso;
  }
  
  private String obtenerNombreArchivo(int modo) {
    String nombreArchivo = null;
    
    selector = new JFileChooser("."); // Construye el componente se�alando al directorio en donde se ubica este programa en ejecuci�n
    selector.setFileSelectionMode(JFileChooser.FILES_ONLY); // Permite al usuario seleccionar solo archivos (no directorios)
    if (modo == MODO_LECTURA) { // Asigna r�tulo al bot�n de Abrir
      selector.setApproveButtonText(TEXTO_BOTON_ABRIR_ARCHIVO); 
    }else{
      if (modo == MODO_ESCRITURA) {
        selector.setApproveButtonText(TEXTO_BOTON_GUARDAR_ARCHIVO);
      }
    }

    int opcionAbrirCancelar = selector.showOpenDialog(null); // Abre un di�logo de apertura de archivos
    if ( opcionAbrirCancelar == JFileChooser.APPROVE_OPTION ){ // Si seleccion� el bot�n de abrir archivo
      nombreArchivo = selector.getSelectedFile().getName();
    }
    
    return nombreArchivo;
  }
  

  
}