package com.LegalMeterology.Online_Verification.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationReadingDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationScheduleDto;
import com.LegalMeterology.Online_Verification.Entities.Assignment;
import com.LegalMeterology.Online_Verification.Entities.Instrument;
import com.LegalMeterology.Online_Verification.Entities.InstrumentDocument;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.StandardTestPoint;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.Verification;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationReading;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationSchedule;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationStandard;
import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;
import com.LegalMeterology.Online_Verification.Enums.Machine.Status;
import com.LegalMeterology.Online_Verification.Exceptions.DuplicateResourceException;
import com.LegalMeterology.Online_Verification.Exceptions.VerificationException;
import com.LegalMeterology.Online_Verification.Repositories.AssignmentRepo;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentDocumentRepo;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo.VerificationApplicationRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationScheduleRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationStandardRepo;
import com.LegalMeterology.Online_Verification.Security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LMOService {

    private final SecurityUtils securityUtils;
    private final AssignmentRepo assignmentRepo;
    private final VerificationApplicationRepo applicationRepo;
    private final InstrumentRepo instrumentRepo;
    private final DocumentService documentService;
    private final VerificationScheduleRepo scheduleRepo;
    private final VerificationRepo verificationRepo;
    private final VerificationStandardRepo standardRepo;
    

   public List<Assignment> getAssignedApplications() {

    String lmoId = securityUtils.getCurrentUserId();
    return assignmentRepo.findAllByAssignedToId(lmoId);

    }

    public VerificationApplication getApplication(
            String applicationNumber) {

        return applicationRepo
                .findByApplicationNumber(applicationNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Application not exist"
                        ));
    }

    public VerificationSchedule schedule(
            String applicationNumber,
            VerificationScheduleDto dto) {

        String lmoId = securityUtils.getCurrentUserId();

        Assignment assignment =
                assignmentRepo.findByApplicationNumber(applicationNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Application is not assigned"
                        ));

        if (!assignment.getAssignedToId().equals(lmoId)) {
            throw new AccessDeniedException(
                    "Application is not assigned to you"
            );
        }

        // check document uploaded or not
        if(!documentService.isUploadedAllInstrumentDocument(applicationNumber)){
                throw new UsernameNotFoundException("Documents Not Uploaded Properly");
        }

        if(!documentService.isAllInstrumentDocumentVerify(applicationNumber)){
                throw new VerificationException("Instrument Documents Not Verified , first verify them");
        }

        if (scheduleRepo
                .findByApplicationNumber(applicationNumber)
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Application already scheduled"
            );
        }

        VerificationSchedule schedule =
                VerificationSchedule.builder()
                        .applicationNumber(applicationNumber)
                        .scheduledById(lmoId)
                        .scheduledDate(dto.getScheduledDate())
                        .build();

        VerificationSchedule saved =
                scheduleRepo.save(schedule);

        VerificationApplication application =
                applicationRepo
                        .findByApplicationNumber(applicationNumber)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Application not exist"
                                ));

        application.setStatus(ApplicationStatus.SCHEDULED);
        assignment.setStatus(ApplicationStatus.SCHEDULED);
        assignmentRepo.save(assignment);

        applicationRepo.save(application);



        return saved;
    }

    //   ye verify logic likha hai yaha

    public Verification verify(
        String applicationNumber,
        VerificationDto dto) {

    String lmoId = securityUtils.getCurrentUserId();

    Assignment assignment =
            assignmentRepo.findByApplicationNumber(applicationNumber)
            .orElseThrow(() ->
                    new UsernameNotFoundException(
                            "Application is not assigned"
                    ));

    if (!assignment.getAssignedToId().equals(lmoId)) {
        throw new AccessDeniedException(
                "Application is not assigned to you"
        );
    }

    VerificationSchedule schedule =
            scheduleRepo.findByApplicationNumber(applicationNumber)
            .orElseThrow(() ->
                    new IllegalStateException(
                            "Application is not scheduled"
                    ));

    // Day crossed
    if (LocalDate.now().isAfter(schedule.getScheduledDate())) {

        VerificationApplication application =
                applicationRepo
                        .findByApplicationNumber(applicationNumber)
                        .orElseThrow();

        application.setStatus(ApplicationStatus.REJECTED);
        assignment.setStatus(ApplicationStatus.REJECTED);
        assignmentRepo.save(assignment);
        applicationRepo.save(application);

        throw new IllegalStateException(
                "Verification date has expired. Application rejected."
        );
    }

    VerificationApplication application =
            applicationRepo
                    .findByApplicationNumber(applicationNumber)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Application not exist"
                            ));

    Instrument instrument =
            instrumentRepo
                    .findByInstrumentNumber(
                            application.getInstrumentNumber()
                    )
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Instrument not exist"
                            ));

    VerificationStandard standard =
            standardRepo
                    .findByInstrumentType(
                            instrument.getInstrumentType()
                    )
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Verification standard not configured"
                            ));

    List<VerificationReading> readings =
            new ArrayList<>();

    boolean allPassed = true;

    for (VerificationReadingDto readingDto :
            dto.getReadings()) {

        StandardTestPoint testPoint =
                standard.getTestPoints()
                        .stream()
                        .filter(point ->
                                point.getTestValue()
                                        .equals(readingDto.getTestValue()))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid test value: "
                                        + readingDto.getTestValue()
                                ));

        double error =
                readingDto.getActualValue()
                        - testPoint.getExpectedValue();

        boolean passed =
                Math.abs(error)
                        <= testPoint.getPermissibleError();

        if (!passed) {
            allPassed = false;
        }

        readings.add(
                VerificationReading.builder()
                        .testValue(testPoint.getTestValue())
                        .expectedValue(testPoint.getExpectedValue())
                        .actualValue(readingDto.getActualValue())
                        .error(error)
                        .permissibleError(
                                testPoint.getPermissibleError())
                        .passed(passed)
                        .build()
        );
    }

    ApplicationStatus result =
            allPassed
                    ? ApplicationStatus.APPROVED
                    : ApplicationStatus.REJECTED;

    Verification verification =
            Verification.builder()
                    .applicationNumber(applicationNumber)
                    .instrumentNumber(
                            application.getInstrumentNumber())
                    .lmoId(lmoId)
                    .readings(readings)
                    .result(result)
                    .createdAt(LocalDateTime.now())
                    .build();

    Verification saved =
            verificationRepo.save(verification);

    // application status update            
    application.setStatus(result);
    applicationRepo.save(application);

    // assignment status update
    assignment.setStatus(result);
    assignmentRepo.save(assignment);

    instrument.setStatus(
            allPassed
                    ? Status.UNDER_VERIFICATION
                    : Status.REJECTED
    );

    // instrument status update
    instrumentRepo.save(instrument);


    return saved;
}
  public InstrumentDocument verifyInstrumentDocument(String applicationNumber){
                return documentService.verifyInstrumentDocument(applicationNumber);
        }


        // give instrument
  public Instrument getInstrument(String instrumentNumber) {
            return instrumentRepo.findByInstrumentNumber(instrumentNumber)
            .orElseThrow(() -> new UsernameNotFoundException("Instrument not exist"));
        }
}