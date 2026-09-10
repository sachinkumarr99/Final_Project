package com.LegalMeterology.Online_Verification.Exceptions;

public class VerificationException extends RuntimeException{
    
     public VerificationException(String message) {
        super(message);
    }
    public VerificationException(){
        super("Instrument Not Verified");
    }
}
