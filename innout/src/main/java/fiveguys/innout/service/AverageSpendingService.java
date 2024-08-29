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
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AgeGroupService ageGroupService;

    public AverageSpendingDTO calculateAverageSpending(Long userId) {
        String userAgeGroup = ageGroupService.calculateAgeGroup(userId);

        List<User> usersInSameAgeGroup = userRepository.findAll().stream()
                .filter(user -> ageGroupService.calculateAgeGroup(user.getId()).equals(userAgeGroup))
                .collect(Collectors.toList());

        if (usersInSameAgeGroup.isEmpty()) {
            throw new RuntimeException("No users found in the age group: " + userAgeGroup);
        }

        List<Long> userIds = usersInSameAgeGroup.stream()
                .map(User::getId)
                .collect(Collectors.toList());
        List<Transaction> transactions = transactionRepository.findAllByUserIdInAndAmountLessThan(userIds, 0.0);

        double totalSpending = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        long uniqueUserCount = transactions.stream()
                .map(transaction -> transaction.getUser().getId())
                .distinct()
                .count();

        double averageSpending = uniqueUserCount > 0 ? totalSpending / uniqueUserCount : 0.0;

        return new AverageSpendingDTO(userAgeGroup, averageSpending, uniqueUserCount);
    }

    public Map<String, Map<String, Double>> getCategorySpendingComparison(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 1: Calculate the user's age group
        String userAgeGroup = ageGroupService.calculateAgeGroup(userId);

        // Step 2: Calculate the user's spending by category
        Map<String, Integer> userSpendingByCategory = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getAmount() < 0)  // Filter only negative amounts
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingInt(Transaction::getAmount) // Sum the amounts
                ));

        // Step 3: Find all users in the same age group
        List<User> usersInSameAgeGroup = userRepository.findAll().stream()
                .filter(u -> ageGroupService.calculateAgeGroup(u.getId()).equals(userAgeGroup))
                .collect(Collectors.toList());

        long totalUsersInAgeGroup = usersInSameAgeGroup.size();

        // Step 4: Calculate total spending by category for users in the same age group
        Map<String, Integer> totalSpendingByCategory = transactionRepository.findAll().stream()
                .filter(t -> usersInSameAgeGroup.contains(t.getUser())) // Filter transactions by users in the same age group
                .filter(t -> t.getAmount() < 0)  // Filter only negative amounts
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingInt(Transaction::getAmount) // Sum the amounts
                ));

        // Step 5: Calculate average spending by category for users in the same age group
        Map<String, Double> averageSpendingByCategory = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalSpendingByCategory.entrySet()) {
            String category = entry.getKey();
            Integer totalSpending = entry.getValue();
            double averageSpending = totalUsersInAgeGroup > 0 ? (double) totalSpending / totalUsersInAgeGroup : 0.0;
            averageSpendingByCategory.put(category, averageSpending);
        }

        // Step 6: Prepare the comparison result
        Map<String, Map<String, Double>> comparison = new HashMap<>();
        for (String category : userSpendingByCategory.keySet()) {
            Map<String, Double> spendingComparison = new HashMap<>();
            spendingComparison.put("userSpending", (double) userSpendingByCategory.get(category));
            spendingComparison.put("averageSpending", averageSpendingByCategory.getOrDefault(category, 0.0));
            comparison.put(category, spendingComparison);
        }

        // Include categories where the user has no spending but the age group has
        for (String category : averageSpendingByCategory.keySet()) {
            if (!comparison.containsKey(category)) {
                Map<String, Double> spendingComparison = new HashMap<>();
                spendingComparison.put("userSpending", 0.0);  // User didn't spend in this category
                spendingComparison.put("averageSpending", averageSpendingByCategory.get(category));
                comparison.put(category, spendingComparison);
            }
        }

        return comparison;
    }


}
