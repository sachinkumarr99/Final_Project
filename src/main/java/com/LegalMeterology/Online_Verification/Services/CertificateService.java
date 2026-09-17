package com.LegalMeterology.Online_Verification.Services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.LegalMeterology.Online_Verification.Entities.Assignment;
import com.LegalMeterology.Online_Verification.Entities.Certificate;
import com.LegalMeterology.Online_Verification.Entities.Instrument;
import com.LegalMeterology.Online_Verification.Entities.User;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.Verification;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationEvidence;
import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;
import com.LegalMeterology.Online_Verification.Exceptions.VerificationException;
import com.LegalMeterology.Online_Verification.Repositories.AssignmentRepo;
import com.LegalMeterology.Online_Verification.Repositories.CertificateRepo;
import com.LegalMeterology.Online_Verification.Repositories.UserRepo;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo.VerificationApplicationRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationEvidenceRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationRepo;
import com.LegalMeterology.Online_Verification.Security.SecurityUtils;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateService {
    
    private final QRCodeService qrCodeService;
    private final TemplateEngine templateEngine;
    private final InstrumentRepo instrumentRepo;
    private final VerificationApplicationRepo verificationApplicationRepo;
    private final SequenceService sequenceService;
    private final VerificationRepo verificationRepo;
    private final VerificationEvidenceRepo verificationEvidenceRepo;
    private final UserRepo userRepo;
    private final CertificateRepo certificateRepo;
    private final Cloudinary cloudinary;
    private final SecurityUtils securityUtils;
    private final AssignmentRepo assignmentRepo;

    private String generateCertificateNumber(){
        Long sequence = sequenceService.getNextSequence("certificate");
        return String.format("CERT-%d-%09d",LocalDateTime.now().getYear(),sequence);
    }

    private Certificate createCertificate(String applicationNumber){

        // check appllication exist
        VerificationApplication verificationApplication=verificationApplicationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()-> new UsernameNotFoundException("Application Not found"));
        // check instrument exist
        Instrument instrument=instrumentRepo.findByInstrumentNumber(verificationApplication.getInstrumentNumber()).orElseThrow(()->new UsernameNotFoundException("Instrument Not found"));
        // verification object done by offical authority officer
        Verification verification=verificationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()-> new UsernameNotFoundException("Instrument not verified by officer"));

        //  check evidence verified or not
        List<VerificationEvidence> verificationEvidences= verificationEvidenceRepo.findAllByApplicationNumber(applicationNumber);
        for(VerificationEvidence evidence : verificationEvidences){
                if(!evidence.getIsVerified()){
                        throw new VerificationException("Evidence Not Verified");
                }
        }
        if(verification.getResult()!=ApplicationStatus.APPROVED){
                throw new RuntimeException("Application Not Approved");
        }

        // certificate is already exist
        Certificate existedCertificate=certificateRepo.findByApplicationNumber(applicationNumber).orElse(null);
        if(existedCertificate!=null && existedCertificate.getValidUpto().isAfter(LocalDateTime.now()) && existedCertificate.getStatus()==ApplicationStatus.APPROVED ){
                throw new RuntimeException("Certificate Already Verified ");
        }
        
        
        return Certificate.builder()
        .certificateNumber(generateCertificateNumber())
        .instrumentNumber(verificationApplication.getInstrumentNumber())
        .applicationNumber(applicationNumber)
        .instrumentType(instrument.getInstrumentType())
        .manufacturer(instrument.getManufacturer())
        .model(instrument.getModel())
        .capacity(instrument.getCapacity())
        .capacityUnit(instrument.getUnit())
        .accuracyClass(instrument.getAccuracyClass().name())
        .ownerId(instrument.getOwnerId())

        .installationLocation(instrument.getInstallationLocation())
        .verificationDate(verification.getCreatedAt())
        .validUpto(verification.getCreatedAt().plusDays(2))
        .verifiedById(securityUtils.getCurrentUserId())
        .status(ApplicationStatus.APPROVED)
        .formatType("PDF")
        .build();

        

    }

    // jinka approved application ka abhhi certificate nahi bana vo dega

    public List<VerificationApplication> getCertificatePendingApplications() {

    return verificationApplicationRepo.findAll()
            .stream()
            .filter(application ->
                    application.getStatus() == ApplicationStatus.APPROVED)
            .filter(application ->
                    certificateRepo
                            .findByApplicationNumber(application.getApplicationNumber())
                            .isEmpty())
            .toList();
}

    public Certificate generateCertificate(String applicationNumber) throws Exception{

        Certificate data= createCertificate(applicationNumber);

        // it is contained by qr to verify certificate
        data.setVerificationToken(UUID.randomUUID().toString());
        String verificationUrl="http://localhost:5173/certificates/verify/"+ data.getVerificationToken();
        User applicant=userRepo.findById(new ObjectId(data.getOwnerId())).orElseThrow(()->new UsernameNotFoundException("Applicant not exist"));


        byte[] qrBytes=qrCodeService.generateQrCode(verificationUrl, 200, 200);

        String qrBase64=Base64.getEncoder().encodeToString(qrBytes);

        Context context=new Context();

         context.setVariable(
                "certificateNumber",
                data.getCertificateNumber()
        );

        context.setVariable(
                "instrumentNumber",
                data.getInstrumentNumber()
        );

        context.setVariable(
                "instrumentType",
                data.getInstrumentType()
        );

        context.setVariable(
                "manufacturer",
                data.getManufacturer()
        );

        context.setVariable(
                "model",
                data.getModel()
        );

        context.setVariable(
                "capacity",
                data.getCapacity()
        );

        context.setVariable(
                "accuracyClass",
                data.getAccuracyClass()
        );

        context.setVariable(
                "owner",
                applicant.getFirstName()+" "+applicant.getLastName()
        );

        context.setVariable(
                "installationLocation",
                data.getInstallationLocation()
        );

        context.setVariable(
                "verificationDate",
                data.getVerificationDate().toLocalDate()
        );

        context.setVariable(
                "validUpto",
                data.getValidUpto().toLocalDate()
        );

        /*
         * QR Code HTML ko dena
         */
        context.setVariable(
                "qrCode",
                qrBase64
        );


         boolean verified =
                ApplicationStatus.APPROVED==data.getStatus();

        context.setVariable(
                "verified",
                verified
        );

        ClassPathResource resource =
        new ClassPathResource("templates/signature.png");

        byte[] bytes = resource.getInputStream().readAllBytes();

        String signatureBase64 =Base64.getEncoder().encodeToString(bytes);
        context.setVariable("signature",signatureBase64);



        String finalHtml=templateEngine.process("Certificate", context);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        PdfRendererBuilder pdfRendererBuilder=new PdfRendererBuilder();

        

        pdfRendererBuilder
                        .useFastMode()
                        .withHtmlContent(finalHtml,null)
                        .toStream(outputStream)
                        .run();

        //  Now steps for uploading on cloudinary

        String folderPath = String.format("legal_meterology/users/usr_%s/certificate",data.getOwnerId());
        String publicId="certificate_"+System.currentTimeMillis()+"_"+UUID.randomUUID().toString().substring(0,8)+".pdf";
        // step 1 : create parameters that needs for cloudinary to make a foder structure
        Map uploadParams=ObjectUtils.asMap(
         "folder", folderPath,
                    "public_id", publicId,
                    "resource_type", "raw",
                    "format","pdf",
                    "overwrite", false
        );

        Map response=cloudinary.uploader().upload(outputStream.toByteArray(), uploadParams);

        // set certificate details
        Object formatObject = response.get("format");
Object publicIdObject = response.get("public_id");
Object secureUrlObject = response.get("secure_url");

if (publicIdObject == null) {
    throw new RuntimeException(
            "Certificate upload failed: Cloudinary public_id missing. Response: "
                    + response
    );
}

if (secureUrlObject == null) {
    throw new RuntimeException(
            "Certificate upload failed: Cloudinary secure_url missing. Response: "
                    + response
    );
}

String format = formatObject != null
        ? formatObject.toString()
        : "pdf";

String secureUrl = secureUrlObject.toString();
String actualPublicId=publicIdObject.toString();

data.setFormatType(format);
data.setPublicId(actualPublicId);
data.setCertificateUrl(secureUrl);
data.setResourceType("raw");

        data=certificateRepo.save(data);

        VerificationApplication verificationApplication=verificationApplicationRepo.findByApplicationNumber(data.getApplicationNumber()).orElseThrow(()->new UsernameNotFoundException("Application Not found"));
        verificationApplication.setStatus(ApplicationStatus.APPROVED);
        Assignment assignment= assignmentRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new ResourceNotFoundException("Application not assigned"));
        assignment.setStatus(ApplicationStatus.APPROVED);

        assignmentRepo.save(assignment);
        
        verificationApplicationRepo.save(verificationApplication);
            
        return data;

    }


    public Certificate verifyCertificate(String verificationToken){


        Certificate certificate =certificateRepo.findByVerificationToken(verificationToken).orElseThrow(()-> new UsernameNotFoundException("Certificate Not Exist"));
        if(certificate.getStatus()!=ApplicationStatus.APPROVED) return certificate;

        // check expiration
        if(certificate.getValidUpto().isBefore(LocalDateTime.now())){
                certificate.setStatus(ApplicationStatus.EXPIRED);
                certificate=certificateRepo.save(certificate);
        }

        return certificate;
    }

    
    
}
