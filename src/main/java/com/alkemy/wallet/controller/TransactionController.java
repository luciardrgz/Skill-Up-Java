package com.alkemy.wallet.controller;

import com.alkemy.wallet.dto.ResponseTransactionDto;
import com.alkemy.wallet.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("transactions")
@RestController
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ResponseTransactionDto> saveDeposit(
            @RequestBody ResponseTransactionDto deposit){
        ResponseTransactionDto depositCreated = transactionService.save(deposit);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositCreated);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getListTransactionByAdminUser(@PathVariable("userId") Long userId){

        //IF PARA VALIDAR USUARIO ADMINISTRADOR

        List<ResponseTransactionDto> listTransactionsByUser = transactionService.findByUserId(userId);
        if(listTransactionsByUser.isEmpty()){
            return new ResponseEntity<>("User doesn't exist or has not transactions", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(listTransactionsByUser, HttpStatus.OK);
        }
    }

}
