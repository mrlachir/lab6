package org.example.service;
import org.example.entities.*;
import javax.persistence.EntityManagerFactory;

public class CommandeService extends CrudService<Commande,Long>{
    public CommandeService(){super(Commande.class);}
    public CommandeService(EntityManagerFactory emf){super(Commande.class,emf);}
    @Override public void create(Commande commande){
        if(commande==null)throw new IllegalArgumentException("Commande nulle");
        dao.write(em->{
            for(CommandeProduit ligne:commande.getLignes())
                ligne.setProduit(em.getReference(Produit.class,ligne.getProduit().getId()));
            em.persist(commande);
            return null;
        });
    }
}
