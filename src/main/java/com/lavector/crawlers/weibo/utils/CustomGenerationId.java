package com.lavector.crawlers.weibo.utils;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

@Component
public class CustomGenerationId implements Configurable, IdentifierGenerator {

    private String idPrefix;

    public CustomGenerationId() {
    }

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {

        this.idPrefix = properties.getProperty("idPrefix");

    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return getId();
    }

    public String getId() {
        synchronized (CustomGenerationId.class) {

//            IdentifierGeneratorFactory identifierGeneratorFactory = IdentifierGeneratorFactory.newInstance();
//
//            LongIdentifierGenerator longIdentifierGenerator = identifierGeneratorFactory.longGenerator();
//
//            Long aLong = longIdentifierGenerator.nextLongIdentifier();

            String aLong = UUID.randomUUID().toString().replace("-", "");

            return aLong;
        }
    }

    public static void main(String[] args) {

//        IdentifierGeneratorFactory identifierGeneratorFactory = IdentifierGeneratorFactory.newInstance();
//
//        LongIdentifierGenerator longIdentifierGenerator = identifierGeneratorFactory.longGenerator();


        for (int i=0; i<10 ; i++){
            String id = new CustomGenerationId().getId();
            System.out.println(id);
        }


    }
}
