package com.LegalMeterology.Online_Verification.Exceptions;

public class DuplicateResourceException extends RuntimeException{
    
    public DuplicateResourceException(String msg){
        super(msg);
    }
    public DuplicateResourceException(){
        super("Trying to insert duplicate data");
    }
}
