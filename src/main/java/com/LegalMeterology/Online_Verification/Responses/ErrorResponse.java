package com.LegalMeterology.Online_Verification.Responses;

import lombok.Builder;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;

    private String error;

    private String message;

    private String apiPath;

    private LocalDateTime timestamp;
}