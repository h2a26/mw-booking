package org.codigo.middleware.mwbooking.service.impl;

import org.codigo.middleware.mwbooking.commons.enum_.PaymentStatus;
import org.codigo.middleware.mwbooking.entity.Package;
import org.codigo.middleware.mwbooking.entity.Payment;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.repository.PaymentRepo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class MockPaymentService {

    private final PaymentRepo paymentRepo;

    public MockPaymentService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    public void paymentCharge(Package pack, User user) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(pack.getPrice());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentTime(ZonedDateTime.now());
        paymentRepo.save(payment);
    }

}
