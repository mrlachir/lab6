package org.example.service;
import org.example.entities.*;
import org.example.dao.GenericDAO;
import org.example.util.HibernateUtil;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class RapportService{
    private final GenericDAO<Categorie,Long> dao;
    public RapportService(){this(HibernateUtil.getSessionFactory());}
    public RapportService(EntityManagerFactory emf){dao=new GenericDAO<>(Categorie.class,emf);}
    public List<VenteMensuelle> moisPrecedent(){return rapport(YearMonth.now().minusMonths(1));}
    public List<VenteMensuelle> rapport(YearMonth mois){
        Objects.requireNonNull(mois);
        return dao.read(em->{
            CriteriaBuilder cb=em.getCriteriaBuilder();CriteriaQuery<Tuple> q=cb.createTupleQuery();
            Root<CommandeProduit> line=q.from(CommandeProduit.class);
            Join<CommandeProduit,Produit> produit=line.join("produit");
            Join<Produit,Categorie> categorie=produit.join("categorie");
            Expression<BigDecimal> revenue=cb.prod(produit.<BigDecimal>get("prix"),cb.toBigDecimal(line.<Integer>get("quantite")));
            q.multiselect(categorie.get("id"),categorie.get("nom"),cb.sum(revenue));
            Path<LocalDate> date=line.get("commande").get("dateCommande");
            q.where(cb.greaterThanOrEqualTo(date,mois.atDay(1)),cb.lessThan(date,mois.plusMonths(1).atDay(1)));
            q.groupBy(categorie.get("id"),categorie.get("nom"));
            Map<Long,BigDecimal> totals=new HashMap<>();
            for(Tuple row:em.createQuery(q).getResultList())totals.put(row.get(0,Long.class),row.get(2,BigDecimal.class));
            // Include categories without sales with zero revenue.
            return em.createQuery("SELECT c FROM Categorie c ORDER BY c.nom",Categorie.class).getResultList().stream()
                .map(c->new VenteMensuelle(c.getId(),c.getNom(),totals.getOrDefault(c.getId(),BigDecimal.ZERO)))
                .collect(Collectors.toList());
        });
    }
}
