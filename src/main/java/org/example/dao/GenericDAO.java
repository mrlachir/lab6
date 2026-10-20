package org.example.dao;
import org.example.util.HibernateUtil;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public class GenericDAO<T,ID extends Serializable>{
    protected final EntityManagerFactory emf;
    private final Class<T> type;
    public GenericDAO(Class<T> type){this(type,HibernateUtil.getSessionFactory());}
    public GenericDAO(Class<T> type,EntityManagerFactory emf){this.type=type;this.emf=Objects.requireNonNull(emf);}
    public <R> R read(Function<EntityManager,R> action){
        EntityManager em=emf.createEntityManager();
        try{return action.apply(em);}finally{em.close();}
    }
    public <R> R write(Function<EntityManager,R> action){
        EntityManager em=emf.createEntityManager();EntityTransaction tx=em.getTransaction();
        try{tx.begin();R result=action.apply(em);tx.commit();return result;}
        catch(RuntimeException ex){
            if(tx.isActive())try{tx.rollback();}catch(RuntimeException rollback){ex.addSuppressed(rollback);}
            throw ex;
        }finally{em.close();}
    }
    public T find(ID id){return read(em->em.find(type,id));}
    public List<T> findAll(){return read(em->em.createQuery("SELECT e FROM "+em.getMetamodel().entity(type).getName()+" e",type).getResultList());}
    public void create(T value){write(em->{em.persist(value);return null;});}
    public T update(T value){return write(em->em.merge(value));}
    public void delete(T value){write(em->{em.remove(em.contains(value)?value:em.merge(value));return null;});}
}
