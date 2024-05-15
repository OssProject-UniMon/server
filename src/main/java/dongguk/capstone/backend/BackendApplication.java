package dongguk.capstone.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling// 스프링 스케쥴러를 사용하기 위해 @Scheduled 애노테이션을 선언할 때, 스프링 부트 실행파일(Application 파일)에 @EnableScheduling을 선언해야 사용할 수 있습니다. @EnableScheduling을 선언하지 않으면, 스케쥴러 소스를 작성하더라도 실행되지 않습니다.
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
