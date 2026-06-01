package org.Job.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMemberResponse {
    private String id;
    private String companyId;
    private String userId;
    private String role;
    private Boolean active;
}
