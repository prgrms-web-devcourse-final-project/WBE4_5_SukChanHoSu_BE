package com.NBE4_5_SukChanHoSu.BE.global.init;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.GenreService;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.MovieRepository;
import com.NBE4_5_SukChanHoSu.BE.global.app.AppConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class MovieInitData {

    @Autowired
    @Lazy
    private MovieInitData self;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private final GenreService genreService;

    @Autowired
    private final Map<Long, Long> MovieIdToTid;

    @Bean
    @Order(1)
    public ApplicationRunner applicationRunner() {
        return args -> {
            if (movieRepository.count() > 0) {
                System.out.println("🎬 이미 Movie 데이터가 존재하여 초기화를 건너뜁니다.");
                return;
            }
            self.movieInit();
        };
    }

    @Transactional
    public void movieInit() throws IOException {
        File csv = new File("backend/src/main/resources/data/ml-latest-small/movies.csv");
        BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(csv)));

        String line = "";
        boolean skipFirstLine = true;
        while ((line = br.readLine()) != null) {
            if(skipFirstLine) {
                skipFirstLine = false;
                continue;
            }

            String[] token = line.split(",");
            Long movieId = Long.parseLong(token[0]);
            String[] genre = token[token.length - 1].split("\\|");

            StringBuilder title = new StringBuilder();
            for(int i = 1; i < token.length - 1; i++) {
                title.append(token[i]);
                if(i != token.length-2) title.append(",");
            }

            movieRepository.save(Movie.builder()
                    .id(movieId)
                    .tId(MovieIdToTid.get(movieId))
                    .title(title.toString())
                    .genres(Arrays.stream(genre)
                            .map(genreService::findOrCreateNew)
                            .collect(Collectors.toSet()))
                    .build());

        }
    }
}
