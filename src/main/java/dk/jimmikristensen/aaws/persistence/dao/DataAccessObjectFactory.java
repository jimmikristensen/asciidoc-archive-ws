package dk.jimmikristensen.aaws.persistence.dao;

import javax.naming.NamingException;

public interface DataAccessObjectFactory {

    public AsciidocDAO getAsciidocDao() throws NamingException;
    
}
