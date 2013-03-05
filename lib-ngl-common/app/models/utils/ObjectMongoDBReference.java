package models.utils;

import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;


/**
 * Object used to retrieve an object define in MongoDB.
 * T is type object mapping with a collection, T extends DBObject
 * Code : the data processing label
 * ClassName : Class extends DBObject 
 * 
 * @author mhaquell
 *
 */
public class ObjectMongoDBReference<T extends DBObject> implements IFetch<T> {
		
	@JsonIgnore
	private Class<T> className;
	public String code;
	
	
	public ObjectMongoDBReference() {
		
	}
	
	public ObjectMongoDBReference(Class<T> className,String code){
		
		this.className=className;
		this.code=code;
		
	}
	
	@Override
	public T getObject() throws Exception {
		MongoCollection annotation =className.getAnnotation(MongoCollection.class);		
		return MongoDBDAO.findByCode(annotation.name(),className,code);
	}

}
