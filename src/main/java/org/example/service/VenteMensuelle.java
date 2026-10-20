package org.example.service;
import java.math.BigDecimal;
public class VenteMensuelle{
    private final Long categorieId;
    private final String categorie;
    private final BigDecimal chiffreAffaires;
    public VenteMensuelle(Long id,String categorie,BigDecimal total){categorieId=id;this.categorie=categorie;chiffreAffaires=total;}
    public Long getCategorieId(){return categorieId;}
    public String getCategorie(){return categorie;}
    public BigDecimal getChiffreAffaires(){return chiffreAffaires;}
    @Override public String toString(){return categorie+" = "+chiffreAffaires;}
}
