package org.codigo.middleware.mwbooking.api.input.waitlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WaitlistEntry {
    private long classId;
    private String email;
}
