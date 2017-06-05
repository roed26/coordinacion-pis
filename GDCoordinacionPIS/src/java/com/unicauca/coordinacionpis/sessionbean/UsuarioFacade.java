/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.sessionbean;

import com.unicauca.coordinacionpis.entidades.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ROED26
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "GDCoordinacionPISPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario buscarUsuarioPorNombreDeUsuario(String nombreUsuario) {

        Query query = getEntityManager().createNamedQuery("Usuario.findByUsunombreusuario");
        query.setParameter("usunombreusuario", nombreUsuario);
        List<Usuario> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
    public boolean buscarUsuarioPorNombreDeUsuarioBool(String nombreUsuario) {

        Query query = getEntityManager().createNamedQuery("Usuario.findByUsunombreusuario");
        query.setParameter("usunombreusuario", nombreUsuario);
        List<Usuario> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<Usuario> buscarPorIdUsuario(Long usuid) {
        Query query = getEntityManager().createNamedQuery("Usuario.findByUsuid");
        query.setParameter("usuid", usuid);
        List<Usuario> resultList = query.getResultList();
        return resultList;
    }

    public boolean buscarPorEmail(String email) {
        Query query = getEntityManager().createNamedQuery("Usuario.findByUsuemail");
        query.setParameter("usuemail", email);
        List<Usuario> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public List<Usuario> buscarUsuarioEjb(String datoBusqueda) {
        Query query = getEntityManager().createNamedQuery("Usuario.findByBusquedaUsuarios");
        query.setParameter("busqueda", "%" + datoBusqueda + "%");
        List<Usuario> resultList = query.getResultList();
        return resultList;
    }

}
