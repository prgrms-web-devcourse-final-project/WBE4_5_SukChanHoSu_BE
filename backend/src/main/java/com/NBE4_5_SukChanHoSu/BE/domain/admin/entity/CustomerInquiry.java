package com.NBE4_5_SukChanHoSu.BE.domain.admin.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerInquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
