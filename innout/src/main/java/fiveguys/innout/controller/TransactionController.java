package fiveguys.innout.controller;

import fiveguys.innout.dto.AverageSpendingDTO;
import fiveguys.innout.dto.TransactionDTO;
import fiveguys.innout.dto.UserSpendingDTO;
import fiveguys.innout.entity.Transaction;
import fiveguys.innout.service.AverageSpendingService;
import fiveguys.innout.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/innout")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AverageSpendingService averageSpendingService;


    @Operation(summary = "내역 생성", description = "새로운 내역을 생성합니다.")
    @PostMapping("/add")
    public Transaction createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }
    @Operation(summary = "내역 수정", description = "주어진 ID를 사용하여 기존 내역을 수정합니다.")
    @PatchMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(id, transactionDTO);
    }
    @Operation(summary = "내역 삭제", description = "주어진 ID를 사용하여 내역을 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
    @Operation(summary = "모든 내역 조회", description = "모든 내역을 조회합니다.")
    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
    @Operation(summary = "내역 상세 조회", description = "주어진 ID로 특정 낸역을 조회합니다.")
    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @Operation(summary = "유저 아이디로 거래 내역 조회", description = "특정 유저 아이디를 기반으로 모든 거래 내역을 조회합니다..")
    @GetMapping("/transaction/{userid}")
    public List<Transaction> getTransactionsByUserId(@PathVariable Long userid) {
        return transactionService.getTransactionByUserId(userid);
    }

    @GetMapping("/consume")
    public List<Map<String, Object>> getTopSpend() {
        List<Map<String, Object>> result = new ArrayList<>();

        transactionService.getTopSpend().forEach((category, entry) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", category);
            map.put("group", entry.getKey());
            map.put("amount", entry.getValue());
            result.add(map);
        });

        return result;
    }

    @GetMapping("/user-spending/{userId}")
    public UserSpendingDTO getUserSpending(@PathVariable Long userId) {
        int totalSpending = transactionService.calculateUserTotalSpending(userId);
        UserSpendingDTO userSpendingDTO = new UserSpendingDTO(totalSpending);
        return userSpendingDTO;
    }

    @GetMapping("/average-spending/{userId}")
    public AverageSpendingDTO getAverageSpending(@PathVariable Long userId) {
        return averageSpendingService.calculateAverageSpending(userId);
    }

}
