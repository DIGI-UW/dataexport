package org.itech.fhir.dataexport.core.model.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class PersistenceEntity<I> {

	@Id
	@GeneratedValue
	I id;

}
