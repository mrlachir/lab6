package org.example.entities;
import javax.persistence.*;
@Entity @Table(name="commande_produit")
public class CommandeProduit {
    @EmbeddedId private CommandeProduitPK id=new CommandeProduitPK();
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @MapsId("commandeId")
    @JoinColumn(name="commande_id") private Commande commande;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @MapsId("produitId")
    @JoinColumn(name="produit_id") private Produit produit;
    @Column(nullable=false) private int quantite;
    public CommandeProduit(){}
    public CommandeProduit(Commande c,Produit p,int q){
        commande=c;produit=p;setQuantite(q);
        id=new CommandeProduitPK(c.getId(),p.getId());
    }
    public CommandeProduitPK getId(){return id;}
    public Commande getCommande(){return commande;}
    public Produit getProduit(){return produit;}
    public void setProduit(Produit value){produit=java.util.Objects.requireNonNull(value);id.setProduitId(value.getId());}
    public int getQuantite(){return quantite;}
    public void setQuantite(int value){if(value<1)throw new IllegalArgumentException("Quantite positive requise");quantite=value;}
    @PrePersist @PreUpdate private void validate(){if(quantite<1)throw new IllegalArgumentException("Quantite positive requise");}
}
