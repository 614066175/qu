package com.hand.hdsp.quality.infra.batchimport.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.ROOT_STANDARD;

import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.starter.driver.core.infra.util.JsonUtil;

/**
 * 词根分组导入
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2023/02/07 11:24
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_ROOT,sheetIndex = 0)
public class RootGroupImportImpl extends AbstractCommonGroupImportImpl {
    private ObjectMapper objectMapper;

    public RootGroupImportImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        //循环添加类型
        List<String> commonGroups = new ArrayList<>();
        for (String json : data) {
            try {
                CommonGroup commonGroup = objectMapper.readValue(json, CommonGroup.class);
                commonGroup.setGroupType(ROOT_STANDARD);
                commonGroups.add(JsonUtil.toJson(commonGroup));
            } catch (IOException e) {
                log.error("导入分组失败");
                return false;
            }
        }
        return super.doImport(commonGroups);
    }
}
