package fiveguys.innout.controller;

import fiveguys.innout.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/innout")
public class restApiInnoutController {

    // 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        List<Transaction> transactionList = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactionList);
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionDetail(@PathVariable("id") int transactionId){
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    // 추가
    @PostMapping("/add")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction){
        Transaction transaction1 = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(transaction1, HttpStatus.CREATED);
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") int transactionId, @RequestBody Transaction updatedTransaction){
        transactionService.updateTransaction(transactionId, updatedTransaction);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") int transactionId){
        transactionService.deleteTransaction(transactionId);
        String okMsg = String.format("Department id = %s deleted successfully!.", transactionId);
        return ResponseEntity.ok(okMsg);
    }

}
