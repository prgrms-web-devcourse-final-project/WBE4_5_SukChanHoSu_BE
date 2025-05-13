package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagedMatchingResponse {
    private List<UserMatchingResponse> userMatchingResponses;
    private int totalPages;

    public PagedMatchingResponse(List<UserMatchingResponse> userMatchingResponses, int totalPages) {
        this.userMatchingResponses = userMatchingResponses;
        this.totalPages = totalPages;
    }
}
