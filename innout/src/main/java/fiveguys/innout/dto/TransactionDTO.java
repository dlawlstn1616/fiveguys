package fiveguys.innout.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionDTO {
    private Long id;
    private Date date;
    private Integer amount;
    private Long categoryId;
    private Long userId;
    private String description;

    // Getters and Setters
}
