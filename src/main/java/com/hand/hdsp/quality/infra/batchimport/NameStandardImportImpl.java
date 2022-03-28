package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.app.service.StandardGroupService;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import javax.naming.Name;

/**
 * <p>
 * description
 * </p>
 *
 * @author 张鹏 2020/12/7 19:38
 * @since 1.0.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD)
public class NameStandardImportImpl implements IDoImportService {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;
    private final StandardGroupRepository standardGroupRepository;
    private final NameStandardMapper nameStandardMapper;

    public NameStandardImportImpl(ObjectMapper objectMapper,
                                  NameStandardRepository nameStandardRepository,
                                  StandardGroupRepository standardGroupRepository,
                                  NameStandardMapper nameStandardMapper) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
        this.standardGroupRepository = standardGroupRepository;
        this.nameStandardMapper = nameStandardMapper;
    }

    @Override
    public Boolean doImport(String data) {
        NameStandardDTO nameStandardDTO;
        try{
            nameStandardDTO=objectMapper.readValue(data, NameStandardDTO.class);
        }catch (IOException e) {
            log.error("data:{}", data);
            log.error("Read Json Error", e);
            // 失败
            return false;
        }
        //设置租户id
        nameStandardDTO.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        //导入时根据目录名 校验目标环境是否有对应目录，若无对应目录则创建对应目录
        String groupName = nameStandardDTO.getGroupName();
        List<StandardGroup> standardGroups = standardGroupRepository.selectByCondition(
                Condition.builder(StandardGroup.class).
                        andWhere(Sqls.custom()
                                .andEqualTo(StandardGroup.FIELD_GROUP_NAME, nameStandardDTO.getGroupName())
                        ).build());
        if(CollectionUtils.isEmpty(standardGroups)){
            //创建新目录
            int newGroupId = standardGroupRepository.insertDTOSelective(StandardGroupDTO.builder()
                    .groupCode(nameStandardDTO.getGroupCode())
                    .groupName(nameStandardDTO.getGroupName())
                    .standardType(nameStandardDTO.getStandardType())
                    .build());
            //设置新目录id
            nameStandardDTO.setGroupId((long)newGroupId);
        }

        //根据责任人姓名 获取目标环境的责任人id
        List<NameStandardDTO> detailUser = nameStandardMapper.detailUser(nameStandardDTO);
        if(CollectionUtils.isNotEmpty(detailUser)){
            nameStandardDTO.setStandardId(detailUser.get(0).getStandardId());
        }
        //根据责任人部门名 获取目标环境责任人部门id
        List<NameStandardDTO> detailUnit = nameStandardMapper.detailUnit(nameStandardDTO);
        if(CollectionUtils.isNotEmpty(detailUnit)){
            nameStandardDTO.setChargeDeptId(detailUnit.get(0).getChargeDeptId());
        }
        nameStandardRepository.importStandard(nameStandardDTO);
        return true;
    }
}
