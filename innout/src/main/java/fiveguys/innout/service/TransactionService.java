package fiveguys.innout.service;

import fiveguys.innout.dto.TransactionDTO;
import fiveguys.innout.entity.Category;
import fiveguys.innout.entity.Transaction;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.CategoryRepository;
import fiveguys.innout.repository.TransactionRepository;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgeGroupService ageGroupService;

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setDate(transactionDTO.getDate());
        transaction.setAmount(transactionDTO.getAmount());

        Category category = categoryRepository.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        User user = userRepository.findById(transactionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        transaction.setCategory(category);
        transaction.setUser(user);

        transaction.setDescription(transactionDTO.getDescription());

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);

        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setDate(transactionDTO.getDate());
            transaction.setAmount(transactionDTO.getAmount());

            Optional<Category> category = categoryRepository.findById(transactionDTO.getCategoryId());
            Optional<User> user = userRepository.findById(transactionDTO.getUserId());

            category.ifPresent(transaction::setCategory);
            user.ifPresent(transaction::setUser);

            transaction.setDescription(transactionDTO.getDescription());

            return transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Transaction not found");
        }
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // 클래스 안에 Logger 추가
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public Map<String, Map.Entry<String, Integer>> getTopSpend() {
        List<Transaction> transactions = transactionRepository.findAll();
        logger.info("Total transactions fetched: " + transactions.size());

        // 각 카테고리별로 amount가 음수인 값들의 합이 가장 큰 그룹을 추출
        Map<String, Map.Entry<String, Integer>> topSpendsByCategory = transactions.stream()
                .filter(t -> t.getAmount() < 0) // amount가 음수인 거래만 필터링
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(
                                        t -> ageGroupService.calculateAgeGroup(t.getUser().getId()) + "-" + t.getUser().getGender(),
                                        Collectors.summingInt(Transaction::getAmount) // 음수 값만 합산
                                ),
                                ageGenderMap -> ageGenderMap.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .orElse(null) // 예외를 던지는 대신 null을 반환하거나 다른 처리 가능
                        )
                ));

        // 로깅 추가: 결과 출력
        topSpendsByCategory.forEach((category, entry) -> {
            if (entry != null) {
                logger.info("Category: " + category + ", Top Group: " + entry.getKey() + ", Spending: " + entry.getValue());
            } else {
                logger.warn("Category: " + category + " has no negative spending groups.");
            }
        });

        return topSpendsByCategory;
    }

    // 로그인한 사용자의 총 지출을 계산하는 메서드
    public int calculateUserTotalSpending(Long userId) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId))
                .mapToInt(Transaction::getAmount)
                .sum();
    
    }
    public List<Transaction> getTransactionsByEmail(String email) {
        // 이메일을 기반으로 거래 내역 검색
        return transactionRepository.findByUserEmail(email);
    }



    public List<Transaction> getTransactionByUserId(Long userid) {
        return transactionRepository.findByUserId(userid);
    }
}
