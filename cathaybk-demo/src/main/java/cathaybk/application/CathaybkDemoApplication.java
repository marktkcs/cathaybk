package cathaybk.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("controller,config,bean,service,component,schedule")
@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class CathaybkDemoApplication {


  public static void main(String[] args) {
    SpringApplication.run(CathaybkDemoApplication.class, args);

  }
}
