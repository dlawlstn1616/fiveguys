package fiveguys.innout.service;

import fiveguys.innout.dto.AverageSpendingDTO;
import fiveguys.innout.entity.Transaction;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.TransactionRepository;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AverageSpendingService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgeGroupService ageGroupService;

    public AverageSpendingDTO calculateAverageSpending(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userAgeGroup = ageGroupService.calculateAgeGroup(userId);

        // 해당 연령대의 모든 사용자를 가져옴
        List<User> usersInSameAgeGroup = userRepository.findAll().stream()
                .filter(u -> ageGroupService.calculateAgeGroup(u.getId()).equals(userAgeGroup))
                .collect(Collectors.toList());

        // 연령대에 속한 사용자의 총 지출을 계산 (음수 값을 절대값으로 변환하여 합산)
        int totalSpending = usersInSameAgeGroup.stream()
                .mapToInt(u -> transactionRepository.findAll().stream()
                        .filter(t -> t.getUser().getId().equals(u.getId()))
                        .mapToInt(t -> Math.abs(t.getAmount())) // 절대값으로 변경
                        .sum()
                )
                .sum();

        // 연령대 평균 지출을 계산
        int averageSpending = totalSpending / usersInSameAgeGroup.size();

        AverageSpendingDTO dto = new AverageSpendingDTO();
        dto.setUserAgeGroup(userAgeGroup);
        dto.setAverageSpending(averageSpending);

        return dto;
    }

    // 카테고리별 사용자와 동연령대 평균 지출 비교
    public Map<String, Map<String, Integer>> getCategorySpendingComparison(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userAgeGroup = ageGroupService.calculateAgeGroup(userId);

        // 사용자의 카테고리별 지출 계산 (음수 값을 절대값으로 변환하여 합산)
        Map<String, Integer> userSpendingByCategory = transactionRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingInt(t -> Math.abs(t.getAmount())) // 절대값으로 변경
                ));

        // 동연령대 사용자의 카테고리별 평균 지출 계산 (음수 값을 절대값으로 변환하여 합산)
        Map<String, Integer> averageSpendingByCategory = transactionRepository.findAll().stream()
                .filter(t -> ageGroupService.calculateAgeGroup(t.getUser().getId()).equals(userAgeGroup))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingInt(t -> Math.abs(t.getAmount())) // 절대값으로 변경
                ));

        // 사용자와 동연령대 평균 지출 비교 결과 반환
        Map<String, Map<String, Integer>> comparison = new HashMap<>();
        for (String category : userSpendingByCategory.keySet()) {
            Map<String, Integer> spending = new HashMap<>();
            spending.put("userSpending", userSpendingByCategory.get(category));
            spending.put("averageSpending", averageSpendingByCategory.getOrDefault(category, 0));
            comparison.put(category, spending);
        }
        return comparison;
    }
}
