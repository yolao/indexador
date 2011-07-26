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
    public boolean escribir;
    private boolean finalizarHilo;
    private Object semaforo;
    
    public ThreadEscritor(){
        this.escritor = new ManejadorArchivosTexto();
        this.escribir = true;
    }

    private synchronized void escribiendo(){
        while (!finalizarHilo){ 
            try { 
                wait(); 
                if (escribir){
                    System.out.println("------------------------------------------------------A imprimir bloque de " + archivo);
                    escritor.guardarString(getTexto(), getArchivo(), true);
                    escribir = false;
                    System.out.println("puse el escribir en false... escribir ="+escribir);
                }
            } 
            catch(Exception e){ 
                System.err.println(e.toString()); 
            }
        }
    }
    
    @Override
    public void run() {
        //escribiendo();
        synchronized(escritor){
            while (!finalizarHilo){ 
                try { 
                    escritor.wait(); 
                    if (escribir){
                        System.out.println("------------------------------------------------------A imprimir bloque de " + archivo);
                        escritor.guardarString(getTexto(), getArchivo(), true);
                        escribir = false;
                    }
                } 
                catch(Exception e){ 
                    System.err.println(e.toString()); 
                }
            }
        }
    }

    public synchronized void escribir(boolean escribir, String texto, String archivo){
        if(escribir==this.escribir){                
            System.out.println("Quieren escribir y no he terminado");
        }
        this.escribir = escribir;
        this.texto = new String (texto);
        this.archivo = new String (archivo);
        if (escribir){
            escritor.notify();
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
        //while (this.escribir == escribir) ;
        synchronized(escritor){
            if(escribir==this.escribir){                
                System.err.println("Quieren escribir y no he terminado");
            }
            this.escribir = escribir;
            if (escribir){
                escritor.notify();
            }
        }
        
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(String archivo) {
        synchronized(escritor){
            this.archivo = new String(archivo);        
        }
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
        this.texto = new String(texto);
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
