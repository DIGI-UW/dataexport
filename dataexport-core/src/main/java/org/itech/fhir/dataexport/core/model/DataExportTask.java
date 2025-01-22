package org.itech.fhir.dataexport.core.model;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hl7.fhir.r4.model.ResourceType;
import org.itech.fhir.dataexport.core.model.base.PersistenceEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "data_export_task")
public class DataExportTask extends PersistenceEntity<Long> {

	public static final TemporalUnit MAX_INTERVAL_UNITS = ChronoUnit.MINUTES;
	public static final TemporalUnit TIMEOUT_UNITS = ChronoUnit.SECONDS;

	// persistence
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "data_export_fhir_resources", joinColumns = @JoinColumn(name = "data_export_task_id"))
	@Column(name = "fhir_resources")
	@Enumerated(EnumType.STRING)
	// validation
	@NotNull
	private List<ResourceType> fhirResources;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "key")
	@Column(name = "value")
	@CollectionTable(name = "data_export_headers", joinColumns = @JoinColumn(name = "data_export_task_id"))
	// validation
	@NotNull
	private Map<String, String> headers;

	@Column(name = "data_request_attempt_timeout")
	private Integer dataRequestAttemptTimeout;

	@Column(name = "max_data_export_interval")
	private Integer maxDataExportInterval;

	@Column(name = "endpoint")
	private String endpoint;

	public DataExportTask() {
	}

}
