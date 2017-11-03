/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import main.main;

/**
 * Estructura de datos que almacena las restricciones que deben satisfacer los
 * transmisores
 *
 * @author juan
 */
public class Restricciones {

    public LinkedList<LinkedList<Integer>> restricciones = new LinkedList<>();
    private List<List<List<Integer>>> resTransmisor;

    public Restricciones () throws FileNotFoundException {
        int contador = 0;
        String dato = "ctr.txt";
        if ( main.DIRECTORIO.matches("scen.*") ) {
            dato = dato.toUpperCase();
        }
        File fichero = new File(main.TRABAJO + "/conjuntos/" + main.DIRECTORIO + "/" + dato);
        Scanner lectura = new Scanner(fichero);
        int[] contadores = new int[ main.LINEAS ];
        Arrays.fill(contadores, 0);

        resTransmisor = new ArrayList<>();
        for ( int i = 0; i < main.LINEAS; i ++ ) {
            resTransmisor.add(new ArrayList<>());
        }

        while( lectura.hasNextLine() ) {
            String linea = lectura.nextLine();
            if ( linea.matches("(.*C.*)") ) {
                Scanner sLinea = new Scanner(linea);
                while( sLinea.hasNextInt() ) {

                    int tr1 = sLinea.nextInt();
                    int tr2 = sLinea.nextInt();
                    sLinea.next();
                    sLinea.next();
                    int diferencia = sLinea.nextInt();
                    int result = sLinea.nextInt();

                    LinkedList<Integer> datos = new LinkedList<>();
                    datos.add(0, tr1);
                    datos.add(1, tr2);
                    datos.add(2, diferencia);
                    datos.add(3, result);

                    restricciones.add(contador, datos);

                    resTransmisor.get(tr1 - 1).add(new ArrayList());
                    resTransmisor.get(tr1 - 1).get(contadores[ tr1 - 1 ] ++).addAll(datos);
                    resTransmisor.get(tr2 - 1).add(new ArrayList());
                    resTransmisor.get(tr2 - 1).get(contadores[ tr2 - 1 ] ++).addAll(datos);
                    contador ++;
                }
                sLinea.close();

            }
        }
        lectura.close();

    }

    /**
     * Funcion que devuelve las restricciones de un transmisor dado
     *
     * @param transmisor
     * @return
     */
    public List<List<Integer>> restriccionesTransmisor ( int transmisor ) {
        List<List<Integer>> restriccionesTransmisor = new ArrayList<>();

        restriccionesTransmisor = resTransmisor.get(transmisor);

        return restriccionesTransmisor;
    }

    /**
     * Devuelve por pantalla todas las restricciones de todos los transmisores
     */
    public void leerResultados () {
        for ( int i = 0; i < restricciones.size(); i ++ ) {
            System.out.println(restricciones.get(i).get(0) + " " + restricciones.get(i).get(1) + " "
                    + restricciones.get(i).get(2) + " " + restricciones.get(i).get(3));
        }
    }
}
