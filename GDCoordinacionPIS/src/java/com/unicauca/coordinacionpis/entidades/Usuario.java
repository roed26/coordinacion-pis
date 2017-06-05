/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ROED26
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByUsuid", query = "SELECT u FROM Usuario u WHERE u.usuid = :usuid"),
    @NamedQuery(name = "Usuario.findByUsufechanacimiento", query = "SELECT u FROM Usuario u WHERE u.usufechanacimiento = :usufechanacimiento"),
    @NamedQuery(name = "Usuario.findByUsunombres", query = "SELECT u FROM Usuario u WHERE u.usunombres = :usunombres"),
    @NamedQuery(name = "Usuario.findByUsuapellidos", query = "SELECT u FROM Usuario u WHERE u.usuapellidos = :usuapellidos"),
    @NamedQuery(name = "Usuario.findByUsugenero", query = "SELECT u FROM Usuario u WHERE u.usugenero = :usugenero"),
    @NamedQuery(name = "Usuario.findByUsunombreusuario", query = "SELECT u FROM Usuario u WHERE u.usunombreusuario = :usunombreusuario"),
    @NamedQuery(name = "Usuario.findByUsucontrasena", query = "SELECT u FROM Usuario u WHERE u.usucontrasena = :usucontrasena"),
    @NamedQuery(name = "Usuario.findByUsuemail", query = "SELECT u FROM Usuario u WHERE u.usuemail = :usuemail"),
    @NamedQuery(name = "Usuario.findByBusquedaUsuarios", query = "SELECT u FROM Usuario u WHERE LOWER(CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(u.usunombres,' '), u.usuapellidos),' ') ,u.usuemail), ' '),u.usunombreusuario)) LIKE :busqueda")
})
public class Usuario implements Serializable {

    @Lob
    @Column(name = "USUFOTO")
    private byte[] usufoto;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "USUID")
    private Long usuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "USUFECHANACIMIENTO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date usufechanacimiento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 75)
    @Column(name = "USUNOMBRES")
    private String usunombres;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 75)
    @Column(name = "USUAPELLIDOS")
    private String usuapellidos;
    @Basic(optional = false)
    @NotNull
    @Column(name = "USUGENERO")
    private Character usugenero;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 75)
    @Column(name = "USUNOMBREUSUARIO")
    private String usunombreusuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "USUCONTRASENA")
    private String usucontrasena;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "USUEMAIL")
    private String usuemail;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private List<Usuariogrupo> usuariogrupoList;
    @JoinColumn(name = "CARID", referencedColumnName = "CARID")
    @ManyToOne
    private Cargo carid;

    public Usuario() {
    }

    public Usuario(Long usuid) {
        this.usuid = usuid;
    }

    public Usuario(Long usuid, Date usufechanacimiento, String usunombres, String usuapellidos, Character usugenero, String usunombreusuario, String usucontrasena, String usuemail) {
        this.usuid = usuid;
        this.usufechanacimiento = usufechanacimiento;
        this.usunombres = usunombres;
        this.usuapellidos = usuapellidos;
        this.usugenero = usugenero;
        this.usunombreusuario = usunombreusuario;
        this.usucontrasena = usucontrasena;
        this.usuemail = usuemail;
    }

    public Long getUsuid() {
        return usuid;
    }

    public void setUsuid(Long usuid) {
        this.usuid = usuid;
    }

    public Date getUsufechanacimiento() {
        return usufechanacimiento;
    }

    public void setUsufechanacimiento(Date usufechanacimiento) {
        this.usufechanacimiento = usufechanacimiento;
    }

    public String getUsunombres() {
        return usunombres;
    }

    public void setUsunombres(String usunombres) {
        this.usunombres = usunombres;
    }

    public String getUsuapellidos() {
        return usuapellidos;
    }

    public void setUsuapellidos(String usuapellidos) {
        this.usuapellidos = usuapellidos;
    }

    public Character getUsugenero() {
        return usugenero;
    }

    public void setUsugenero(Character usugenero) {
        this.usugenero = usugenero;
    }

    public String getUsunombreusuario() {
        return usunombreusuario;
    }

    public void setUsunombreusuario(String usunombreusuario) {
        this.usunombreusuario = usunombreusuario;
    }

    public String getUsucontrasena() {
        return usucontrasena;
    }

    public void setUsucontrasena(String usucontrasena) {
        this.usucontrasena = usucontrasena;
    }

    public String getUsuemail() {
        return usuemail;
    }

    public void setUsuemail(String usuemail) {
        this.usuemail = usuemail;
    }

    @XmlTransient
    public List<Usuariogrupo> getUsuariogrupoList() {
        return usuariogrupoList;
    }

    public void setUsuariogrupoList(List<Usuariogrupo> usuariogrupoList) {
        this.usuariogrupoList = usuariogrupoList;
    }

    public Cargo getCarid() {
        return carid;
    }

    public void setCarid(Cargo carid) {
        this.carid = carid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuid != null ? usuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.usuid == null && other.usuid != null) || (this.usuid != null && !this.usuid.equals(other.usuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.unicauca.coordinacionpis.entidades.Usuario[ usuid=" + usuid + " ]";
    }

    public byte[] getUsufoto() {
        return usufoto;
    }

    public void setUsufoto(byte[] usufoto) {
        this.usufoto = usufoto;
    }

}
