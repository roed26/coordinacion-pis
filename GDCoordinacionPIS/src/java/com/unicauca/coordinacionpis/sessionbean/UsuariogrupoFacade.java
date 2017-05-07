/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.sessionbean;

import com.unicauca.coordinacionpis.entidades.Usuariogrupo;
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
public class UsuariogrupoFacade extends AbstractFacade<Usuariogrupo> {

    @PersistenceContext(unitName = "GDCoordinacionPISPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuariogrupoFacade() {
        super(Usuariogrupo.class);
    }

    public List<Usuariogrupo> buscarPorNombreUsuario(String nombreDeUsuario) {
        Query query = getEntityManager().createNamedQuery("Usuariogrupo.findByUsunombreusuario");
        query.setParameter("usunombreusuario", nombreDeUsuario);
        List<Usuariogrupo> resultList = query.getResultList();
        return resultList;
    }

    public Usuariogrupo buscarPorNombreUsuarioObj(String nombreDeUsuario) {
        Query query = getEntityManager().createNamedQuery("Usuariogrupo.findByUsunombreusuario");
        query.setParameter("usunombreusuario", nombreDeUsuario);
        List<Usuariogrupo> resultList = query.getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        } else {
            return null;
        }

    }

}
