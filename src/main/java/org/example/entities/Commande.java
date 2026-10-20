package org.example.entities;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
@Entity @Table(name="commandes")
public class Commande {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="date_commande") private LocalDate dateCommande;
    @OneToMany(mappedBy="commande",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<CommandeProduit> lignes=new ArrayList<>();
    public Commande(){}
    public Commande(LocalDate date){dateCommande=date;}
    public Long getId(){return id;}
    public LocalDate getDateCommande(){return dateCommande;}
    public void setDateCommande(LocalDate value){dateCommande=value;}
    public List<CommandeProduit> getLignes(){return Collections.unmodifiableList(lignes);}
    public void addProduit(Produit p,int quantite){
        Objects.requireNonNull(p);
        if(p.getId()==null)throw new IllegalArgumentException("Persist product before ordering");
        if(lignes.stream().anyMatch(l->p.getId().equals(l.getProduit().getId())))
            throw new IllegalArgumentException("Produit deja present");
        lignes.add(new CommandeProduit(this,p,quantite));
    }
    public void removeProduit(Produit p){lignes.removeIf(l->Objects.equals(l.getProduit().getId(),p.getId()));}
}
