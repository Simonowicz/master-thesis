package pl.edu.eiti.pw.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import pl.edu.eiti.pw.model.BasicDBObjectKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Component converting POJOs into DBObjects understood by MongoDB
 */
@Component
public class MongoConverter<DOMAIN_OBJECT> {

    public DBObject convertToDbObject(DOMAIN_OBJECT object) {
        BasicDBObject dbObject = new BasicDBObject();
        for(Field field : object.getClass().getDeclaredFields()) {
            String key = getDbObjectKey(field);
            dbObject.put(key, findAndInvokeGetter(object, field.getName()));
        }

        return dbObject;
    }

    public DOMAIN_OBJECT convertToDomainObject(DBObject dbObject, Class<DOMAIN_OBJECT> domainObjectClass) {
        DOMAIN_OBJECT domainObject;
        try {
            domainObject = domainObjectClass.newInstance();
            for (Field field : domainObjectClass.getDeclaredFields()) {
                String key = getDbObjectKey(field);
                Object dbObjectValue = dbObject.get(key);
                if (!isCollectionsType(field)) {
                    findAndInvokeSetter(domainObject, field.getName(), dbObjectValue);
                } else {
                    findAndInvokeAdder(domainObject, field.getName(), dbObjectValue);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return domainObject;
    }

    private String getDbObjectKey(Field field) {
        String key = field.getName();
        if (field.isAnnotationPresent(BasicDBObjectKey.class)) {
            key = field.getAnnotation(BasicDBObjectKey.class).value();
        }
        return key;
    }

    private void findAndInvokeSetter(DOMAIN_OBJECT domainObject, String key, Object dbObjectValue) {
        try {
            PropertyUtils.setProperty(domainObject, key, dbObjectValue);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private void findAndInvokeAdder(DOMAIN_OBJECT domainObject, String fieldName, Object dbObjectValue) {
        for (Method method : domainObject.getClass().getDeclaredMethods()) {
            if (isMethodNameAdderForFieldName(method.getName(), fieldName)) {
                try {
                    //TODO: find a way to get all the objects from underlying collection in dbObjectValue
                    method.invoke(domainObject, dbObjectValue);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    private boolean isMethodNameAdderForFieldName(String methodName, String fieldName) {
        return methodName.contains("add") && methodName.contains(fieldName.substring(0, fieldName.length() - 2));
    }

    private Object findAndInvokeGetter(DOMAIN_OBJECT object, String fieldName) {
        try {
            return PropertyUtils.getProperty(object, fieldName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isCollectionsType(Field field) {
        return field.getType().isAssignableFrom(Collection.class);
    }
}
