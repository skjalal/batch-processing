package com.example.config;

import com.example.domain.Person;
import com.example.listener.JobCompletionNotificationListener;
import com.example.processor.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

  @Bean
  public FlatFileItemReader<Person> reader() {
    return new FlatFileItemReaderBuilder<Person>()
        .name("personItemReader")
        .resource(new ClassPathResource("sample-data.csv"))
        .delimited()
        .names("firstName", "lastName")
        .fieldSetMapper(this::mapFieldSet)
        .build();
  }

  @Bean
  public ItemProcessor<Person, Person> processor() {
    return new PersonItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Person>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("insert into people (first_name, last_name) values (:firstName, :lastName)")
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Job importUserJob(JobRepository jobRepository, JobExecutionListener listener, Step step1) {
    return new JobBuilder("importUserJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
  }

  @Bean
  public JobExecutionListener listener(JdbcTemplate jdbcTemplate) {
    return new JobCompletionNotificationListener(jdbcTemplate);
  }

  @Bean
  public Step step1(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      JdbcBatchItemWriter<Person> writer,
      ItemProcessor<Person, Person> processor,
      FlatFileItemReader<Person> reader) {
    return new StepBuilder("step1", jobRepository)
        .<Person, Person>chunk(10, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  private Person mapFieldSet(FieldSet fieldSet) {
    var person = new Person();
    person.setFirstName(fieldSet.readString("firstName"));
    person.setLastName(fieldSet.readString("lastName"));
    return person;
  }
}
