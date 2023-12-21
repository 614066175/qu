package org.xdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.hzero.starter.driver.core.infra.exception.JsonException;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.xdsp.core.CommonGroupClient;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.DataStandardDTO;
import org.xdsp.quality.domain.entity.DataStandard;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.domain.entity.StandardExtra;
import org.xdsp.quality.domain.repository.DataStandardRepository;
import org.xdsp.quality.domain.repository.ReferenceDataRepository;
import org.xdsp.quality.domain.repository.StandardExtraRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.converter.DataStandardConverter;
import org.xdsp.quality.infra.mapper.DataStandardMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.xdsp.core.infra.constant.CommonGroupConstants.GroupType.DATA_STANDARD;
import static org.xdsp.quality.infra.constant.StandardConstant.StandardType.DATA;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.*;

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
    private final ReferenceDataRepository referenceDataRepository;
    private final StandardExtraRepository standardExtraRepository;
    private final DataStandardMapper dataStandardMapper;
    private final DataStandardConverter dataStandardConverter;
    private final XdspProfileClient profileClient;
    private final CommonGroupClient commonGroupClient;


    public DataStandardBatchImportServiceImpl(ObjectMapper objectMapper, DataStandardRepository dataStandardRepository,
                                              CommonGroupRepository commonGroupRepository,
                                              ReferenceDataRepository referenceDataRepository,
                                              StandardExtraRepository standardExtraRepository,
                                              DataStandardMapper dataStandardMapper,
                                              DataStandardConverter dataStandardConverter,
                                              XdspProfileClient profileClient,
                                              CommonGroupClient commonGroupClient) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.commonGroupRepository = commonGroupRepository;
        this.referenceDataRepository = referenceDataRepository;
        this.standardExtraRepository = standardExtraRepository;
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
        List<StandardExtra> extraList = new ArrayList<>();
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
                List<Long> chargeIds = dataStandardMapper.checkCharger(dataStandardDTO.getChargeName(), dataStandardDTO.getTenantId());
                if(CollectionUtils.isNotEmpty(chargeIds)){
                    dataStandardDTO.setChargeId(chargeIds.get(0));
                }
                DataStandard exist = dataStandardRepository.selectOne(DataStandard.builder()
                        .standardCode(dataStandardDTO.getStandardCode())
                        .tenantId(dataStandardDTO.getTenantId())
                        .projectId(dataStandardDTO.getProjectId())
                        .build());
                // 参考数据
                if (PlanConstant.StandardValueType.REFERENCE_DATA.equals(dataStandardDTO.getValueType()) && StringUtils.isNotBlank(dataStandardDTO.getValueRange())) {
                    String referenceDataCode = dataStandardDTO.getValueRange();
                    List<ReferenceData> referenceDataList = referenceDataRepository.select(ReferenceData
                            .builder()
                            .dataCode(referenceDataCode)
                            .projectId(projectId)
                            .tenantId(tenantId)
                            .build());
                    if (CollectionUtils.isNotEmpty(referenceDataList)) {
                        dataStandardDTO.setValueRange(String.valueOf(referenceDataList.get(0).getDataId()));
                    }
                }
                //附加信息处理
                String standardExtraStr = dataStandardDTO.getStandardExtraStr();
                if (StringUtils.isNotEmpty(standardExtraStr)) {
                    try {
                        List<Map<String, Object>> list = JsonUtil.toObj(standardExtraStr, List.class);
                        for (Map<String, Object> map : list) {
                            extraList.add(StandardExtra.builder()
                                    .standardType(DATA)
                                    .extraKey(map.keySet().iterator().next())
                                    .extraValue(String.valueOf(map.values().iterator().next()))
                                    .tenantId(tenantId)
                                    .projectId(projectId)
                                    .build());
                        }
                    } catch (JsonException e) {
                        log.error("Json Error", e);
                        addErrorMsg(i, "JSON格式错误:"+e.getMessage());
                    }
                }
                if (exist != null) {
                    dataStandardDTO.setStandardId(exist.getStandardId());
                    dataStandardDTO.setObjectVersionNumber(exist.getObjectVersionNumber());
                    dataStandardDTO.setReleaseBy(exist.getReleaseBy());
                    dataStandardDTO.setReleaseDate(exist.getReleaseDate());
                    //存在默认使用本身状态
                    dataStandardDTO.setStandardStatus(exist.getStandardStatus());
                    //如果是在线或下线中状态，判断是否需要下线审核，需要则报错
                    if (ONLINE.equals(exist.getStandardStatus()) || OFFLINE_APPROVING.equals(exist.getStandardStatus())) {
                        String offlineOpen = profileClient.getProfileValue(tenantId, projectId, WorkFlowConstant.OpenConfig.DATA_STANDARD_OFFLINE);
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
                    // 附加信息设置外键
                    extraList.forEach(extra -> extra.setStandardId(exist.getStandardId()));
                    //删除旧的附加信息
                    standardExtraRepository.delete(StandardExtra.builder().standardId(exist.getStandardId())
                            .standardType(DATA)
                            .tenantId(exist.getTenantId()).build());
                    updateList.add(dataStandardConverter.dtoToEntity(dataStandardDTO));
                } else {
                    DataStandard dataStandard = dataStandardConverter.dtoToEntity(dataStandardDTO);
                    dataStandardRepository.insertSelective(dataStandard);
                    // 附加信息设置外键
                    extraList.forEach(extra -> extra.setStandardId(dataStandard.getStandardId()));
                }
            }

            if (CollectionUtils.isNotEmpty(addList)) {
                dataStandardRepository.batchInsertSelective(addList);

            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                dataStandardRepository.batchUpdateByPrimaryKey(updateList);
            }
            //批量插入
            if (CollectionUtils.isNotEmpty(extraList)) {
                standardExtraRepository.batchInsertSelective(extraList);
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
