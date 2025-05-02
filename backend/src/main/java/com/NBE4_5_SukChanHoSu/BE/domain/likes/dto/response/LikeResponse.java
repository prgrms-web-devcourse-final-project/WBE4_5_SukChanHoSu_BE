package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {
    private UserLikes likes;
}
