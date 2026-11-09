package com.insurance.engine;

import com.insurance.model.Claim;
import com.insurance.model.Policy;

public class SettlementEngine {

    public String verifyAndSettle(Policy policy, Claim claim, boolean isNetworkHospital) {
        // RULE 1: Permanent Exclusion Check
        for (String exclusion : policy.getExcludedIllnesses()) {
            if (exclusion.equalsIgnoreCase(claim.getTreatmentType())) {
                claim.setStatus("REJECTED");
                claim.setRejectionReason("Treatment falls under Permanent Exclusions.");
                return "❌ Claim Rejected: Treatment is explicitly excluded under policy terms.";
            }
        }

        // RULE 2: Pre-Existing Disease (PED) Waiting Period Validation (Industry Standard: 3-Year limit)
        if (claim.isPreExisting() && policy.getPolicyAgeYears() < 3) {
            claim.setStatus("REJECTED");
            claim.setRejectionReason("Pre-Existing waiting period violation.");
            return "❌ Claim Rejected: Pre-Existing conditions carry a mandatory 3-Year waiting period. Policy age: " + policy.getPolicyAgeYears() + " yr(s).";
        }

        // RULE 3: Overall Limit Check
        if (policy.getBalanceSumInsured() <= 0) {
            claim.setStatus("REJECTED");
            claim.setRejectionReason("Policy balance fully exhausted.");
            return "❌ Claim Rejected: Remaining policy sum insured balance is 0.";
        }

        // RULE 4: Room Rent Capping & Proportionate Deductions
        // Industry Standard: Allowed Room Rent per day = 1% of Base Sum Insured
        double allowedRoomRentPerDay = policy.getBaseSumInsured() * 0.01;
        double actualRoomRentPerDay = claim.getRoomRentPerDay();

        double finalAdmissibleRoomRent = Math.min(allowedRoomRentPerDay, actualRoomRentPerDay) * claim.getDaysAdmitted();
        double finalAdmissibleNursing = claim.getNursingAndICUCharges();
        double finalAdmissibleDoctorFees = claim.getDoctorConsultationFees();

        // Pro-rata Penalty: If they chose a room higher than their eligibility tier, penalize doctor & nursing charges proportionally
        String penaltyNotes = "Normal Room Used (No penalty)";
        if (actualRoomRentPerDay > allowedRoomRentPerDay) {
            double penaltyRatio = allowedRoomRentPerDay / actualRoomRentPerDay;
            finalAdmissibleNursing *= penaltyRatio;
            finalAdmissibleDoctorFees *= penaltyRatio;
            penaltyNotes = String.format("Proproportional Penalty Applied (Allowed Room Rent: ₹%.2f/day vs Used: ₹%.2f/day)", allowedRoomRentPerDay, actualRoomRentPerDay);
        }

        // Non-network penalty (Optional additional rule from your original code context)
        if (!isNetworkHospital) {
            finalAdmissibleDoctorFees *= 0.85; // 15% out-of-pocket penalty adjustment
            penaltyNotes += " | Non-Network Hospital 15% deduction on consultancy fee";
        }

        // Pharmacy bills remain unaffected by room rent tier penalties
        double finalAdmissiblePharmacy = claim.getPharmacyAndMedication();

        // Accumulate intermediate admissible total
        double totalAdmissibleBeforeCaps = finalAdmissibleRoomRent + finalAdmissibleNursing + finalAdmissibleDoctorFees + finalAdmissiblePharmacy;

        // RULE 5: Specific Disease Sub-limits Cap Evaluation
        double diseaseSubLimit = policy.getSubLimit(claim.getTreatmentType());
        double netAdmissibleAmount = Math.min(totalAdmissibleBeforeCaps, diseaseSubLimit);

        // RULE 6: Co-Payment Clause Deduction
        double coPayDeduction = netAdmissibleAmount * policy.getCoPayPercentage();
        double absoluteInsurerLiability = netAdmissibleAmount - coPayDeduction;

        // RULE 7: Final Pool Available Limit Check
        double finalPayout = Math.min(absoluteInsurerLiability, policy.getBalanceSumInsured());

        // State changes
        policy.deductBalance(finalPayout);
        claim.setStatus("APPROVED");
        claim.setApprovedAmount(finalPayout);

        return String.format(
                "✅ Claim Settled Successfully!\n" +
                        "📋 Assessment Notes: %s\n" +
                        "💵 Original Bill: ₹%.2f\n" +
                        "🏛️ Insurance Coverage Payout: ₹%.2f\n" +
                        "👤 Patient Share (Co-pay/Penalties): ₹%.2f\n" +
                        "📉 Remaining Policy Pool Balance: ₹%.2f",
                penaltyNotes, claim.getTotalClaimedAmount(), finalPayout, (claim.getTotalClaimedAmount() - finalPayout), policy.getBalanceSumInsured()
        );
    }
}


