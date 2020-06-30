package com.application.domain;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;

/**
 * Clase que representa un registro a almacenar de adn recibido.
 */
public class DnaReceived {
	public static String collectionName = "dna";

	private String id;
	// El array de adns recibidos.
	private String[] content;

	private boolean isMutant;
	public static String _isMutant = "isMutant";

	public boolean getIsMutant() {
		return this.isMutant;
	}

	public void setIsMutant(boolean mutant) {
		this.isMutant = mutant;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setContent(String[] content) {
		this.content = content;
	}

	public String getId() {
		return this.id;
	}

	public String[] getContent() {
		return this.content;
	}

	public DnaReceived(String[] content, Boolean isMutant){
		setIsMutant(isMutant);
		setContent(content);
		setId(new ObjectId().toString());
	}

	public Document toDocument(){
		Document document = new Document("_id", getId());
		document.put("dna", Arrays.asList(getContent()));
		document.put("isMutant", getIsMutant());
		return document;
	}
}
