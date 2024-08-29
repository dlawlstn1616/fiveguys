package fiveguys.innout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AverageSpendingDTO {
    private String userAgeGroup;
    private double averageSpending;  // Changed to double to accommodate decimal values
    private long uniqueUserCount;    // Added to represent the number of users

    // Constructor
    public AverageSpendingDTO(String userAgeGroup, double averageSpending, long uniqueUserCount) {
        this.userAgeGroup = userAgeGroup;
        this.averageSpending = averageSpending;
        this.uniqueUserCount = uniqueUserCount;
    }
}
