package org.example.entities;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class CommandeProduitPK implements Serializable {
    private static final long serialVersionUID=1L;
    @Column(name="commande_id") private Long commandeId;
    @Column(name="produit_id") private Long produitId;
    public CommandeProduitPK(){}
    public CommandeProduitPK(Long c,Long p){commandeId=c;produitId=p;}
    public Long getCommandeId(){return commandeId;}
    public void setCommandeId(Long value){commandeId=value;}
    public Long getProduitId(){return produitId;}
    public void setProduitId(Long value){produitId=value;}
    @Override public boolean equals(Object o){
        if(this==o)return true;if(!(o instanceof CommandeProduitPK))return false;
        CommandeProduitPK k=(CommandeProduitPK)o;
        return Objects.equals(commandeId,k.commandeId)&&Objects.equals(produitId,k.produitId);
    }
    @Override public int hashCode(){return Objects.hash(commandeId,produitId);}
}
