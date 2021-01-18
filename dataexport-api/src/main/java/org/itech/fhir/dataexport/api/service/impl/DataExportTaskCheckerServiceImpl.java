package org.itech.fhir.dataexport.api.service.impl;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.itech.fhir.dataexport.api.service.DataExportService;
import org.itech.fhir.dataexport.api.service.DataExportSource;
import org.itech.fhir.dataexport.api.service.DataExportTaskCheckerService;
import org.itech.fhir.dataexport.core.model.DataExportTask;
import org.itech.fhir.dataexport.core.service.DataExportTaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataExportTaskCheckerServiceImpl implements DataExportTaskCheckerService {

	private DataExportTaskService dataExportTaskService;
	private DataExportService dataExportService;
	private DataExportSource dataExportSource;

	public DataExportTaskCheckerServiceImpl(DataExportTaskService dataExportTaskService,
			DataExportService dataExportService, DataExportSource dataExportSource) {
		log.info(this.getClass().getName() + " has started");
		this.dataExportTaskService = dataExportTaskService;
		this.dataExportService = dataExportService;
		this.dataExportSource = dataExportSource;
	}

	@Override
	@Scheduled(initialDelay = 10 * 1000, fixedRate = 60 * 1000)
	@Transactional
	public void checkDataRequestNeedsRunning() {
		log.trace("checking if servers need data import to be done");

		Iterable<DataExportTask> dataExportTasks = dataExportTaskService.getDAO().findAll();
		for (DataExportTask dataExportTask : dataExportTasks) {
			if (dataExportTask.getActive()) {
				if (maximumTimeHasPassed(dataExportTask)) {
					log.debug("server found with dataExportTask needing to be run");
					try {
						dataExportService.exportNewDataFromSourceToRemote(dataExportTask, dataExportSource);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						log.error("error while exporting data", e);
					}
				}
			}
		}
	}

	private boolean maximumTimeHasPassed(DataExportTask dataExportTask) {
		if (dataExportTask.getMaxDataExportInterval() == 0 || dataExportTask.getMaxDataExportInterval() == null) {
			return false;
		}
		Instant now = Instant.now();
		Instant lastAttemptInstant = dataExportTaskService.getLatestInstantForDataExportTask(dataExportTask);
		Instant nextScheduledRuntime = lastAttemptInstant.plus(dataExportTask.getMaxDataExportInterval(),
				DataExportTask.MAX_INTERVAL_UNITS);
		return now.isAfter(nextScheduledRuntime);
	}

}
