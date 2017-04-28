/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ROED26
 */
@Entity
@Table(name = "materia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Materia.findAll", query = "SELECT m FROM Materia m"),
    @NamedQuery(name = "Materia.findByIdMateria", query = "SELECT m FROM Materia m WHERE m.idMateria = :idMateria"),
    @NamedQuery(name = "Materia.findByNombreMateria", query = "SELECT m FROM Materia m WHERE m.nombreMateria = :nombreMateria"),
    @NamedQuery(name = "Materia.findByNumeroEstudiantes", query = "SELECT m FROM Materia m WHERE m.numeroEstudiantes = :numeroEstudiantes"),
    @NamedQuery(name = "Materia.findByGruposSolicitados", query = "SELECT m FROM Materia m WHERE m.gruposSolicitados = :gruposSolicitados"),
    @NamedQuery(name = "Materia.findByGruposCancelados", query = "SELECT m FROM Materia m WHERE m.gruposCancelados = :gruposCancelados"),
    @NamedQuery(name = "Materia.findByGruposOfertados", query = "SELECT m FROM Materia m WHERE m.gruposOfertados = :gruposOfertados"),
    @NamedQuery(name = "Materia.findByGruposFusionados", query = "SELECT m FROM Materia m WHERE m.gruposFusionados = :gruposFusionados"),
    @NamedQuery(name = "Materia.findByGruposNuevos", query = "SELECT m FROM Materia m WHERE m.gruposNuevos = :gruposNuevos"),
    @NamedQuery(name = "Materia.findByMateria", query = "SELECT m FROM Materia m WHERE LOWER(CONCAT(CONCAT(CONCAT(CONCAT(m.semestre,' '),m.codigoMateria),' '),m.nombreMateria)) LIKE :datoBusqueda")
})

public class Materia implements Serializable {

    @Size(max = 4)
    @Column(name = "semestre")
    private String semestre;
    @Column(name = "creditos")
    private Integer creditos;
    @Column(name = "intensidad_horaria")
    private Integer intensidadHoraria;

    @Size(min = 1, max = 30)
    @Column(name = "codigo_materia")
    private String codigoMateria;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_materia")
    private Integer idMateria;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 70)
    @Column(name = "nombre_materia")
    private String nombreMateria;
    @Column(name = "numero_estudiantes")
    private Integer numeroEstudiantes;
    @Column(name = "grupos_solicitados")
    private Integer gruposSolicitados;
    @Column(name = "grupos_cancelados")
    private Integer gruposCancelados;
    @Column(name = "grupos_ofertados")
    private Integer gruposOfertados;
    @Column(name = "grupos_fusionados")
    private Integer gruposFusionados;
    @Column(name = "grupos_nuevos")
    private Integer gruposNuevos;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id_departamento")
    @ManyToOne(optional = false)
    private Departamento idDepartamento;

    public Materia() {
    }

    public Materia(Integer idMateria) {
        this.idMateria = idMateria;
    }

    public Materia(Integer idMateria, String nombreMateria) {
        this.idMateria = idMateria;
        this.nombreMateria = nombreMateria;
    }

    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public Integer getNumeroEstudiantes() {
        return numeroEstudiantes;
    }

    public void setNumeroEstudiantes(Integer numeroEstudiantes) {
        this.numeroEstudiantes = numeroEstudiantes;
    }

    public Integer getGruposSolicitados() {
        return gruposSolicitados;
    }

    public void setGruposSolicitados(Integer gruposSolicitados) {
        this.gruposSolicitados = gruposSolicitados;
    }

    public Integer getGruposCancelados() {
        return gruposCancelados;
    }

    public void setGruposCancelados(Integer gruposCancelados) {
        this.gruposCancelados = gruposCancelados;
    }

    public Integer getGruposOfertados() {
        return gruposOfertados;
    }

    public void setGruposOfertados(Integer gruposOfertados) {
        this.gruposOfertados = gruposOfertados;
    }

    public Integer getGruposFusionados() {
        return gruposFusionados;
    }

    public void setGruposFusionados(Integer gruposFusionados) {
        this.gruposFusionados = gruposFusionados;
    }

    public Integer getGruposNuevos() {
        return gruposNuevos;
    }

    public void setGruposNuevos(Integer gruposNuevos) {
        this.gruposNuevos = gruposNuevos;
    }

    public Departamento getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Departamento idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMateria != null ? idMateria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Materia)) {
            return false;
        }
        Materia other = (Materia) object;
        if ((this.idMateria == null && other.idMateria != null) || (this.idMateria != null && !this.idMateria.equals(other.idMateria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.unicauca.coodinacionpis.entidades.Materia[ idCurso=" + idMateria + " ]";
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }

    public void setCodigoMateria(String codigoMateria) {
        this.codigoMateria = codigoMateria;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public void setIntensidadHoraria(Integer intensidadHoraria) {
        this.intensidadHoraria = intensidadHoraria;
    }

}
