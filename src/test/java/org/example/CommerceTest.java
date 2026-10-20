package org.example;
import org.example.entities.*;
import org.example.dao.GenericDAO;
import org.example.service.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.Assert.*;

public class CommerceTest{
    private SessionFactory emf;
    private CrudService<Categorie,Long> cats;
    private ProduitService produits;
    private Categorie cat;
    private Produit p;
    @Before public void setup(){
        Configuration cfg=new Configuration().configure();
        cfg.setProperty("hibernate.connection.driver_class","org.h2.Driver");
        cfg.setProperty("hibernate.connection.url","jdbc:h2:mem:test_"+UUID.randomUUID()+";DB_CLOSE_DELAY=-1");
        cfg.setProperty("hibernate.connection.username","sa");cfg.setProperty("hibernate.connection.password","");
        cfg.setProperty("hibernate.dialect","org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto","create-drop");cfg.setProperty("hibernate.show_sql","false");
        emf=cfg.buildSessionFactory();
        cats=new CrudService<>(Categorie.class,emf);produits=new ProduitService(emf);
        cat=new Categorie("Informatique");cats.create(cat);
        p=new Produit("Clavier",new BigDecimal("49.99"),cat);produits.create(p);
    }
    @After public void close(){if(emf!=null)emf.close();}
    @Test public void createAndReadDetachedProductWithCategory(){
        Produit loaded=produits.getById(p.getId());
        assertEquals("Informatique",loaded.getCategorie().getNom());
        assertEquals("Clavier",loaded.getLibelle());
        assertNotNull(loaded.getDateCreation());
    }
    @Test public void crudMergeDelete(){
        p.setLibelle("Clavier USB");produits.update(p);
        assertEquals("Clavier USB",produits.getById(p.getId()).getLibelle());
        produits.delete(p);assertNull(produits.getById(p.getId()));
        assertNotNull(cats.getById(cat.getId()));
    }
    @Test public void namedAndCriteriaQueries(){
        produits.create(new Produit("Souris",new BigDecimal("20"),cat));
        assertEquals(2,produits.getAll().size());
        assertEquals(2,produits.findByCategorie(cat.getId()).size());
        assertEquals(1,produits.searchByLibelle("CLA").size());
        assertEquals(1,produits.search("cla",new BigDecimal("40"),new BigDecimal("50")).size());
        assertEquals(0,produits.search(null,new BigDecimal("60"),null).size());
        assertThrows(IllegalArgumentException.class,()->produits.search(null,BigDecimal.TEN,BigDecimal.ONE));
    }
    @Test public void paginationAndValidation(){
        produits.create(new Produit("Souris",BigDecimal.ONE,cat));
        assertEquals(1,produits.findPage(1,1).size());
        assertNotEquals(produits.findPage(1,1).get(0).getId(),produits.findPage(2,1).get(0).getId());
        assertTrue(produits.findPage(3,1).isEmpty());
        assertThrows(IllegalArgumentException.class,()->produits.findPage(0,1));
        assertThrows(IllegalArgumentException.class,()->cats.create(null));
    }
    @Test public void orphanRemoval(){
        GenericDAO<Categorie,Long> dao=new GenericDAO<>(Categorie.class,emf);
        dao.write(em->{Categorie c=em.find(Categorie.class,cat.getId());c.removeProduit(c.getProduits().get(0));return null;});
        assertNull(produits.getById(p.getId()));
    }
    @Test public void joinedInheritance(){
        CrudService<User,Long> users=new CrudService<>(User.class,emf);
        Client c=new Client("Alice",LocalDate.now());Fournisseur f=new Fournisseur("Bob","Rabat");
        users.create(c);users.create(f);
        assertEquals(2,users.getAll().size());
        assertTrue(users.getById(c.getId()) instanceof Client);
        assertTrue(users.getById(f.getId()) instanceof Fournisseur);
    }
    @Test public void compositeKeyAndOrderCascade(){
        Commande c=new Commande(LocalDate.now());c.addProduit(p,3);
        CommandeService service=new CommandeService(emf);service.create(c);
        GenericDAO<CommandeProduit,CommandeProduitPK> lines=new GenericDAO<>(CommandeProduit.class,emf);
        CommandeProduit line=lines.find(new CommandeProduitPK(c.getId(),p.getId()));
        assertEquals(3,line.getQuantite());
        assertEquals(new CommandeProduitPK(c.getId(),p.getId()),line.getId());
        assertEquals(new CommandeProduitPK(1L,2L).hashCode(),new CommandeProduitPK(1L,2L).hashCode());
        assertThrows(IllegalArgumentException.class,()->c.addProduit(p,2));
        service.delete(c);assertTrue(lines.findAll().isEmpty());
        assertNotNull(produits.getById(p.getId()));
    }
    @Test public void rollbackAndNegativePrice(){
        GenericDAO<Categorie,Long> dao=new GenericDAO<>(Categorie.class,emf);
        assertThrows(IllegalStateException.class,()->dao.write(em->{
            em.persist(new Categorie("Annulee"));throw new IllegalStateException("test");
        }));
        assertEquals(1,cats.getAll().size());
        assertThrows(RuntimeException.class,()->produits.create(new Produit("Negatif",new BigDecimal("-1"),cat)));
        assertEquals(1,produits.getAll().size());
    }
    @Test public void monthlyReportAggregatesAndIncludesZeroSales(){
        Categorie empty=new Categorie("Sans ventes");cats.create(empty);
        YearMonth month=YearMonth.of(2024,6);
        CommandeService orders=new CommandeService(emf);
        for(LocalDate date:Arrays.asList(month.atDay(1),month.atEndOfMonth(),month.plusMonths(1).atDay(1),month.atDay(1).minusDays(1))){
            Commande c=new Commande(date);c.addProduit(p,2);orders.create(c);
        }
        List<VenteMensuelle> report=new RapportService(emf).rapport(month);
        assertEquals(2,report.size());
        assertEquals(0,new BigDecimal("199.96").compareTo(report.get(0).getChiffreAffaires()));
        assertEquals(0,BigDecimal.ZERO.compareTo(report.get(1).getChiffreAffaires()));
    }
}
