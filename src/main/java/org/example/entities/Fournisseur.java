package org.example.entities;
import javax.persistence.*;
@Entity @Table(name="fournisseurs")
public class Fournisseur extends User{
    @Column(name="adresse") private String adresse;
    public Fournisseur(){}
    public Fournisseur(String nom,String value){super(nom);this.adresse=value;}
    public String getAdresse(){return adresse;}
    public void setAdresse(String value){adresse=value;}
}
