package indexadorri2011;

// Código reutilizado. Fuente Pablo López Gutiérrez

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader; // clase para lectura de archivos de texto
import java.io.BufferedReader; // clase para lectura de flujos de texto en forma agrupada (arreglos, l�neas, etc.)
import java.io.File;
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
  private JFileChooser selector;
  private String archivo;
  private int numeroDeLinea;
    private FileOutputStream os;
  
  public ManejadorArchivosTexto() {
    flujoEntrada = null;
    selector = null;
    archivo = null;
    numeroDeLinea = 0;
  }
  
  public ManejadorArchivosTexto(String elNombreArchivo){    
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
      String[] lasLineas=null;
      
        try {            
            archivo = elNombreArchivo;
            String contenido = this.leerTodoArchivo(elNombreArchivo,null);
            lasLineas = contenido.split("\n");
        } catch (IOException ex) {
            Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lasLineas;
  }
  
  /* Guarda en archivo un grupo de líneas de texto
   * Luego de terminar de escribir el archivo se debe llamar al metodo cerrar()
   */
  public boolean guardarLineasTexto(String[] lasLineas, String elNombreArchivo, boolean append) {
    boolean guardoExitoso=true;
        
    if ((elNombreArchivo == null)&&(lasLineas!=null)) {
      elNombreArchivo = this.obtenerNombreArchivo(MODO_ESCRITURA);
    }

    if ((elNombreArchivo!=null) && (lasLineas!=null)) {
          String texto="";
          for(int i = 0; i < lasLineas.length;i++)
              texto += lasLineas[i] + "\n";
          
          if(!(new File(elNombreArchivo)).exists()){
              this.crearArchivo(elNombreArchivo, texto);
          }else if(append){
              this.agregarAlArchivo(texto);
          }else {
              this.crearArchivo(elNombreArchivo, texto);
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
  
  /**
   * 
   * @param archivo
   * @param salto
   * @param numLineas
   * @return lineas leidas
   */
  public String[] leerLineasDesdePosicion(String archivo, long salto, int numLineas){
      String[] lineas = null;
      if(archivo!=null){
           try {
               flujoEntrada = new BufferedReader(new FileReader(archivo));
               long s = flujoEntrada.skip(salto);
               
               lineas = new String[numLineas];
               for (int i=0; i < numLineas; ++i)
                   lineas[i] = flujoEntrada.readLine();
               flujoEntrada.close();
           }
           catch (IOException ex) {
               Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
           }           
      }      
      return lineas;
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
  


    public String leerTodoArchivo(String fileName, String charsetName)throws java.io.IOException {    
        java.io.InputStream is = new java.io.FileInputStream(fileName);                    
        try {    
            final int bufsize = 60096;    
            int available = is.available();    
            byte data[] = new byte[available < bufsize ? bufsize : available];    
            int used = 0;    
            while (true) {    
                if (data.length - used < bufsize) {    
                    byte newData[] = new byte[data.length << 1];    
                    System.arraycopy(data, 0, newData, 0, used);    
                    data = newData;    
                }    
                int got = is.read(data, used, data.length - used);    
                if (got <= 0) break;    
                    used += got;    
            }    
            return charsetName != null ? new String(data, 0, used, charsetName)    
                               : new String(data, 0, used);    
        } finally {
            is.close();  
        }  
    }
    
    public void crearArchivo(String fileName, String datos){
        try {
            os = new java.io.FileOutputStream(fileName);
            agregarAlArchivo(datos);
        } catch (IOException ex) {
            Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void agregarAlArchivo(String datos){
        try {            
            os.write(datos.getBytes(), 0, datos.getBytes().length);            
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cerrarArchivo(){
        try {
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(ManejadorArchivosTexto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}