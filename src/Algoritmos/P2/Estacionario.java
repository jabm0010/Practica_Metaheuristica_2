/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.P2;

import static Algoritmos.P2.Generacional.alfa;
import Utils.Restricciones;
import static Utils.Utilidades.*;
import Utils.listaTransmisores;
import Utils.rangoFrec;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;

/**
 *
 * @author ptondreau
 */
public class Estacionario {

    static int numParejas = 1;
    static int convergenciaSol=40; // 80% de 50
    
    static double probMutacion=0.02;
    
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

    public Estacionario(listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest,boolean _cruce) throws FileNotFoundException {
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

           
            //Loop hasta 20000 evaluaciones
            generarHijos();
            System.out.println("Hijos generados");
            cruzarIndividuos();
            System.out.println("Hijos cruzados");
            mutarIndividuos();
            System.out.println("Hijos mutados");

            nuevaGeneracion();
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
        while (cont < 2) {
            Random numero = NUMERO;
            int seleccionado = numero.nextInt(50);
             System.out.println(seleccionado+":"+resultado[seleccionado]);

            Random numero2 = NUMERO;
            int seleccionado2 = numero.nextInt(50);
              System.out.println(seleccionado2+":"+resultado[seleccionado2]);

            if (resultado[seleccionado] < resultado[seleccionado2]) {
                hijos.add(cont, padres.get(seleccionado));
                    System.out.println(seleccionado);

            } else {
                hijos.add(cont, padres.get(seleccionado2));
                   System.out.println(seleccionado2);
            }
            cont++;

        }
    }

    void cruzarIndividuos() {

            int individuo1 = 0;
            int individuo2 = 1;
            
            if(cruce==false){
            algCruce2Puntos(individuo1, individuo2);
            }else{
                algBX(individuo1, individuo2);
            }
            
            //System.out.println(cont);
        
        
        

    }

    //Funciona bien
    /*
    Ahora devuelve un entero, para controlar el numero de evaluaciones total
    Cambios para el estacionario:
    -Si se muta el individuo 0, devuelve 0
    -Si se muta el individuo 1, devuelve 1
    -Si no se muta ninguno, devuelve -1
    */
    
    void mutarIndividuos() {
        //Mutamos solo un individuo

        //Seleccionamos el individuo a mutar
        Random n=NUMERO;
        
 
        
        System.out.println("Mutación");
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(2);

        int transmisorMut = numero.nextInt(transmisores.size());
        int frecAsociada = transmisores.get(transmisorMut);

        int frecuenciaMut = numero.nextInt(frecuencias.get(frecAsociada).size());
        hijos.get(seleccionado).set(transmisorMut, frecuencias.get(frecAsociada).get(frecuenciaMut));
        

        
        
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
        
        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();
        
        for(int i=0;i<transmisores.size();i++){
            int d=Math.abs(hijos.get(individuo1).get(i)-hijos.get(individuo2).get(i));
           // System.out.println("Diferencia: "+d);
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
   
            //System.out.println("Maximo: "+cmax);
            //System.out.println("Minimo: "+cmin);
            
            double vmind=cmin-d*alfa;
            double vmaxd=cmax+d*alfa;
            int vmin=(int)vmind;
            int vmax=(int)vmaxd;
            
            //System.out.println("Limite inferior:"+vmin);
            //System.out.println("Limite superior:"+vmax);
            
            int frecAsociada=transmisores.get(i);
            //System.out.println("Frecuencia asociada:"+frecAsociada);
            
           //Para la solución 1
           Random n=NUMERO;
           int valorObtenido=n.nextInt(vmax+1)+vmin;
           
            //System.out.println(valorObtenido);
            int minimaDiferencia=Integer.MAX_VALUE;
            int frecuenciaFinal=0;
            
            for(int j=0;j<frecuencias.get(frecAsociada).size();j++){
                if(Math.abs(valorObtenido-frecuencias.get(frecAsociada).get(j))<minimaDiferencia){
                     minimaDiferencia=Math.abs(valorObtenido-frecuencias.get(frecAsociada).get(j));
                    frecuenciaFinal=frecuencias.get(frecAsociada).get(j);
                }
            }
            //System.out.println(frecuenciaFinal);
            
            solucion1.add(i, frecuenciaFinal);

            //Para la solución 2
            int valorObtenido2=(int)Math.floor(Math.random()*(vmax-vmin+1)+vmin);
            int minimaDiferencia2=Integer.MAX_VALUE;
            int frecuenciaFinal2=0;
            
            for(int j=0;j<frecuencias.get(frecAsociada).size();j++){
                if(Math.abs(valorObtenido2-frecuencias.get(frecAsociada).get(j))<minimaDiferencia2){
                    minimaDiferencia2=Math.abs(valorObtenido2-frecuencias.get(frecAsociada).get(j));
                    frecuenciaFinal2=frecuencias.get(frecAsociada).get(j);
                }
            }
            
            //System.out.println(frecuenciaFinal2);
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

    public void nuevaGeneracion() throws FileNotFoundException {
        
        
        //Buscamos los dos peores individuos de la población de padres
        
        int maximo1=Integer.MIN_VALUE;
        int actual1=0;
        List<Integer> actual1L=new ArrayList<>();
        for(int i=0;i<50;i++){
            if(resultado[i]>maximo1){
            maximo1=resultado[i];
            actual1=i;
            }
        }
        actual1L=padres.get(actual1);
        
        //En actual tengo la posición del elemento con el mayor resultado, es decir, el peor hijo
        
        //Busco el segundo peor, excluyo el valor encontrado antes
                
        int maximo2=Integer.MIN_VALUE;
        int actual2=0;
        List<Integer> actual2L=new ArrayList<>();
        for(int i=0;i<50;i++){
            if(resultado[i]>maximo2 && i!=actual1){
            maximo2=resultado[i];
            actual2=i;
            }
        }
        
        actual2L=padres.get(actual2);
        
        System.out.println("Valores padres:"+maximo1+" y "+maximo2);
        
        resultadoHijos = evaluar(hijos);
        
        System.out.println("Valores hijos:"+resultadoHijos[0]+" y "+resultadoHijos[1]);
        
        numEvaluaciones+=2;
        
        System.out.println("Valores se insertarán en posiciones: "+actual1+" y "+actual2);
        
        int[] ordenar={maximo1,maximo2,resultadoHijos[0],resultadoHijos[1]};
        Arrays.sort(ordenar);
        System.out.println("Se insertarán los hijos con los valores: "+ordenar[0]+" y "+ordenar[1]);
        
        //Actualizo lista resultado
        resultado[actual1]=ordenar[0];
        resultado[actual2]=ordenar[1];
        
        //Actualizo la lista de listas padres con los resultados
        if(ordenar[0]==maximo1){
            padres.set(actual1,actual1L);
        }else if(ordenar[0]==maximo2){
            padres.set(actual1, actual2L);
        }else if(ordenar[0]==resultadoHijos[0]){
            padres.set(actual1,hijos.get(0));
        }else{
            padres.set(actual1,hijos.get(1));
        }
        
        
        
        if(ordenar[1]==maximo1){
            padres.set(actual2,actual1L);
        }else if(ordenar[1]==maximo2){
            padres.set(actual2, actual2L);
        }else if(ordenar[1]==resultadoHijos[0]){
            padres.set(actual2,hijos.get(0));
        }else{
            padres.set(actual2,hijos.get(1));
        }
                

    }

    public int[] evaluar(List<List<Integer>> individuos) throws FileNotFoundException {
        int[] nRes = new int[2];
        int valor1=rDiferencia(individuos.get(0),restricciones);
        nRes[0]=valor1;
        int valor2=rDiferencia(individuos.get(1),restricciones);
        nRes[1]=valor2;


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
