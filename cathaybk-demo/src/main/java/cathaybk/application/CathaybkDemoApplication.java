package cathaybk.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("controller,config,bean,service,component,schedule")
@EnableMongoRepositories("repository")
@EntityScan("entity")
@EnableScheduling
@SpringBootApplication
public class CathaybkDemoApplication {


  public static void main(String[] args) {
    SpringApplication.run(CathaybkDemoApplication.class, args);

  }
}
