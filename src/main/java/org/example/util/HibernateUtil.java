package org.example.util;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static SessionFactory factory;
    private HibernateUtil() {}
    public static synchronized SessionFactory getSessionFactory() {
        if (factory == null || factory.isClosed()) {
            Configuration cfg = new Configuration().configure();
            if ("h2".equals(System.getProperty("lab.db"))) {
                cfg.setProperty("hibernate.connection.driver_class","org.h2.Driver");
                cfg.setProperty("hibernate.connection.url","jdbc:h2:mem:commerce;DB_CLOSE_DELAY=-1");
                cfg.setProperty("hibernate.dialect","org.hibernate.dialect.H2Dialect");
                cfg.setProperty("hibernate.connection.username","sa");
                cfg.setProperty("hibernate.connection.password","");
            } else {
                override(cfg,"hibernate.connection.url","DB_URL");
                override(cfg,"hibernate.connection.username","DB_USER");
                override(cfg,"hibernate.connection.password","DB_PASSWORD");
            }
            factory = cfg.buildSessionFactory();
        }
        return factory;
    }
    private static void override(Configuration cfg,String key,String env) {
        String value = System.getenv(env);
        if(value != null) cfg.setProperty(key,value);
    }
    public static synchronized void shutdown() {
        if(factory != null && !factory.isClosed()) factory.close();
        factory = null;
    }
}
