package profitsoft.intership.songsplaylist;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import profitsoft.intership.songsplaylist.config.ApplicationConfig;
import profitsoft.intership.songsplaylist.config.WebConfig;


@SpringBootApplication
@Import({ApplicationConfig.class, WebConfig.class})
@EnableTransactionManagement
public class SongsPlaylistApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SongsPlaylistApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }


}