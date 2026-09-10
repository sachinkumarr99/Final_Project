package com.LegalMeterology.Online_Verification.Services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRCodeService {
    

    public byte[] generateQrCode(String url,int height,int width) throws IOException ,WriterException{

        QRCodeWriter qrCodeWriter=new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream= new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix,"PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }


}
