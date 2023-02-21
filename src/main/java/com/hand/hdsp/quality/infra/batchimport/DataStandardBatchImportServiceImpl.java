package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.CommonGroupClient;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.converter.DataStandardConverter;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchImportHandler;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.profile.ProfileClient;
import org.hzero.mybatis.helper.DataSecurityHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.DATA_STANDARD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/04 15:26
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD, sheetIndex = 1)
public class DataStandardBatchImportServiceImpl extends BatchImportHandler implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;
    private final CommonGroupRepository commonGroupRepository;
    private final DataStandardMapper dataStandardMapper;
    private final DataStandardConverter dataStandardConverter;
    private final ProfileClient profileClient;
    private final CommonGroupClient commonGroupClient;


    public DataStandardBatchImportServiceImpl(ObjectMapper objectMapper, DataStandardRepository dataStandardRepository,
                                              CommonGroupRepository commonGroupRepository,
                                              DataStandardMapper dataStandardMapper, DataStandardConverter dataStandardConverter, ProfileClient profileClient, CommonGroupClient commonGroupClient) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.commonGroupRepository = commonGroupRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.dataStandardConverter = dataStandardConverter;
        this.profileClient = profileClient;
        this.commonGroupClient = commonGroupClient;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<DataStandard> addList = new ArrayList<>();
        List<DataStandard> updateList = new ArrayList<>();
        try {
            for (int i = 0; i < data.size(); i++) {
                String json = data.get(i);
                DataStandardDTO dataStandardDTO = objectMapper.readValue(json, DataStandardDTO.class);
                CommonGroup commonGroup = commonGroupRepository.selectOne(CommonGroup.builder()
                        .groupType(DATA_STANDARD)
                        .groupPath(dataStandardDTO.getGroupPath())
                        .tenantId(tenantId).projectId(projectId).build());
                if (commonGroup == null) {
//                    addErrorMsg(i, "分组不存在，请先维护分组!");
//                    getContextList().get(i).setDataStatus(IMPORT_FAILED);
//                    continue;
                    //不存在直接新建
                    commonGroupClient.createGroup(tenantId, projectId, DATA_STANDARD, dataStandardDTO.getGroupPath());
                    CommonGroup group = commonGroupRepository.selectOne(CommonGroup.builder()
                            .groupType(DATA_STANDARD)
                            .groupPath(dataStandardDTO.getGroupPath())
                            .tenantId(tenantId).projectId(projectId).build());
                    dataStandardDTO.setGroupId(group.getGroupId());
                } else {
                    dataStandardDTO.setGroupId(commonGroup.getGroupId());
                }
                //标准编码存在
                //若编码已存在，且状态为新建/离线，则采用更新逻辑。
                //若编码已存在，且流程不需要下线审批，状态为在线/下线审批中，则下线原内容后更新。
                //若编码已存在，且流程需要下线审批，状态为在线/下线审批中，则报错。
                //若编码已存在，状态为发布审核中，则撤回之前的流程后更新。
                //导入更新后直接进入审核/在线状态
                dataStandardDTO.setTenantId(tenantId);
                dataStandardDTO.setProjectId(projectId);
                dataStandardDTO.setStandardStatus(CREATE);
                //设置责任人
                if (DataSecurityHelper.isTenantOpen()) {
                    //加密后查询
                    String chargeName = DataSecurityHelper.encrypt(dataStandardDTO.getChargeName());
                    dataStandardDTO.setChargeName(chargeName);
                }
                Long chargeId = dataStandardMapper.checkCharger(dataStandardDTO.getChargeName(), dataStandardDTO.getTenantId());
                dataStandardDTO.setChargeId(chargeId);
                DataStandard exist = dataStandardRepository.selectOne(DataStandard.builder()
                        .standardCode(dataStandardDTO.getStandardCode())
                        .tenantId(dataStandardDTO.getTenantId())
                        .projectId(dataStandardDTO.getProjectId())
                        .build());
                if (exist != null) {
                    dataStandardDTO.setStandardId(exist.getStandardId());
                    dataStandardDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    dataStandardDTO.setReleaseBy(exist.getReleaseBy());
                    dataStandardDTO.setReleaseDate(exist.getReleaseDate());
                    //存在默认使用本身状态
                    dataStandardDTO.setStandardStatus(exist.getStandardStatus());
                    //如果是在线或下线中状态，判断是否需要下线审核，需要则报错
                    if (ONLINE.equals(exist.getStandardStatus()) || OFFLINE_APPROVING.equals(exist.getStandardStatus())) {
                        String offlineOpen = profileClient.getProfileValueByOptions(DetailsHelper.getUserDetails().getTenantId(), null, null, WorkFlowConstant.OpenConfig.DATA_STANDARD_OFFLINE);
                        //为空，或者为true
                        if (StringUtils.isEmpty(offlineOpen) || Boolean.parseBoolean(offlineOpen)) {
                            addErrorMsg(i, "标准已存在，状态不可进行数据修改，请先下线标准！");
                            getContextList().get(i).setDataStatus(DataStatus.IMPORT_FAILED);
                            continue;
                        } else {
                            //设置为离线状态
                            dataStandardDTO.setStandardStatus(OFFLINE);
                        }
                    }

                    updateList.add(dataStandardConverter.dtoToEntity(dataStandardDTO));
                } else {
                    addList.add(dataStandardConverter.dtoToEntity(dataStandardDTO));
                }
            }

            if (CollectionUtils.isNotEmpty(addList)) {
                dataStandardRepository.batchInsertSelective(addList);

            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                dataStandardRepository.batchUpdateByPrimaryKey(updateList);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }

        return true;
    }
}
