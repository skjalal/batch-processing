package com.example.processor;

import com.example.domain.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
  @Override
  public Person process(Person item) {
    log.info("Processing records: {}", item);
    return item;
  }
}
