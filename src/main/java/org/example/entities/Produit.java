package org.example.entities;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="produits")
@NamedQueries({
@NamedQuery(name="Produit.findAll",query="SELECT p FROM Produit p JOIN FETCH p.categorie ORDER BY p.id"),
@NamedQuery(name="Produit.findByCategorie",query="SELECT p FROM Produit p JOIN FETCH p.categorie WHERE p.categorie.id=:catId ORDER BY p.id"),
@NamedQuery(name="Produit.searchByLibelle",query="SELECT p FROM Produit p JOIN FETCH p.categorie WHERE LOWER(p.libelle) LIKE LOWER(CONCAT('%',:term,'%')) ORDER BY p.id")
})
public class Produit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=150) private String libelle;
    @Column(precision=10,scale=2) private BigDecimal prix;
    @Column(name="date_creation") private LocalDate dateCreation;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="categorie_id",nullable=false)
    private Categorie categorie;
    public Produit(){}
    public Produit(String libelle,BigDecimal prix,Categorie categorie){
        this.libelle=libelle;this.prix=prix;this.dateCreation=LocalDate.now();setCategorie(categorie);
    }
    public Long getId(){return id;}
    public String getLibelle(){return libelle;}
    public void setLibelle(String value){libelle=value;}
    public BigDecimal getPrix(){return prix;}
    public void setPrix(BigDecimal value){prix=value;}
    public LocalDate getDateCreation(){return dateCreation;}
    public void setDateCreation(LocalDate value){dateCreation=value;}
    public Categorie getCategorie(){return categorie;}
    public void setCategorie(Categorie value){
        if(categorie==value)return;
        Categorie old=categorie;categorie=value;
        if(old!=null)old.detach(this);if(value!=null)value.attach(this);
    }
    @PrePersist @PreUpdate private void validate(){
        if(prix!=null && prix.signum()<0)throw new IllegalArgumentException("Prix negatif");
    }
    @Override public String toString(){return libelle+" ("+prix+")";}
}
