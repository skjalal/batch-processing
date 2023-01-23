package com.example.listener;

import com.example.domain.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JobCompletionNotificationListener implements JobExecutionListener {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("BatchJob Starting... {}", jobExecution.getJobId());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");
      jdbcTemplate
          .query("select first_name, last_name from people", this::mapRow)
          .forEach(log::info);
    } else {
      log.info("JobStatus: {}", jobExecution.getStatus());
    }
  }

  private String mapRow(ResultSet rs, int rowNum) {
    log.debug("Row: {}", rowNum);
    var person = new Person();
    try {
      person.setFirstName(rs.getString(1));
      person.setLastName(rs.getString(2));
    } catch (Exception e) {
      log.error("Failed to parse data from SQL", e);
    }
    return person.toString();
  }
}
