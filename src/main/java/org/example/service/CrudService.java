package org.example.service;
import org.example.dao.GenericDAO;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.*;

public class CrudService<T,ID extends Serializable>{
    protected final GenericDAO<T,ID> dao;
    public CrudService(Class<T> type){dao=new GenericDAO<>(type);}
    public CrudService(Class<T> type,EntityManagerFactory emf){dao=new GenericDAO<>(type,emf);}
    public T getById(ID id){return dao.find(id);}
    public List<T> getAll(){return dao.findAll();}
    private void validate(T value){if(value==null)throw new IllegalArgumentException("L'entite ne doit pas etre nulle");}
    public void create(T value){validate(value);dao.create(value);}
    public T update(T value){validate(value);return dao.update(value);}
    public void delete(T value){validate(value);dao.delete(value);}
}
