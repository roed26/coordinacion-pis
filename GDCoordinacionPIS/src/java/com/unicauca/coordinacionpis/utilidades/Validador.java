/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.utilidades;

import java.text.SimpleDateFormat;

/**
 *
 * @author alexa
 */
public class Validador {

    public static boolean esNumero(String dato) {
        try {
            Integer.parseInt(dato);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean esFecha(String dato) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            formatoFecha.parse(dato);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
