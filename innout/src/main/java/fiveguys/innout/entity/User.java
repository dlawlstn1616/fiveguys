package fiveguys.innout.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Temporal(TemporalType.DATE)  // 날짜만 저장하는 경우 사용
    private Date birthDate;
    // Getters and Setters

}
