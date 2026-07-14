package com.hrm.service;

import com.hrm.model.entity.Application;
import com.hrm.model.entity.Interview;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecruitmentWorkflowRulesTest {

    @Test
    void schedulesOnlyApplicationsBeforeOfferStep() {
        assertTrue(RecruitmentWorkflowRules.canScheduleInterview(application("Applied", "Applied")));
        assertTrue(RecruitmentWorkflowRules.canScheduleInterview(application("Screening", "Screening")));
        assertTrue(RecruitmentWorkflowRules.canScheduleInterview(application("Interview", "Interview")));
        assertFalse(RecruitmentWorkflowRules.canScheduleInterview(application("Interview", "Offer")));
        assertFalse(RecruitmentWorkflowRules.canScheduleInterview(application("Rejected", "Rejected")));
    }

    @Test
    void preparesOfferOnlyAfterInterviewPass() {
        assertTrue(RecruitmentWorkflowRules.canPrepareOffer(application("Interview", "Offer")));
        assertFalse(RecruitmentWorkflowRules.canPrepareOffer(application("Interview", "Interview")));
        assertFalse(RecruitmentWorkflowRules.canPrepareOffer(application("Offered", "Offer")));
    }

    @Test
    void recognizesSentOffer() {
        assertTrue(RecruitmentWorkflowRules.isOfferSent(application("Offered", "Offer")));
        assertFalse(RecruitmentWorkflowRules.isOfferSent(application("Interview", "Offer")));
    }

    @Test
    void recordsResultOnlyForDuePendingSchedule() {
        LocalDateTime now = LocalDateTime.now();
        Interview interview = interview("Scheduled", "Pending", now.minusMinutes(1));
        assertTrue(RecruitmentWorkflowRules.canRecordResult(interview, now));

        interview.setScheduledAt(now.plusMinutes(1));
        assertFalse(RecruitmentWorkflowRules.canRecordResult(interview, now));

        interview.setScheduledAt(now.minusMinutes(1));
        interview.setStatus("Completed");
        interview.setResult("Passed");
        assertFalse(RecruitmentWorkflowRules.canRecordResult(interview, now));
    }

    private Application application(String status, String currentStep) {
        Application application = new Application();
        application.setStatus(status);
        application.setCurrentStep(currentStep);
        return application;
    }

    private Interview interview(String status, String result, LocalDateTime scheduledAt) {
        Interview interview = new Interview();
        interview.setStatus(status);
        interview.setResult(result);
        interview.setScheduledAt(scheduledAt);
        return interview;
    }
}
