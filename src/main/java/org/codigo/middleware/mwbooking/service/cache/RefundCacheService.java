package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.Refund;
import org.codigo.middleware.mwbooking.repository.RefundRepo;
import org.springframework.stereotype.Service;

@Service
public class RefundCacheService {

    private final RefundRepo refundRepo;

    public RefundCacheService(RefundRepo refundRepo) {
        this.refundRepo = refundRepo;
    }

    public Refund save(Refund refund) {
        return refundRepo.save(refund);
    }

    public Refund findById(long packageId) {
        return refundRepo.findByRefundId(packageId);
    }
}
