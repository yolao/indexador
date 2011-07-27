/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexadorri2011;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        this.escribir = false;
    }

    private synchronized void escribiendo(){
        while (!finalizarHilo){ 
            try { 
                while(!escribir && !finalizarHilo)
                    wait(); 
                if (escribir){
                    System.out.println("------------------------------------------------------A imprimir bloque de " + archivo);
                    escritor.guardarString(this.texto, this.archivo, true);
                    escribir = false;
                    notifyAll();
                    
                }
            } 
            catch(Exception e){ 
                System.err.println(e.toString()); 
            }
        }
    }
    
    @Override
    public void run() {
        escribiendo();
        /*synchronized(escritor){
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
        }*/
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
    public synchronized void escribir(String nuevoTexto, String nuevoArchivo) {
        try {
            //while (this.escribir == escribir) ;
            //(escritor){
            while(this.escribir)
                wait();
            //if(escribir==this.escribir){                
              //  System.err.println("Quieren escribir y no he terminado");
            //}
            this.texto = nuevoTexto;
            this.archivo = nuevoArchivo;                    
            this.escribir = true;
            //if (escribir){
                notify();
            //}
            //}
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadEscritor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * @param archivo the archivo to set
     */
    /*public void setArchivo(String archivo) {
        synchronized(escritor){
            this.archivo = new String(archivo);        
        }
    }*/

    /**
     * @return the archivo
     */
    /*public String getArchivo() {
        return archivo;
    }*/

    /**
     * @return the texto
     */
    /*public String getTexto() {
        return texto;
    }*/

    /**
     * @param texto the texto to set
     */
    /*public void setTexto(String texto) {
        this.texto = new String(texto);
    }*/

    /**
     * @return the finalizarHilo
     */
    public boolean isFinalizarHilo() {
        return finalizarHilo;
    }

    /**
     * @param finalizarHilo the finalizarHilo to set
     */
    public synchronized void setFinalizarHilo(boolean finalizarHilo) {
        try {
            while(escribir)
                wait();
            this.finalizarHilo = finalizarHilo;
            if (finalizarHilo){
                //synchronized (escritor){
                notify();
                
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadEscritor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
