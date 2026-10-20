package org.example.service;
import org.example.entities.*;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

public class ProduitService extends CrudService<Produit,Long>{
    public ProduitService(){super(Produit.class);}
    public ProduitService(EntityManagerFactory emf){super(Produit.class,emf);}
    // Fetch category explicitly before closing the persistence context.
    @Override public Produit getById(Long id){
        return dao.read(em->em.createQuery("SELECT p FROM Produit p JOIN FETCH p.categorie WHERE p.id=:id",Produit.class)
            .setParameter("id",id).getResultStream().findFirst().orElse(null));
    }
    @Override public List<Produit> getAll(){return dao.read(em->em.createNamedQuery("Produit.findAll",Produit.class).getResultList());}
    public List<Produit> findByCategorie(Long id){return dao.read(em->em.createNamedQuery("Produit.findByCategorie",Produit.class).setParameter("catId",id).getResultList());}
    public List<Produit> searchByLibelle(String term){return dao.read(em->em.createNamedQuery("Produit.searchByLibelle",Produit.class).setParameter("term",Objects.requireNonNull(term)).getResultList());}
    public List<Produit> search(String term,BigDecimal min,BigDecimal max){
        if((min!=null&&min.signum()<0)||(max!=null&&max.signum()<0)||(min!=null&&max!=null&&min.compareTo(max)>0))
            throw new IllegalArgumentException("Invalid price range");
        return dao.read(em->{
            CriteriaBuilder cb=em.getCriteriaBuilder();CriteriaQuery<Produit> q=cb.createQuery(Produit.class);
            Root<Produit> r=q.from(Produit.class);r.fetch("categorie");
            List<Predicate> filters=new ArrayList<>();
            if(term!=null&&!term.trim().isEmpty())filters.add(cb.like(cb.lower(r.get("libelle")),"%"+term.toLowerCase(Locale.ROOT)+"%"));
            if(min!=null)filters.add(cb.ge(r.get("prix"),min));
            if(max!=null)filters.add(cb.le(r.get("prix"),max));
            q.select(r).where(filters.toArray(new Predicate[0])).orderBy(cb.asc(r.get("id")));
            return em.createQuery(q).getResultList();
        });
    }
    public List<Produit> findPage(int page,int size){
        long offset=(long)(page-1)*size;
        if(page<1||size<1||offset>Integer.MAX_VALUE)throw new IllegalArgumentException("Invalid pagination");
        return dao.read(em->em.createQuery("SELECT p FROM Produit p JOIN FETCH p.categorie ORDER BY p.id",Produit.class)
            .setFirstResult((int)offset).setMaxResults(size).getResultList());
    }
}
