/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.validadores;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author alexa
 */
@FacesValidator(value = "validarCampoNumeroPlanEstudio")
public class ValidarCampoNumeroPlanEstudio implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int numero = Integer.parseInt(String.valueOf(value));
        if (numero <= 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Número mayor que cero", "Número mayor que cero");
            throw new ValidatorException(msg);
        }
    }

}
