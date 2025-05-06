package com.NBE4_5_SukChanHoSu.BE.domain.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Genre findOrCreateNew(String name) {
        return genreRepository.findByName(name).orElseGet(
                () -> genreRepository.save(new Genre(name))
        );
    }
}
