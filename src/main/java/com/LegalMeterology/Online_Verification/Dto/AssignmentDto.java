package com.LegalMeterology.Online_Verification.Dto;

import com.LegalMeterology.Online_Verification.Enums.AssignmentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {
 
   
    @NotBlank(message = "Application Number Required")
    private String applicationNumber;

    @NotNull(message = "Assigned To Type Required")
    private AssignmentType assignedToType;

    @NotBlank(message = "Assigned To Required")
    private String assignedToId;

    private String remarks;

}
