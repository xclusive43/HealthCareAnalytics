package com.xclusive43.heatlthcare.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        ClassLoader classLoader = FirebaseConfig.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("serviceAccount.json")).getFile());
        FileInputStream serviceAccount =  new FileInputStream(file.getAbsoluteFile());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://healthcare-79fbc-default-rtdb.firebaseio.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();

    }


}
