package com.LegalMeterology.Online_Verification.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.LegalMeterology.Online_Verification.Dto.VerificationApplicationDto;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Enums.Machine.DocumentCategory;
import com.LegalMeterology.Online_Verification.Exceptions.DuplicateResourceException;
import com.LegalMeterology.Online_Verification.Mappers.VerificationApplicationMapper;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentDocumentRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo.VerificationApplicationRepo;
import com.LegalMeterology.Online_Verification.Responses.RegistersResponse.VerificationApplicationResponse;
import com.LegalMeterology.Online_Verification.Security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationApplicationService {

    private final VerificationApplicationRepo applicationRepository;
    private final InstrumentService instrumentService;
    private final VerificationApplicationMapper mapper;
    private final SecurityUtils securityUtils;
    private final InstrumentDocumentRepo instrumentDocumentRepo;
    private final SequenceService sequenceService;
    

    public boolean isApplicationExist(String instrumentNumber){
       return applicationRepository.existsByInstrumentNumber(instrumentNumber);
    }
    

    public VerificationApplicationResponse submitApplication(VerificationApplicationDto dto) {


        String instrumentNumber = dto.getInstrumentNumber();

           // check instrument exist or not for submit application
        if(!instrumentService.isInstrumentExist(instrumentNumber)){
                throw new UsernameNotFoundException("Instrument Not Exist");
        }

        // check application exists or not 
        if(isApplicationExist(instrumentNumber)){
                throw new DuplicateResourceException("Application Already exists");
        }

        List<DocumentCategory> requiredCategories = List.of(
        DocumentCategory.INSTRUMENT_PHOTO,
        DocumentCategory.PURCHASE_INVOICE,
        DocumentCategory.MANUFACTURER_DOCUMENT
        );

        StringJoiner notUploadedDocuments=new StringJoiner(", ");
     
        for (DocumentCategory category : requiredCategories) {

            boolean exists =
            instrumentDocumentRepo
                    .existsByInstrumentNumberAndCategory(
                            instrumentNumber,
                            category
                    );

             if (!exists) {
                notUploadedDocuments.add(category.name());
             }
        }

        if(notUploadedDocuments.length()>0){
                throw new UsernameNotFoundException("Documents ( "+notUploadedDocuments+" ) Not Uploaded");
        }


        //convert application VerificationApplication entity
        VerificationApplication application =
                mapper.toEntity(
                        dto,
                        securityUtils.getCurrentUserId(),
                        generateApplicationNumber()
                );

        // save application
        VerificationApplication savedApplication=applicationRepository.save(application);
        return VerificationApplicationResponse.builder()
                                              .applicationNumber(savedApplication.getApplicationNumber())
                                              .instrumentNumber(savedApplication.getInstrumentNumber())
                                              .status(savedApplication.getStatus())
                                              .build();
    }

    public String generateApplicationNumber(){

        Long sequence = sequenceService.getNextSequence("application");

        return String.format(
                "APP-%d-%09d",
                LocalDateTime.now().getYear(),
                sequence
        );

    }

    public List<VerificationApplication> getAllUserApplication(){
        
        return applicationRepository.findAllByUserId(securityUtils.getCurrentUserId());
    }
}
