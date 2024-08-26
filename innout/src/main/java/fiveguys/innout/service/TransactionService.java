package fiveguys.innout.service;

import fiveguys.innout.dto.TransactionDto;
import fiveguys.innout.entity.Category;
import fiveguys.innout.entity.Transaction;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.CategoryRepository;
import fiveguys.innout.repository.TransactionRepository;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction createTransaction(TransactionDto transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setDate(transactionDTO.getDate());
        transaction.setAmount(transactionDTO.getAmount());

        Optional<Category> category = categoryRepository.findById(transactionDTO.getCategoryId());
        Optional<User> user = userRepository.findById(transactionDTO.getUserId());

        category.ifPresent(transaction::setCategory);
        user.ifPresent(transaction::setUser);

        transaction.setDescription(transactionDTO.getDescription());

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionDto transactionDTO) {
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
}
