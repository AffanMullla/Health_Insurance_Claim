package com.insurance.repository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LogRepository {
    private static final String FILE_NAME = "claims_history_log.txt";

    public void logSettlement(String claimId, String policyId, String status, double approvedAmount) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("Timestamp: " + LocalDateTime.now());
            pw.println("Claim ID: " + claimId);
            pw.println("Policy ID: " + policyId);
            pw.println("Status: " + status);
            pw.println("Approved Amount: ₹" + approvedAmount);
            pw.println("--------------------------------------------------");

        } catch (IOException e) {
            System.err.println("⚠️ Could not write history to file log: " + e.getMessage());
        }
    }
}

