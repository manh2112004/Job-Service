package org.Job.client;

import lombok.extern.slf4j.Slf4j;
import org.Job.client.dto.CompanyMemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class CompanyClient {

    @Autowired
    private RestTemplate restTemplate;

    public CompanyMemberResponse getCompanyMember(String companyId, String userId) {
        String url = "http://company-service/internal/companies/" + companyId + "/users/" + userId;
        try {
            return restTemplate.getForObject(url, CompanyMemberResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Company member validation failed. Company or member not found: companyId={}, userId={}", companyId, userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Công ty không tồn tại hoặc bạn không có quyền đăng tuyển cho công ty này");
        } catch (Exception e) {
            log.error("Error calling Company Service for member validation: companyId={}, userId={}", companyId, userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xác thực thông tin công ty từ Company Service");
        }
    }

    public org.Job.client.dto.CompanyResponse getCompany(String companyId) {
        String url = "http://company-service/internal/companies/" + companyId;
        try {
            return restTemplate.getForObject(url, org.Job.client.dto.CompanyResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Company not found: companyId={}", companyId);
            return null;
        } catch (Exception e) {
            log.error("Error calling Company Service to fetch company info: companyId={}", companyId, e);
            return null;
        }
    }
}
