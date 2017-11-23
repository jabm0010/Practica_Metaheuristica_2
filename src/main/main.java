/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Algoritmos.P2.Generacional;
import Algoritmos.P2.Estacionario;
import Utils.Restricciones;
import Utils.listaTransmisores;
import Utils.rangoFrec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author alumno
 */
public class main {

    public static String DIRECTORIO;
    public static String TRABAJO;
    public static Integer LINEAS;
    public static Random NUMERO;
    public static Integer SEMILLAS[] = {3181827, 1818273, 8182731, 1827318, 8273181};
    
    public static boolean cruce=false;//cruce==false ->algoritmo cruce 2 puntos
                                      //cruce==true  -> algoritmo BLX-a
    

    //Variables para el menu
    static Scanner scanner = new Scanner(System.in);
    static int select = -1;

    /**
     * @param args the command line arguments
     */
    public static void main ( String[] args ) throws FileNotFoundException, IOException {
        NUMERO = new Random();

        TRABAJO = System.getProperty("user.dir");

        System.out.println("Conjunto de archivos que quiere usar: ");
        Scanner reader = new Scanner(System.in);
        DIRECTORIO = reader.next();
        LINEAS = countLines(DIRECTORIO) + 1;

        rangoFrec frecuencias = new rangoFrec();
        listaTransmisores transmisores = new listaTransmisores();
        Restricciones rest = new Restricciones();

        float startTime;
        float endTime;
        float duration;
        int contador = 0;
        int semilla = 0;
        while( select != 0 ) {

            if ( semilla == 0 ) {
                NUMERO.setSeed(SEMILLAS[ contador ]);
            } else {
                NUMERO.setSeed(semilla);
            }

//            try {
                System.out.print("Elige opción:\n1.- Generacional"
                        + "\n2.- Estacionario\n"
                        + "3.- Elegir algoritmo de cruce\n"
                        + "4.- ----\n"
                        + "5.- ----\n"
                        + "6.- ----\n "
                        + "0.- Salir"
                        + "\n: ");

                select = Integer.parseInt(scanner.nextLine());

                switch( select ) {
                    case 1:
                        startTime = System.nanoTime();
                        Generacional generacional = new Generacional(transmisores, frecuencias, rest,cruce);
                        endTime = System.nanoTime();
                        
                        //generacional.resultadosHijos();
                        // generacional.resMejorIndividuo();
                        
                        duration = (endTime - startTime) / 1000000000;
                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
                        break;
                    case 2:
                        startTime = System.nanoTime();
                        Estacionario estacionario = new Estacionario(transmisores, frecuencias, rest,cruce);
                        endTime = System.nanoTime();
                        
                        //generacional.resultadosHijos();
                        // generacional.resMejorIndividuo();
                        
                        duration = (endTime - startTime) / 1000000000;
                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
   
                        

                        break;
                    case 3:
                        if(cruce==false){
                            cruce=true;
                            System.out.println("Algoritmo cambiado a BLX-a ");
                        }else{
                            cruce=false;
                            System.out.println("Algoritmo cambiado a cruce en 2 puntos");
                        }

                        break;
                    case 4:
//                        startTime = System.nanoTime();
//                        Grasp grasp = new Grasp(transmisores, frecuencias, rest);
//                        grasp.algoritmo();
//                        endTime = System.nanoTime();
//
//                        grasp.resultados();
//                        duration = (endTime - startTime) / 1000000000;
//                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
                        break;
                    case 5:
                        System.out.println("Conjunto de archivos que quiere usar: ");

                        DIRECTORIO = reader.next();
                        LINEAS = countLines(DIRECTORIO) + 1;

                        frecuencias = new rangoFrec();
                        transmisores = new listaTransmisores();
                        rest = new Restricciones();
                        break;
                    case 6:
                        System.out.print("Nueva semilla: ");
                        semilla = reader.nextInt();
                        NUMERO.setSeed(semilla);
                        break;
                    case 0:
                        System.out.println("Fin");
                        break;
                    default:
                        System.out.println("Número no reconocido");
                        break;
                }

                System.out.println("\n"); //Mostrar un salto de línea en Java

//            } catch( Exception e ) {
//                System.out.println("Uoop! Error! " + e.toString());
//            }
            contador = Math.floorMod(contador + 1, 5);
        }
    }

    public static int countLines ( String filename ) throws IOException {
        String archivo = "var.txt";
        if ( filename.matches("scen.*") ) {
            archivo = archivo.toUpperCase();
        }
        File file = new File(main.TRABAJO + "/conjuntos/" + main.DIRECTORIO + "/" + archivo);
        Scanner lineas = new Scanner(file);
        int ultimoTransmisor = 0;
        while( lineas.hasNextLine() ) {
            ultimoTransmisor = lineas.nextInt();
            String ultimaLinea = lineas.nextLine();
        }
        return ultimoTransmisor;
    }

}
