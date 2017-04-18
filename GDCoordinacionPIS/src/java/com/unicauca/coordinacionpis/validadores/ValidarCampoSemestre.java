package com.unicauca.coordinacionpis.validadores;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


@FacesValidator(value="ValidarCampoSemestre")
public class ValidarCampoSemestre implements Validator
{

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException 
    {
        String texto = String.valueOf(value);
        
        
       if(texto.length()>4){
           FacesMessage msg= new FacesMessage(FacesMessage.SEVERITY_ERROR,"","Máximo cuatro caracteres");
           throw new ValidatorException(msg);
        }
        
        

    }
    
    
    
    
    
}
