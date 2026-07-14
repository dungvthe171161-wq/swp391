package com.hrm.service;

import com.hrm.model.entity.Application;
import com.hrm.model.entity.Interview;
import java.time.LocalDateTime;

public final class RecruitmentWorkflowRules {

    private RecruitmentWorkflowRules() {
    }

    public static boolean canScheduleInterview(Application application) {
        if (application == null) {
            return false;
        }
        return "Applied".equals(application.getStatus())
                || "Screening".equals(application.getStatus())
                || ("Interview".equals(application.getStatus())
                && "Interview".equals(application.getCurrentStep()));
    }

    public static boolean canPrepareOffer(Application application) {
        return application != null
                && "Interview".equals(application.getStatus())
                && "Offer".equals(application.getCurrentStep());
    }

    public static boolean isOfferSent(Application application) {
        return application != null
                && "Offered".equals(application.getStatus())
                && "Offer".equals(application.getCurrentStep());
    }

    public static boolean canModifySchedule(Interview interview) {
        if (interview == null || !"Pending".equals(interview.getResult())) {
            return false;
        }
        return "Scheduled".equals(interview.getStatus())
                || "Rescheduled".equals(interview.getStatus());
    }

    public static boolean canRecordResult(Interview interview, LocalDateTime now) {
        return canModifySchedule(interview)
                && now != null
                && interview.getScheduledAt() != null
                && !interview.getScheduledAt().isAfter(now);
    }
}
