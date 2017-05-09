package com.unicauca.coordinacionpis.validadores;


import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class ValidarEdicionUsuarios implements Serializable {

    public ValidarEdicionUsuarios() {

    }


    public boolean validarNombres(String nombre) {
        if (nombre.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo Nombre Obligatorio.", "Campo Nombre Obligatorio."));
            return false;
        } else {
            Pattern patron = Pattern.compile("[^A-Za-z ñÑáéíóú]");
            Matcher encaja = patron.matcher(nombre);
            if (encaja.find()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo nombre solo letras y espacios.", "Campo nombre solo letras y espacios"));
                return false;
            }
            return true;
        }

    }

    public boolean validarApellidos(String apellido) {
        if (apellido.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo apellidos obligatorio.", "Campo apellidos obligatorio."));
            return false;
        } else {
            Pattern patron = Pattern.compile("[^A-Za-z ñÑáéíóú]");
            Matcher encaja = patron.matcher(apellido);
            if (encaja.find()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo apellidos solo letras y espacios.", "Campo apellido solo letras y espacios."));
                return false;
            }
            return true;
        }

    }

    public boolean validarFechaNacimiento(Date fecha) {
        if (fecha == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo fecha de nacimiento es obligatorio.", "Campo fecha de nacimiento es obligatorio."));
            return false;
        } else {
            Date fechaActual = new Date();
            if (fecha.compareTo(fechaActual) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo fecha de nacimiento no puede ser mayor a la fecha actual.", "Campo fecha de nacimiento no puede ser mayor a la fecha actual."));
                return false;
            } else {
                return true;
            }
        }

    }

    
    public boolean validarTelefono(String telefono) {
        if (!telefono.isEmpty()) {

            try {
                long campo = Long.parseLong(telefono);

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo teléfono solo puede contener números.", "Campo teléfono solo puede contener números."));
                return false;
            }

        }
        return true;
    }


    public boolean validarContrasena(String contrasena) {
        if (contrasena.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña obligatorio.", "Campo contraseña obligatorio."));
            return false;
        } else {
            if (contrasena.length() < 6) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña minimo 6 caracteres.", "Campo contraseña minimo 6 caracteres."));
                return false;
            } else {
                if (contrasena.length() > 20) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña maximo 20 caracteres.", "Campo contraseña maximo 20 caracteres."));
                    return false;
                }
                return true;
            }
        }
    }

    public boolean validarContrasenaConConfirmacion(String contrasena, String confirmarContrasena) {
        if (confirmarContrasena.isEmpty() && contrasena.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña obligatorio.", "Campo contraseña obligatorio."));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo confirmar contraseña obligatorio.", "Campo confirmar contraseña obligatorio."));
            return false;
        }
        if (contrasena.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña obligatorio.", "Campo contraseña obligatorio."));
            return false;
        } else {
            if (confirmarContrasena.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo confirmar contraseña obligatorio.", "Campo confirmar contraseña obligatorio."));
                return false;
            } else {
                if (contrasena.length() < 6) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña minimo 6 caracteres.", "Campo contraseña minimo 6 caracteres."));
                    return false;
                } else {
                    if (contrasena.length() > 20) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo contraseña maximo 20 caracteres.", "Campo contraseña maximo 20 caracteres."));
                        return false;
                    } else {
                        if (!contrasena.equals(confirmarContrasena)) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Las contraseñas no coinciden.", "Las contraseñas no coinciden."));
                            return false;
                        }
                        return true;
                    }

                }
            }

        }
    }

}
