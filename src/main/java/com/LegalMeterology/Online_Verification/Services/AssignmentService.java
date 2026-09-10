package com.LegalMeterology.Online_Verification.Services;

import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.LegalMeterology.Online_Verification.Dto.AssignmentDto;
import com.LegalMeterology.Online_Verification.Entities.Assignment;
import com.LegalMeterology.Online_Verification.Entities.User;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Enums.AssignmentType;
import com.LegalMeterology.Online_Verification.Exceptions.DuplicateResourceException;
import com.LegalMeterology.Online_Verification.Mappers.AssignmentMapper;
import com.LegalMeterology.Online_Verification.Repositories.AssignmentRepo;
import com.LegalMeterology.Online_Verification.Repositories.UserRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo.VerificationApplicationRepo;
import com.LegalMeterology.Online_Verification.Security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentService {
 
    private final AssignmentRepo assignmentRepo;
    private final SecurityUtils securityUtils;
    private final VerificationApplicationRepo verificationApplicationRepo;
    private final UserRepo userRepo;

    
    public Assignment assigned(AssignmentDto assignmentDto){

        // check application exist or not
        VerificationApplication assignedApplication=verificationApplicationRepo.findByApplicationNumber(assignmentDto.getApplicationNumber()).orElseThrow( ()->new UsernameNotFoundException("Application not exist"));

        // check application is already assigned or not
        if(assignmentRepo.existsByApplicationNumberAndAssignedById(assignmentDto.getApplicationNumber(),securityUtils.getCurrentUserId())){
            User user=userRepo.findById(new ObjectId(assignmentDto.getAssignedToId())).orElseThrow(()-> new UsernameNotFoundException("User who added application Not Exist"));
            throw new DuplicateResourceException("Application Already Assigned to : "+user.getFirstName()+" "+user.getLastName());
        }

        // check officer valid or not
        if(assignmentDto.getAssignedToType()==AssignmentType.LMO || assignmentDto.getAssignedToType()==AssignmentType.GATC_OFFICER){
            User user=userRepo.findById(new ObjectId(assignmentDto.getAssignedToId())).orElseThrow(()-> new UsernameNotFoundException("LMO/GATC_OFFICER Not exist"));
            if(!user.getRole().name().equals(AssignmentType.LMO.name())){
                throw new UsernameNotFoundException("Not LMO");
            }
        }

        // check organization valid or not
        if(assignmentDto.getAssignedToType()==AssignmentType.GATC_ORGANIZATION){
            User user=userRepo.findById(new ObjectId(assignmentDto.getAssignedToId())).orElseThrow(()-> new UsernameNotFoundException("LMO/GATC_OFFICER Not exist"));
            if(!user.isOrganization()){
                throw new UsernameNotFoundException("Not an organziation");
            }
        }

        // it will automatically update status ASSIGNED and save it
        Assignment assignedAssignment=assignmentRepo.save(AssignmentMapper.toEntity(assignmentDto, securityUtils.getCurrentUserId()));

        // update verification application status
        
        assignedApplication.setStatus(assignedAssignment.getStatus());
        verificationApplicationRepo.save(assignedApplication);

        

        return assignedAssignment;
    }

}
