/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

/**
 *
 * @author Aaron
 */
public class ThreadEscritor extends Thread {

    private String archivo;
    private String texto;
    private ManejadorArchivosTexto escritor;
    private boolean escribir;
    private boolean finalizarHilo;
    
    public ThreadEscritor(){
        this.escritor = new ManejadorArchivosTexto();
    }

    @Override
    public void run() {
        while (!finalizarHilo){
            if (escribir){
                System.out.println("------------------------------------------------------A imprimir bloque de " + archivo);
                escritor.guardarString(getTexto(), getArchivo(), true);
                escribir = false;
            }
            synchronized(escritor){
                try { 
                    escritor.wait(); 
                } 
                catch(Exception e){ 
                    System.err.println(e.toString()); 
                }
            }
        }
    }

    /**
     * @return the escribir
     */
    public boolean isEscribir() {
        return escribir;
    }

    /**
     * @param escribir the escribir to set
     */
    public void setEscribir(boolean escribir) {
        this.escribir = escribir;
        if (escribir){
            synchronized(escritor){                
                escritor.notify();
            }
        }
        
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(String archivo) {
        this.archivo = archivo;        
    }

    /**
     * @return the archivo
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * @return the finalizarHilo
     */
    public boolean isFinalizarHilo() {
        return finalizarHilo;
    }

    /**
     * @param finalizarHilo the finalizarHilo to set
     */
    public void setFinalizarHilo(boolean finalizarHilo) {
        this.finalizarHilo = finalizarHilo;
        if (finalizarHilo){
            synchronized (escritor){
                escritor.notify();
            }
        }
    }
}
