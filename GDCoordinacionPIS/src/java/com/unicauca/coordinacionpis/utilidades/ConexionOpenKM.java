/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.utilidades;

import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;

/**
 *
 * @author ROED26
 */
public class ConexionOpenKM {

    private String url;
    private String user;
    private String pass;
    OKMWebservices okm ;

    public ConexionOpenKM() {
        this.url = "http://localhost:8083/OpenKM";
        this.user = "okmAdmin";
        this.pass = "admin";
        this.okm = OKMWebservicesFactory.newInstance(url, user, pass);
    }

    public OKMWebservices getOkm() {
        return okm;
    }

    public void setOkm(OKMWebservices okm) {
        this.okm = okm;
    }
    
    
}
