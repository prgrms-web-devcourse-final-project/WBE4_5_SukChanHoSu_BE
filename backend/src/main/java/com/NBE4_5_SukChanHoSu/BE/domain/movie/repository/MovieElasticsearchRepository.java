package com.NBE4_5_SukChanHoSu.BE.domain.movie.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.document.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieElasticsearchRepository extends ElasticsearchRepository<MovieDocument, Long> {
    List<MovieDocument> findByTitleContaining(String title); // movieNm 검색
    List<MovieDocument> findByGenresRawContaining(String genresRaw); // genres 검색
    List<MovieDocument> findByDescriptionContaining(String description); // description 검색 (추후 활성화 시)
    List<MovieDocument> findByDirectorContaining(String director); // director 검색 (추후 활성화 시)

    // 자동완성을 위한 메서드 (예시 - 제목 prefix 검색)
    List<MovieDocument> findByTitleStartingWith(String title); // movieNm 자동완성
}
