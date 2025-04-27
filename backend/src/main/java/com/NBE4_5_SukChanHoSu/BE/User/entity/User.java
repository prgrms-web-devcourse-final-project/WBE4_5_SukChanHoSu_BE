package com.NBE4_5_SukChanHoSu.BE.User.entity;

import com.NBE4_5_SukChanHoSu.BE.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private LocalDate birthdate;

    @Column(length = 10)
    private String gender;

    @Column(length = 255)
    private String profileImage;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

}
