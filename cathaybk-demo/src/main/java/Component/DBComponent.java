package Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Aspect
@Component
public class DBComponent {

  private static final Logger logger = LoggerFactory.getLogger(DBComponent.class);

  @Value("${mongodb.uri}")
  private String uri;

  @Value("${mongodb.database}")
  private String databaseName;

  @Around("execution(* service..*(..))")
  public Object modifyParameters(ProceedingJoinPoint joinPoint) throws Throwable {
    try (MongoClient mongoClient = MongoClients.create(uri)) {

      MongoDatabase database = mongoClient.getDatabase(databaseName);

      Object[] args = joinPoint.getArgs();


      if (args.length > 1 && args[1] instanceof Object) {
        args[1] = database;
      }


      return joinPoint.proceed(args);
    } catch (Throwable e) {
      logger.error(" opSession is exception :" + e.getMessage());

    }
    return joinPoint;
  }

}
