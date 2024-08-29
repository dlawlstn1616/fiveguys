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

        // 각 카테고리별로 가장 큰 소비금액을 가진 그룹을 추출
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(
                                        t -> ageGroupService.calculateAgeGroup(t.getUser().getId()) + "-" + t.getUser().getGender(),
                                        Collectors.summingInt(Transaction::getAmount)
                                ),
                                ageGenderMap -> ageGenderMap.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .orElseThrow(() -> new RuntimeException("No transactions found"))
                        )
                ));
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
