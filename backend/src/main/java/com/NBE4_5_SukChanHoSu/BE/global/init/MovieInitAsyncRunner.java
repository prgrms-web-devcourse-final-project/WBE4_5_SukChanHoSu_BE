package com.NBE4_5_SukChanHoSu.BE.global.init;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MovieInitAsyncRunner {
    private final MovieInitData movieInitData;
    private final MovieRepository movieRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void runInitAsync() throws IOException {
        if (movieRepository.count() > 0) {
            System.out.println("🎬 이미 Movie 데이터가 존재하여 초기화를 생략합니다.");
            return;
        }

        movieInitData.movieInit(); // ✅ 비동기 실행
    }
}
