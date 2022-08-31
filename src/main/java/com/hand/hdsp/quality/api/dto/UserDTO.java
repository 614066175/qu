package com.hand.hdsp.quality.api.dto;

import lombok.Data;

import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * 用户信息
 * @author 罗付强
 */
@Data
public class UserDTO {
    private Long id;
    private String loginName;
    private String realName;
    private String email;
    private String phone;

    public String getEmail() {
        if (DataSecurityHelper.isTenantOpen()) {
            return DataSecurityHelper.decrypt(email);
        }
        return email;
    }

    public String getPhone() {
        if (DataSecurityHelper.isTenantOpen()) {
            return DataSecurityHelper.decrypt(phone);
        }
        return phone;
    }
}
