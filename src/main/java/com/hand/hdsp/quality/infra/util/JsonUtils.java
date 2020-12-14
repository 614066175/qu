package com.hand.hdsp.quality.infra.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.RelationshipDTO;
import com.hand.hdsp.quality.api.dto.TableRelCheckDTO;
import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.ResponseUtils;

/**
 * <p>对象、json转换工具类</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-05 16:38:42
 */
public class JsonUtils {
    private JsonUtils() {
    }

    /**
     * 对象转json字符串
     *
     * @param obj
     * @return
     */
    public static String object2Json(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return ResponseUtils.getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.JSON_PROCESS);
        }
    }

    /**
     * json 转告警等级list
     *
     * @param content
     * @return
     */
    public static List<WarningLevelDTO> json2WarningLevel(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        try {
            return ResponseUtils.getObjectMapper().readValue(content, new TypeReference<List<WarningLevelDTO>>() {
            });
        } catch (IOException e) {
            throw new CommonException(ErrorCode.JSON_PROCESS);
        }
    }

    /**
     * json 转告警等级VO
     *
     * @param content
     * @return
     */
    public static List<WarningLevelVO> json2WarningLevelVO(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        try {
            return ResponseUtils.getObjectMapper().readValue(content, new TypeReference<List<WarningLevelVO>>() {
            });
        } catch (IOException e) {
            throw new CommonException(ErrorCode.JSON_PROCESS);
        }
    }

    /**
     * json 转关联关系list
     *
     * @param content
     * @return
     */
    public static List<RelationshipDTO> json2Relationship(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        try {
            return ResponseUtils.getObjectMapper().readValue(content, new TypeReference<List<RelationshipDTO>>() {
            });
        } catch (IOException e) {
            throw new CommonException(ErrorCode.JSON_PROCESS);
        }
    }

    /**
     * json 转表间校验关系list
     *
     * @param content
     * @return
     */
    public static List<TableRelCheckDTO> json2TableRelCheck(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        try {
            return ResponseUtils.getObjectMapper().readValue(content, new TypeReference<List<TableRelCheckDTO>>() {
            });
        } catch (IOException e) {
            throw new CommonException(ErrorCode.JSON_PROCESS);
        }
    }


}
