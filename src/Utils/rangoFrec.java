/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import main.main;

/**
 *
 * @author alumno
 */
public class rangoFrec {

    public List<List<Integer>> rangoFrecuencias = new ArrayList<>();

    public rangoFrec () throws FileNotFoundException {
        int contX = 0;
        String datos = "dom.txt";
        if ( main.DIRECTORIO.matches("scen.*") ) {
            datos = datos.toUpperCase();
        }
        File file = new File(main.TRABAJO + "/conjuntos/" + main.DIRECTORIO + "/" + datos);

        Scanner archivo = new Scanner(file);
        while( archivo.hasNextLine() ) {
            rangoFrecuencias.add(new ArrayList<Integer>());
            String line = archivo.nextLine();
            Scanner lineScanner = new Scanner(line);
            lineScanner.nextInt();
            lineScanner.nextInt(); // Salta indice y numero de frecuencias
            while( lineScanner.hasNext() ) {
                int token = lineScanner.nextInt();
                rangoFrecuencias.get(contX).add(token);
            }
            lineScanner.close();
            contX ++;
        }
        archivo.close();
    }

}
