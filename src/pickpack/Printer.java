/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author user
 */
public class Printer {
     
    String IMPRESORA = null;
    String FILE_NAME = null;
    public Printer(String impresora, String file_name){
        this.FILE_NAME = file_name;
        this.IMPRESORA = impresora;
    }
    
    public void sendPrint(){
        String impresora = "TSC TE200";
        impresora = "OneNote for Windows 10";
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);
        Boolean se_puede = false;
        for(int i=0;i<printServices.length;i++){
            System.out.println(printServices[i].getName());
            if(this.IMPRESORA.equals(printServices[i].getName())){
                se_puede = true;
            }
        }

        if(se_puede){
            System.out.println("Impresora encontrada");
            try {
                PDDocument document = PDDocument.load(new File("Guias/"+this.FILE_NAME));
                
                PrintService myPrintService = findPrintService(this.IMPRESORA);
                
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPageable(new PDFPageable(document));
                try {
                    job.setPrintService(myPrintService);
                } catch (PrinterException ex) {
                    System.out.println("Impresora no conectada");
                    Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    job.print();
                } catch (PrinterException ex) {
                    System.out.println("No se pudo imprimir");
                    Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                System.out.println("No se encontro el documento");
                Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Impresora NOT FOUND");
        }
    }
    
    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
}
