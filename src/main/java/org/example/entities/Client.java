package org.example.entities;
import javax.persistence.*;
@Entity @Table(name="clients")
public class Client extends User{
    @Column(name="date_inscription") private java.time.LocalDate dateInscription;
    public Client(){}
    public Client(String nom,java.time.LocalDate value){super(nom);this.dateInscription=value;}
    public java.time.LocalDate getDateInscription(){return dateInscription;}
    public void setDateInscription(java.time.LocalDate value){dateInscription=value;}
}
