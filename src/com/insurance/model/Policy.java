package com.insurance.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Policy {
    private String policyId;
    private String holderName;
    private double baseSumInsured; // 🆕 Added to track original limit for 1% room rent cap
    private double balanceSumInsured;
    private double coPayPercentage;
    private int policyAgeYears; // 🆕 Added to check waiting periods
    private List<String> excludedIllnesses;
    private Map<String, Double> treatmentSubLimits; // 🆕 Added for disease-specific caps

    public Policy(String policyId, String holderName, double sumInsured, double coPayPercentage, int policyAgeYears, List<String> exclusions) {
        this.policyId = policyId;
        this.holderName = holderName;
        this.baseSumInsured = sumInsured;
        this.balanceSumInsured = sumInsured;
        this.coPayPercentage = coPayPercentage;
        this.policyAgeYears = policyAgeYears;
        this.excludedIllnesses = exclusions;

        // 🆕 Seed default real-world disease caps
        this.treatmentSubLimits = new HashMap<>();
        this.treatmentSubLimits.put("CATARACT", 40000.0);
        this.treatmentSubLimits.put("HERNIA", 60000.0);
    }

    // Getters and Setters
    public String getPolicyId() { return policyId; }
    public String getHolderName() { return holderName; }
    public double getBaseSumInsured() { return baseSumInsured; } // 🆕 New Getter
    public double getBalanceSumInsured() { return balanceSumInsured; }
    public double getCoPayPercentage() { return coPayPercentage; }
    public int getPolicyAgeYears() { return policyAgeYears; } // 🆕 New Getter
    public List<String> getExcludedIllnesses() { return excludedIllnesses; }

    // 🆕 Helper method to check disease limits dynamically
    public double getSubLimit(String treatment) {
        return treatmentSubLimits.getOrDefault(treatment.toUpperCase(), Double.MAX_VALUE);
    }

    public void deductBalance(double amount) {
        this.balanceSumInsured -= amount;
    }
}


