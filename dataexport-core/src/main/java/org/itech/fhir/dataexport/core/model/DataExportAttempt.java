package org.itech.fhir.dataexport.core.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.itech.fhir.dataexport.core.model.base.PersistenceEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "data_export_attempt")
public class DataExportAttempt extends PersistenceEntity<Long> {

	public enum DataExportStatus {
		GENERATED('G'), REQUESTING('R'), COLLECTED('C'), EXPORTING('E'), SUCCEEDED('S'), FAILED('F'), INCOMPLETE('I'),
		NOT_RAN('N');

		private char code;

		private DataExportStatus(char code) {
			this.code = code;
		}

		public char getCode() {
			return code;
		}
	}

	// persistence
	@Column(name = "start_time", updatable = false)
	// validation
	@NotNull
	private Instant startTime;

	@Column(name = "end_time")
	private Instant endTime;

	// persistence
	@ManyToOne
	@JoinColumn(name = "data_export_task_id", nullable = false, updatable = false)
	// validation
	@NotNull
	private DataExportTask dataExportTask;

	// persistence
	@Enumerated(EnumType.STRING)
	@Column(name = "data_export_status")
	// validation
	@NotNull
	private DataExportStatus dataExportStatus;

	DataExportAttempt() {
	}

	public DataExportAttempt(DataExportTask dataExportTask) {
		startTime = Instant.now();
		dataExportStatus = DataExportStatus.GENERATED;
		this.dataExportTask = dataExportTask;
	}

}
