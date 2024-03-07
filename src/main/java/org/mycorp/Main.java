package org.mycorp;

import org.mycorp.models.AbstractEntity;
import org.mycorp.models.Category;
import org.mycorp.services.CategoryService;
import org.mycorp.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Main{
    @Autowired
    CategoryService service;


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {

        List<Category> staticCategoryPack = new ArrayList<>();
        staticCategoryPack.add(new Category(3, "No category"));
        staticCategoryPack.add(new Category(2, "Family"));
        staticCategoryPack.add(new Category(3,"Work"));
        staticCategoryPack.add(new Category(4,"Personal"));
        staticCategoryPack.add(new Category(5,"Sport"));
        staticCategoryPack.add(new Category(6,"Study"));

        staticCategoryPack.forEach(category -> service.create(category));
    }

}

@Configuration
@EnableJpaRepositories(basePackages = "org.mycorp.repository")
class Configur {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}