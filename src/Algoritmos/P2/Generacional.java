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
    static int convergenciaSol=40; // 80% de 50
    static double alfa=0.5;
    
    boolean cruce=false;

    List<List<Integer>> frecuencias = new ArrayList<>();
    List<Integer> transmisores = new ArrayList<>();
    List<Integer> frecuenciasR = new ArrayList<>();
    Restricciones restricciones;
    int[] resultado = new int[50];
    int[] resultadoHijos = new int[50];
    List<List<Integer>> padres = new ArrayList<>();
    List<List<Integer>> hijos = new ArrayList<>();

    int numEvaluaciones = 0;
    
    boolean reinicializar=false;

    public Generacional(listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest,boolean _cruce) throws FileNotFoundException {
        cruce= _cruce;
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
        

        int numGeneraciones=0;
        int ultimoResultado=0;
        List<Integer> ultimoResultadoL=new ArrayList<>();
        int resultadoActual=Integer.MAX_VALUE;
        List<Integer> resultadoActualL=new ArrayList<>();
        
        
        do{
       
            System.out.println(numEvaluaciones);
            System.out.println(numGeneraciones);
           
            //Loop hasta 20000 evaluaciones
            generarHijos();
            System.out.println("Hijos generados");
            cruzarIndividuos();
            System.out.println("Hijos cruzados");
            int evalu=mutarIndividuos();
            System.out.println("Hijos mutados");

            nuevaGeneracion(evalu);
            System.out.println("Nueva generación");
            
            //Buscamos el mínimo 
            for(int i=0;i<50;i++){
                if(resultado[i]<resultadoActual){
                    resultadoActual=resultado[i];
                    resultadoActualL=padres.get(i);
                }
            }
            //Reinicialización: 80% de la población es igual
            //comprobarConvergencia1() solo comprueba en función de resultado[]. En caso de que
            //haya 80% de valores iguales entonces procedemos a comprobarConvergencia2() que ya
            //si que comprueba con las listas y los valores de transmisores asignados
            
            if(comprobarConvergencia1()){
                if(comprobarConvergencia2()){
                   reinicializar(resultadoActualL,resultadoActual);
                }
            }
            
            
            //Reinicialización: 20 generaciones con mismo mejor resultado
            if(resultadoActual==ultimoResultado){
                if(ultimoResultadoL.equals(resultadoActualL));
                numGeneraciones++;
            }else{
                numGeneraciones=0;
            }
            
            if(numGeneraciones==20){
                System.out.println("Reinicialización");
                reinicializar(resultadoActualL,resultadoActual);
                numGeneraciones=0;
            }
            
            ultimoResultado=resultadoActual;
            ultimoResultadoL=resultadoActualL;
            
            System.out.println(ultimoResultado);
            
        } while(numEvaluaciones<20000);
        
        
        for (int i = 0; i < transmisores.size() - 1; i++) {
            System.out.println("Transmisor " + (i + 1) + ": " + ultimoResultadoL.get(i));
        }
        
        System.out.println(ultimoResultado);
        
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

    //Funciona bien
    void generarHijos() {
        int cont = 0;
        while (cont < 50) {
            Random numero = NUMERO;
            int seleccionado = numero.nextInt(50);
            // System.out.println(seleccionado+":"+resultado[seleccionado]);

            Random numero2 = NUMERO;
            int seleccionado2 = numero.nextInt(50);
            //  System.out.println(seleccionado2+":"+resultado[seleccionado2]);

            if (resultado[seleccionado] < resultado[seleccionado2]) {
                hijos.add(cont, padres.get(seleccionado));
                //     System.out.println(seleccionado);

            } else {
                hijos.add(cont, padres.get(seleccionado2));
                //    System.out.println(seleccionado2);
            }
            cont++;

        }
    }

    void cruzarIndividuos() {
        int cont = 0;
        while (cont < numParejas) {
            int individuo1 = cont;
            int individuo2 = cont + 1;
            
            if(cruce==false){
            algCruce2Puntos(individuo1, individuo2);
            }else{
                algBX(individuo1, individuo2);
            }
            cont += 2;
            //System.out.println(cont);
        }
        
        

    }

    //Funciona bien
    /*
    Ahora devuelve un entero, para controlar el numero de evaluaciones total
    */
    
    int mutarIndividuos() {
        //Mutamos solo un individuo

        //Seleccionamos el individuo a mutar
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(50);

        int transmisorMut = numero.nextInt(transmisores.size());
        int frecAsociada = transmisores.get(transmisorMut);

        int frecuenciaMut = numero.nextInt(frecuencias.get(frecAsociada).size());
        hijos.get(seleccionado).set(transmisorMut, frecuencias.get(frecAsociada).get(frecuenciaMut));
        
        return seleccionado;

        // System.out.println(seleccionado+"-"+transmisorMut+"- "+frecuenciaMut);
//        //mutamos k genes, k= 0,1
//        //esperanza matematica= 0,1*numTransmisores
//        double numMutar = 0.1 * transmisores.size();
//
//        //Para 200 transmisores mutaría los 20 primeros 
//        for (int i = 0; i < numMutar; i++) {
//            int frecAsociada = transmisores.get(i);
//            int nuevaFrecuencia = numero.nextInt(frecuencias.get(frecAsociada).size());
//            hijos.get(seleccionado).set(i, frecuencias.get(frecAsociada).get(nuevaFrecuencia));
//        }
    }

    void algBX(int individuo1, int individuo2) {

        //http://www.tomaszgwiazda.com/blendX.htm
        
        System.out.println("BLX");
        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();
        
        for(int i=0;i<transmisores.size();i++){
            int d=Math.abs(hijos.get(individuo1).get(i)-hijos.get(individuo2).get(i));
            int cmin=Integer.MAX_VALUE;
            int cmax=Integer.MIN_VALUE;
            
            if(hijos.get(individuo1).get(i)<hijos.get(individuo2).get(i)){
                cmin=hijos.get(individuo1).get(i);
            }else{
                cmin=hijos.get(individuo2).get(i);
            }
            
            if(hijos.get(individuo1).get(i)>hijos.get(individuo2).get(i)){
                cmax=hijos.get(individuo1).get(i);
            }else{
                cmax=hijos.get(individuo2).get(i);
            }
   
            double vmin=cmin-d*alfa;
            double vmax=cmax+d*alfa;
            
            int frecAsociada=transmisores.get(i);
            
           //Para la solución 1
            int valorObtenido=(int)Math.floor(Math.random()*(vmax-vmin+1)+vmin);
            int minimaDiferencia=Integer.MAX_VALUE;
            int frecuenciaFinal=0;
            
            for(int j=0;j<frecuencias.get(frecAsociada).size();j++){
                if(Math.abs(valorObtenido-frecuencias.get(frecAsociada).get(j))<minimaDiferencia){
                    frecuenciaFinal=frecuencias.get(frecAsociada).get(j);
                }
            }
            
            solucion1.add(i, frecuenciaFinal);

            //Para la solución 2
            int valorObtenido2=(int)Math.floor(Math.random()*(vmax-vmin+1)+vmin);
            int minimaDiferencia2=Integer.MAX_VALUE;
            int frecuenciaFinal2=0;
            
            for(int j=0;j<frecuencias.get(frecAsociada).size();j++){
                if(Math.abs(valorObtenido2-frecuencias.get(frecAsociada).get(j))<minimaDiferencia2){
                    frecuenciaFinal2=frecuencias.get(frecAsociada).get(j);
                }
            }
            
            solucion2.add(i, frecuenciaFinal2);
       
            
            
            
            
        }
        
        hijos.set(individuo1,solucion1);
        hijos.set(individuo2,solucion2);
        
    }

    //Funciona bien
    void algCruce2Puntos(int individuo1, int individuo2) {
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(transmisores.size());
        //System.out.println(seleccionado);

        Random numero2 = NUMERO;
        int seleccionado2 = numero.nextInt(transmisores.size());
        //System.out.println(seleccionado2);
        if (seleccionado2 < seleccionado) {
            int temp = seleccionado;
            seleccionado = seleccionado2;
            seleccionado2 = temp;

        }
//        System.out.println("Individuo 1");
//        for (int i = 0; i < hijos.get(individuo1).size(); i++) {
//            System.out.println(hijos.get(individuo1).get(i));
//
//        }
//        System.out.println("Individuo 2");
//        for (int i = 0; i < hijos.get(individuo2).size(); i++) {
//            System.out.println(hijos.get(individuo2).get(i));
//
//        }

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
//        System.out.println("Solucion 1");
//        for (int i = 0; i < solucion1.size(); i++) {
//            System.out.println(solucion1.get(i));
//
//        }

        hijos.set(individuo1, solucion1);

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

        hijos.set(individuo2, solucion2);
    }

    public void nuevaGeneracion(int evalu) throws FileNotFoundException {
        //Elitismo

        //Buscamos el mejor individuo de la generación de padres
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for (int i = 0; i < 50; i++) {
            if (resultado[i] < minimo) {
                minimo = resultado[i];
                actual = i;
            }
        }
//
//        for (int i = 0; i < 50; i++) {
//            System.out.println(resultado[i]);
//        }

       // System.out.println("Individuo padre:" + minimo + " : " + actual);

        List<Integer> mejorIndividuo = padres.get(actual);

        //Evaluamos los hijos
       
        if(evalu<=36){
        resultadoHijos = evaluar(hijos,36);
        numEvaluaciones+=36;
        }else{
           
            resultadoHijos=evaluar(hijos,evalu);
            numEvaluaciones+=37;
        }
        
        for(int i=36;i<50;i++){
            if(i!=evalu){
                resultadoHijos[i]=resultado[i];
            }
        }
//        for (int i = 0; i < 50; i++) {
//            System.out.println(resultadoHijos[i]);
//        }

        //Buscamos el hijo con el mayor coste
        int maximo = Integer.MIN_VALUE;
        int actual2 = 0;
        for (int i = 0; i < 50; i++) {
            if (resultadoHijos[i] > maximo) {
                maximo = resultadoHijos[i];
                actual2 = i;
            }
        }

        //System.out.println("Individuo hijo:" + maximo + " : " + actual2);

        //Si el menor de los padres tiene menor coste que el mayor de los hijos se reemplaza
        if (minimo < maximo) {
            hijos.set(actual2, mejorIndividuo);
            resultadoHijos[actual2] = minimo;

        }

//        for (int i = 0; i < 50; i++) {
//            System.out.println(resultadoHijos[i]);
//        }

        //Los hijos serán los padres para la siguiente generación
        padres.clear();
        padres.addAll(hijos);
        hijos.clear();

        resultado = resultadoHijos;

    }

    public int[] evaluar(List<List<Integer>> individuos,int tama) throws FileNotFoundException {
        int[] nRes = new int[50];
        for (int i = 0; i < 36; i++) {
            int valor = rDiferencia(individuos.get(i), restricciones);
            nRes[i] = valor;
        }
        if(tama>=36){
            nRes[tama]= rDiferencia(individuos.get(tama), restricciones);
        }

        //numEvaluaciones+=50;  
        return nRes;
    }
    
    public boolean comprobarConvergencia1(){
        
        for(int i=0;i<50;i++){
            
            int contador=0;
            
            for(int j=0;j<50;j++){
               if(resultado[i]==resultado[j]){
                   contador++;
               }
               if(contador==convergenciaSol){
                   return true;
               }
            }
        }
        return false;
    }
    
    public boolean comprobarConvergencia2(){
        for(int i=0;i<50;i++){
            
            int contador=0;
            
            for(int j=0;j<50;j++){
               if(padres.get(i)==padres.get(j)){
                   contador++;
               }
               if(contador==convergenciaSol){
                   return true;
               }
            }
        }
        return false;
    }
    
    public void reinicializar(List<Integer> mejorResultadoL, int mejorResultado)throws FileNotFoundException{
        padres.clear();
        for (int i = 0; i < 50; i++) {
            padres.add(new ArrayList<>());
        }
        
        padres.add(0, mejorResultadoL);
        resultado[0]=mejorResultado;
        
        for(int i=1;i<50;i++){
            greedyInicial(i);
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

    public void resMejorIndividuo() throws FileNotFoundException {
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for (int i = 0; i < 50; i++) {
            if (resultado[i] < minimo) {
                minimo = resultado[i];
                actual = i;
            }
        }
        List<Integer> mejorIndividuo = padres.get(actual);

        for (int i = 0; i < transmisores.size() - 1; i++) {
            System.out.println("Transmisor " + (i + 1) + ": " + padres.get(actual).get(i));
        }

        int a = rDiferencia(mejorIndividuo, restricciones);

        //System.out.println(resultado[actual]);
        System.out.println(a);
    }
}
