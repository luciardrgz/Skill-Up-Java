package com.alkemy.wallet.exceptions;

public class TransactionError extends RuntimeException{
    public TransactionError (String error) { super(error );}
}