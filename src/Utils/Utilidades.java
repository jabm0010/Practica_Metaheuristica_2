/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ptondreau
 */
public class Utilidades {
    
            /**
     * Funcion que devuelve una lista con las restricciones que puede satisfacer
     * un transmisor
     *
     * @param transmisor
     * @return
     * @throws FileNotFoundException
     */
    public static List<List<Integer>> compruebaTransmisores ( int transmisor, Restricciones restricciones, 
            List<Integer> frecuenciasR ) throws FileNotFoundException 
    {
        int contador = 0;
        List<List<Integer>> listaRest = new ArrayList<>();
        List<List<Integer>> listaT = restricciones.restriccionesTransmisor(transmisor);
        for ( int i = 0; i < listaT.size(); i ++ ) {
            if ( frecuenciasR.get(listaT.get(i).get(0) - 1) != 0 || frecuenciasR.get(listaT.get(i).get(1) - 1) != 0 ) {
                listaRest.add(new LinkedList<>());
                listaRest.get(contador ++).addAll(listaT.get(i));
            }
        }

        return listaRest;
    }
    
    public static int rDiferencia ( List<Integer> valores, List<List<Integer>> rest ) {
        int total = 0;
        for ( int i = 0; i < rest.size(); i ++ ) {
            int tr1 = rest.get(i).get(0);
            int tr2 = rest.get(i).get(1);
            int diferencia = rest.get(i).get(2);
            int result = rest.get(i).get(3);

            if ( Math.abs(valores.get(tr1 - 1) - valores.get(tr2 - 1)) > diferencia ) {
                total += result;
            }

        }

        return total;
    }

    public static int rDiferencia ( List<Integer> valores, Restricciones rest ) throws FileNotFoundException {

        int total = 0;
        for ( int i = 0; i < rest.restricciones.size(); i ++ ) {
            int tr1 = rest.restricciones.get(i).get(0);
            int tr2 = rest.restricciones.get(i).get(1);
            int diferencia = rest.restricciones.get(i).get(2);
            int result = rest.restricciones.get(i).get(3);

            if ( Math.abs(valores.get(tr1 - 1) - valores.get(tr2 - 1)) > diferencia ) {
                total += result;
            }

        }

        return total;
    }

    /**
     * Calcula el resultado del problema a minimizar
     *
     * @param valores Valores de los transmisores
     * @param cambioTransmisor Transmisor al que se le aplico un cambio de
     * frecuencia
     * @param rest Restricciones a evaluar
     * @return
     * @throws FileNotFoundException
     */
    public static int rDiferencia ( List<Integer> valores, int cambioTransmisor, Restricciones rest ) throws FileNotFoundException {

        List<List<Integer>> listaRest = new ArrayList<>();
        listaRest = rest.restriccionesTransmisor(cambioTransmisor);

        int total = 0;
        for ( int i = 0; i < listaRest.size(); i ++ ) {

            int tr1 = listaRest.get(i).get(0);
            int tr2 = listaRest.get(i).get(1);

            if ( tr1 == cambioTransmisor + 1 || tr2 == cambioTransmisor + 1 ) {
                int diferencia = listaRest.get(i).get(2);
                int result = listaRest.get(i).get(3);

                if ( Math.abs(valores.get(tr1 - 1) - valores.get(tr2 - 1)) > diferencia ) {
                    total += result;
                }

            }

        }

        return total;
    }
    
}
