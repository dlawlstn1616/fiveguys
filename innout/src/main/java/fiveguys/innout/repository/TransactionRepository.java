package fiveguys.innout.repository;

import fiveguys.innout.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserEmail(String email); // 이메일을 기준으로 거래 내역 검색

    List<Transaction> findByUserId(Long userid);
}