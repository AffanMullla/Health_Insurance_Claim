package com.insurance.model;

public class Claim {
    private String claimId;
    private String policyId;
    private String treatmentType;
    private boolean isPreExisting; // 🆕 Added to identify waiting period claims

    // 🆕 Itemized Bill Breakdowns replacing single billAmount
    private double roomRentPerDay;
    private int daysAdmitted;
    private double nursingAndICUCharges;
    private double doctorConsultationFees;
    private double pharmacyAndMedication;

    private String status;
    private double approvedAmount;
    private String rejectionReason; // 🆕 Added to log specific fraud/rule denials

    // 🆕 Updated Constructor
    public Claim(String claimId, String policyId, String treatmentType, boolean isPreExisting,
                 double roomRent, int days, double nursing, double doctorFees, double pharmacy) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.treatmentType = treatmentType;
        this.isPreExisting = isPreExisting;
        this.roomRentPerDay = roomRent;
        this.daysAdmitted = days;
        this.nursingAndICUCharges = nursing;
        this.doctorConsultationFees = doctorFees;
        this.pharmacyAndMedication = pharmacy;
        this.status = "PENDING";
        this.approvedAmount = 0.0;
        this.rejectionReason = "";
    }

    // 🆕 Helper method to get raw total bill amount
    public double getTotalClaimedAmount() {
        return (roomRentPerDay * daysAdmitted) + nursingAndICUCharges + doctorConsultationFees + pharmacyAndMedication;
    }

    // Getters and Setters
    public String getClaimId() { return claimId; }
    public String getPolicyId() { return policyId; }
    public String getTreatmentType() { return treatmentType; }
    public boolean isPreExisting() { return isPreExisting; }
    public double getRoomRentPerDay() { return roomRentPerDay; }
    public int getDaysAdmitted() { return daysAdmitted; }
    public double getNursingAndICUCharges() { return nursingAndICUCharges; }
    public double getDoctorConsultationFees() { return doctorConsultationFees; }
    public double getPharmacyAndMedication() { return pharmacyAndMedication; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(double approvedAmount) { this.approvedAmount = approvedAmount; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String reason) { this.rejectionReason = reason; }
}
