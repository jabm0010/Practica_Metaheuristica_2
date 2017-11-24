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
import static main.main.LINEAS;

/**
 *
 * @author pierre
 */
public class listaTransmisores {

    public List<Integer> transmisores = new ArrayList<>();

    public listaTransmisores () throws FileNotFoundException {
        String datos = "var.txt";
        if ( main.DIRECTORIO.matches("scen.*") ) {
            datos = datos.toUpperCase();
        }
        File file = new File(main.TRABAJO + "/conjuntos/" + main.DIRECTORIO + "/" + datos);

        for ( int i = 0; i < LINEAS; i ++ ) {
            transmisores.add(0);
        }

        Scanner archivo = new Scanner(file);
        while( archivo.hasNextLine() ) {
            int cont = 0;
            String line = archivo.nextLine();
            Scanner linea = new Scanner(line);
            int transmisor = linea.nextInt();
            while( linea.hasNext() && cont < 1 ) {
                int token = linea.nextInt();
                transmisores.set(transmisor-1, token);
                cont ++;
            }
            linea.close();
        }
        archivo.close();
    }
}
