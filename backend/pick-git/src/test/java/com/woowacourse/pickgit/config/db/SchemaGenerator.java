package com.woowacourse.pickgit.config.db;

import static java.util.stream.Collectors.toList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToWriter;
import org.springframework.stereotype.Component;

@Component
public class SchemaGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    public String create() {
        return generate(Action.CREATE);
    }

    public String drop() {
        return generate(Action.DROP);
    }

    private String generate(Action action) {
        StringWriter output = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(output);

        try (output; bufferedWriter) {
            StandardServiceRegistry serviceRegistry = getServiceRegistry();
            MetadataSources metadataSources = getMetadataSources();

            for (Class<?> entity : getEntities()) {
                metadataSources.addAnnotatedClass(entity);
            }

            Metadata metadata = metadataSources.buildMetadata(serviceRegistry);

            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setFormat(true);
            schemaExport.setDelimiter(";");
            schemaExport
                .perform(action, metadata, new ScriptTargetOutputToWriter(bufferedWriter));

            return output.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<? extends Class<?>> getEntities() {
        Set<EntityType<?>> entities = entityManager
            .getEntityManagerFactory()
            .getMetamodel()
            .getEntities();

        return entities.stream()
            .map(Type::getJavaType)
            .collect(toList());
    }


    private MetadataSources getMetadataSources() {
        return new MetadataSources(
            new StandardServiceRegistryBuilder().build()
        );
    }

    private StandardServiceRegistry getServiceRegistry() {
        SessionFactory sessionFactory = entityManager
            .getEntityManagerFactory()
            .unwrap(SessionFactory.class);

        return sessionFactory
            .getSessionFactoryOptions()
            .getServiceRegistry();
    }
}
