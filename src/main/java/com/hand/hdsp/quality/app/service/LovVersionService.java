package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * <p>LOV表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovVersionService {


    /**
     * 获取lovId下的版本列表
     * @param lovId
     * @return
     */
    List<CodeVersion> getVersion(Long lovId) ;


    /**
     * 获取最大版本号列表
     * @param tenantId
     * @return
     */
    List<LovVersionDTO> getMaxList(Long tenantId);
}
