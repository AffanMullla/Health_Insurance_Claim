package com.insurance;

import com.insurance.engine.SettlementEngine;
import com.insurance.model.Claim;
import com.insurance.model.Policy;
import com.insurance.repository.LogRepository;

import java.util.*;

public class MainApplication {
    private static Map<String, Policy> policyDatabase = new HashMap<>();
    private static int claimIdCounter = 1001;

    public static void main(String[] args) {
        seedData(); // Populate baseline mock data
        SettlementEngine engine = new SettlementEngine();
        LogRepository logger = new LogRepository();
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("🏥 HEALTH INSURANCE CLAIM VERIFICATION ENGINE 🏥");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Submit & Verify New Claim");
            System.out.println("2. Check Remaining Policy Balance");
            System.out.println("3. Exit System");
            System.out.print("Select an option (1-3): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("\nEnter Policy ID (e.g., POL100, POL200): ");
                    String pId = scanner.nextLine().trim().toUpperCase();

                    if (!policyDatabase.containsKey(pId)) {
                        System.out.println("❌ Error: Policy record not found in system storage database.");
                        break;
                    }

                    Policy activePolicy = policyDatabase.get(pId);
                    System.out.println("👤 Policy Holder Found: " + activePolicy.getHolderName());

                    System.out.print("Enter Medical Treatment / Illness Type: ");
                    String treatment = scanner.nextLine().trim();

                    System.out.print("Is this a Pre-Existing Disease? (yes/no): ");
                    boolean isPreExisting = scanner.nextLine().trim().equalsIgnoreCase("yes");

                    // 🆕 Collect itemized breakdown inputs
                    try {
                        System.out.print("Enter Room Rent Per Day (₹): ");
                        double roomRent = Double.parseDouble(scanner.nextLine());

                        System.out.print("Enter Total Days Admitted: ");
                        int days = Integer.parseInt(scanner.nextLine());

                        System.out.print("Enter ICU / Nursing Charges (₹): ");
                        double nursing = Double.parseDouble(scanner.nextLine());

                        System.out.print("Enter Doctor Consultation / Surgeon Fees (₹): ");
                        double doctorFees = Double.parseDouble(scanner.nextLine());

                        System.out.print("Enter Pharmacy / Medication Cost (₹): ");
                        double pharmacy = Double.parseDouble(scanner.nextLine());

                        System.out.print("Is this a Network Hospital? (yes/no): ");
                        boolean isNetwork = scanner.nextLine().trim().equalsIgnoreCase("yes");

                        // Process upgraded claim object
                        String generatedClaimId = "CLM" + (claimIdCounter++);
                        Claim newClaim = new Claim(generatedClaimId, pId, treatment, isPreExisting, roomRent, days, nursing, doctorFees, pharmacy);

                        System.out.println("\n--- Processing Real-Life Claims Engine Rules ---");
                        String evaluationResult = engine.verifyAndSettle(activePolicy, newClaim, isNetwork);
                        System.out.println(evaluationResult);

                        // Record history logs
                        logger.logSettlement(newClaim.getClaimId(), pId, newClaim.getStatus(), newClaim.getApprovedAmount());

                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid numerical formatting entered. Claim dropped.");
                    }
                    break;


                case "2":
                    System.out.print("\nEnter Policy ID: ");
                    String checkId = scanner.nextLine().trim().toUpperCase();
                    if (policyDatabase.containsKey(checkId)) {
                        Policy p = policyDatabase.get(checkId);
                        System.out.printf("👤 Holder: %s | Remaining Pool Limit Balance: ₹%.2f\n", p.getHolderName(), p.getBalanceSumInsured());
                    } else {
                        System.out.println("❌ Policy ID missing from active records.");
                    }
                    break;

                case "3":
                    System.out.println("Shutting down the engine. Goodbye!");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("❌ Invalid option selection entry choice. Please enter numbers 1-3.");
            }
        }
    }

    private static void seedData() {
        // Parameters: PolicyId, HolderName, SumInsured, CoPay %, PolicyAgeInYears, Exclusions List
        policyDatabase.put("POL100", new Policy("POL100", "Rahul Sharma", 400000.0, 0.10, 1, Arrays.asList("Cosmetic", "Dental")));
        policyDatabase.put("POL200", new Policy("POL200", "Priya Patel", 500000.0, 0.0, 4, Arrays.asList("Cosmetic")));
    }

}

