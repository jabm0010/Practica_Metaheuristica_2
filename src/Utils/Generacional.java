/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.P2;

import Utils.Restricciones;
import static Utils.Utilidades.*;
import Utils.listaTransmisores;
import Utils.rangoFrec;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;

/**
 *
 * @author ptondreau
 */
public class Generacional {

    static int numParejas = 18;

    List<List<Integer>> frecuencias = new ArrayList<>();
    List<Integer> transmisores = new ArrayList<>();
    List<Integer> frecuenciasR = new ArrayList<>();
    Restricciones restricciones;
    int[] resultado = new int[50];
    int[] resultadoHijos = new int[50];
    List<List<Integer>> padres = new ArrayList<>();
    List<List<Integer>> hijos = new ArrayList<>();

    public Generacional(listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest) throws FileNotFoundException {
        frecuencias = _frecuencias.rangoFrecuencias;
        transmisores = _transmisores.transmisores;
        restricciones = _rest;

        for (int i = 0; i < 50; i++) {
            padres.add(new ArrayList<>());
        }

        for (int i = 0; i < 50; i++) {
            //System.out.println(i);
            greedyInicial(i);
        }

        generarHijos();
        System.out.println("Hijos generados");
        cruzarIndividuos();
        System.out.println("Hijos cruzados");
        mutarIndividuos();
        System.out.println("Hijos mutados");

    }

    void greedyInicial(int id) throws FileNotFoundException {

        for (int i = 0; i < transmisores.size(); i++) {
            frecuenciasR.add(0);
        }

        Random numero = NUMERO;
        int seleccionado = numero.nextInt(transmisores.size());

        int tamanio = frecuencias.get(transmisores.get(seleccionado)).size();
        int frecuenciaRandom = frecuencias.get(transmisores.get(seleccionado)).get(numero.nextInt(tamanio));
        frecuenciasR.set(seleccionado, frecuenciaRandom);

        // System.out.println("Transmisor seleccionado: "+seleccionado);
        List<List<Integer>> listaRestric = new ArrayList<>();
        int transmisor = 0;
        boolean fin = false;
        while (transmisor < transmisores.size()) {
            listaRestric = restricciones.restriccionesTransmisor(transmisor);
            if (transmisor != seleccionado && listaRestric.size() > 0) {

                int minimo = Integer.MAX_VALUE;
                boolean encontrado = false;
                int frecuenciaR = 0;
                int frecuencia;
                int pos = 0;

                int valor = 0; //Sacado del bucle while

                while (pos < frecuencias.get(transmisores.get(transmisor)).size() && !encontrado) {

                    List<Integer> nuevaLista = new ArrayList<>();
                    nuevaLista.addAll(frecuenciasR);

                    frecuencia = frecuencias.get(transmisores.get(transmisor)).get(pos);
                    nuevaLista.set(transmisor, frecuencia);
                    List<List<Integer>> listaRest = compruebaTransmisores(transmisor, restricciones, frecuenciasR);

                    if (listaRest.size() > 0) { // Lista no vacía, se selecciona frecuencia que afecte lo menos posible al resultado

                        valor = rDiferencia(nuevaLista, listaRest);
                        if (valor < minimo) {
                            minimo = valor;
                            frecuenciaR = frecuencia;
                            if (valor == 0) // Si la suma de todas las restricciones = 0 entonces es el mejor resultado posible
                            {
                                encontrado = true;
                            }
                        }
                    } else { // En caso de que la lista este vacía no hay restricciones que se puedan satisfacer -> frecuencia aleatoria

                        tamanio = frecuencias.get(transmisores.get(transmisor)).size();
                        frecuenciaR = frecuencias.get(transmisores.get(transmisor)).get(numero.nextInt(tamanio));
                        valor = 0;
                        encontrado = true;
                    }
                    pos++;
                }
                frecuenciasR.set(transmisor, frecuenciaR);
            }
            transmisor++;
        }
        resultado[id] = rDiferencia(frecuenciasR, restricciones);
        padres.get(id).addAll(frecuenciasR);
        frecuenciasR.clear(); // Borra todos los elementos anteriores para nueva solucion
    }

    void generarHijos() {
        int cont = 0;
        while (cont < 50) {
            Random numero = NUMERO;
            int seleccionado = numero.nextInt(50);

            Random numero2 = NUMERO;
            int seleccionado2 = numero.nextInt(50);

            if (resultado[seleccionado] < resultado[seleccionado2]) {
                hijos.add(cont, padres.get(seleccionado));

            } else {
                hijos.add(cont, padres.get(seleccionado2));
            }
            cont++;

        }
    }

    void cruzarIndividuos() {
        int cont = 0;
        while (cont < numParejas) {
            int individuo1 = cont;
            int individuo2 = cont + 1;

            algCruce2Puntos(individuo1, individuo2);
            cont += 2;
        }

    }

    // No estoy seguro de si habría que hacerla así.
    void mutarIndividuos() {
        //Mutamos solo un individuo

        //Seleccionamos el individuo a mutar
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(50);

        //mutamos k genes, k= 0,1
        //esperanza matematica= 0,1*numTransmisores
        double numMutar = 0.1 * transmisores.size();

        //Para 200 transmisores mutaría los 20 primeros 
        for (int i = 0; i < numMutar; i++) {
            int frecAsociada = transmisores.get(i);
            int nuevaFrecuencia = numero.nextInt(frecuencias.get(frecAsociada).size());
            hijos.get(seleccionado).set(i, frecuencias.get(frecAsociada).get(nuevaFrecuencia));
        }

    }

    void algCruce2Puntos(int individuo1, int individuo2) {
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(transmisores.size());

        Random numero2 = NUMERO;
        int seleccionado2 = numero.nextInt(transmisores.size());

        if (seleccionado2 < seleccionado) {
            int temp = seleccionado;
            seleccionado = seleccionado2;
            seleccionado2 = temp;

        }

        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();

        //Primer cruce
        for (int i = 0; i < seleccionado; i++) {
            solucion1.add(i, hijos.get(individuo1).get(i));
        }
        for (int i = seleccionado; i < seleccionado2; i++) {
            solucion1.add(i, hijos.get(individuo2).get(i));
        }
        for (int i = seleccionado2; i < transmisores.size(); i++) {
            solucion1.add(i, hijos.get(individuo1).get(i));
        }

        //Segundo cruce
        for (int i = 0; i < seleccionado; i++) {
            solucion2.add(i, hijos.get(individuo2).get(i));
        }
        for (int i = seleccionado; i < seleccionado2; i++) {
            solucion2.add(i, hijos.get(individuo1).get(i));
        }
        for (int i = seleccionado2; i < transmisores.size(); i++) {
            solucion2.add(i, hijos.get(individuo2).get(i));
        }

    }

    public void resultados() {
        for (int i = 0; i < 50; i++) {
            System.out.println("------------------" + (i + 1) + "------------------");
            for (int j = 0; j < transmisores.size() - 1; j++) {
                System.out.println("Transmisor " + (j + 1) + ": " + padres.get(i).get(j));
            }
            System.out.println(resultado[i]);
        }
    }

    public void resultadosHijos() {
        for (int i = 0; i < 50; i++) {
            System.out.println("------------------" + (i + 1) + "------------------");
            for (int j = 0; j < transmisores.size() - 1; j++) {
                System.out.println("Transmisor " + (j + 1) + ": " + hijos.get(i).get(j));
            }
            //System.out.println(resultado[i]);
        }
    }
}
