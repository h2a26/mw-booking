package org.codigo.middleware.mwbooking.service;

import org.codigo.middleware.mwbooking.api.input.class_.*;
import org.codigo.middleware.mwbooking.api.output.class_.*;

import java.util.List;

public interface ClassService {
    ClassRegisterResponse registerClass(ClassRegisterRequest classRegisterRequest);
    List<ClassResponse> getAvailableClassesByCountry(String country);
    void refundWaitlistUserCreditsWhenClassEnd();
    void remindClassStartTimeToUser();
}
