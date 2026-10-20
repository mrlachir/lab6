package org.example;
import org.example.entities.*;
import org.example.dao.GenericDAO;
import org.example.service.*;
import org.example.util.HibernateUtil;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.*;

public class App{
    public static void main(String[] args){
        // Local demo defaults to H2; supply -Dlab.db=mysql to use MySQL instead.
        if(System.getProperty("lab.db")==null)System.setProperty("lab.db","h2");
        try{
            EntityManagerFactory emf=HibernateUtil.getSessionFactory();
            CrudService<Categorie,Long> cats=new CrudService<>(Categorie.class);
            ProduitService produits=new ProduitService();
            Categorie informatique=new Categorie("Informatique"), papeterie=new Categorie("Papeterie");
            cats.create(informatique);cats.create(papeterie);
            Produit clavier=new Produit("Clavier",new BigDecimal("49.99"),informatique);
            Produit souris=new Produit("Souris",new BigDecimal("20.00"),informatique);
            Produit stylo=new Produit("Stylo",new BigDecimal("1.20"),papeterie);
            produits.create(clavier);produits.create(souris);produits.create(stylo);
            System.out.println("=== NAMED QUERIES ===");
            System.out.println("Tous : "+produits.getAll());
            System.out.println("Categorie : "+produits.findByCategorie(informatique.getId()));
            System.out.println("Libelle CLA : "+produits.searchByLibelle("CLA"));
            System.out.println("=== CRITERIA : prix 10..50 ===");
            System.out.println(produits.search(null,new BigDecimal("10"),new BigDecimal("50")));
            System.out.println("=== PAGINATION ===");
            System.out.println("Page 1 : "+produits.findPage(1,2));
            System.out.println("Page 2 : "+produits.findPage(2,2));
            CrudService<User,Long> users=new CrudService<>(User.class);
            users.create(new Client("Alice",LocalDate.now()));
            users.create(new Fournisseur("Bob","Casablanca"));
            System.out.println("=== JOINED ===");
            for(User u:users.getAll())System.out.println(u.getClass().getSimpleName()+": "+u.getNom());
            Commande cmd=new Commande(YearMonth.now().minusMonths(1).atDay(15));
            cmd.addProduit(clavier,3);cmd.addProduit(souris,5);
            new CommandeService().create(cmd);
            System.out.println("=== COMMANDE : id="+cmd.getId()+", lignes=2 ===");
            System.out.println("=== RAPPORT MOIS PRECEDENT ===");
            new RapportService().moisPrecedent().forEach(System.out::println);
            GenericDAO<Categorie,Long> dao=new GenericDAO<>(Categorie.class,emf);
            try{dao.write(em->{em.persist(new Categorie("Rollback"));throw new IllegalArgumentException("Erreur simulee");});}
            catch(IllegalArgumentException expected){System.out.println("ROLLBACK OK : "+expected.getMessage());}
            stylo.setPrix(new BigDecimal("1.50"));produits.update(stylo);
            System.out.println("CRUD UPDATE Stylo = "+produits.getById(stylo.getId()).getPrix());
            produits.delete(stylo);
            System.out.println("CRUD DELETE Stylo = "+(produits.getById(stylo.getId())==null));
            System.out.println("DEMO TERMINEE");
        }finally{HibernateUtil.shutdown();}
    }
}
