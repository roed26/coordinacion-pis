/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.classMetadatos;

import java.util.Date;

/**
 *
 * @author alexa
 */
public class MetadatosPlanEstudio {

    private int numero;
    private String acuerdo;
    private Date vigencia;

    public MetadatosPlanEstudio() {
        numero = 1;
        acuerdo = "";
        vigencia = null;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getAcuerdo() {
        return acuerdo;
    }

    public void setAcuerdo(String acuerdo) {
        this.acuerdo = acuerdo;
    }

    public Date getVigencia() {
        return vigencia;
    }

    public void setVigencia(Date vigencia) {
        this.vigencia = vigencia;
    }

}
