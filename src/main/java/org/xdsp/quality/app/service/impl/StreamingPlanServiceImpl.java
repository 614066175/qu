package org.xdsp.quality.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.quality.api.dto.StreamingPlanBaseDTO;
import org.xdsp.quality.api.dto.StreamingPlanDTO;
import org.xdsp.quality.app.service.StreamingPlanService;
import org.xdsp.quality.domain.entity.StreamingPlanBase;
import org.xdsp.quality.domain.repository.StreamingPlanBaseRepository;
import org.xdsp.quality.domain.repository.StreamingPlanRepository;
import org.xdsp.quality.infra.constant.ErrorCode;

import java.util.List;

/**
 * <p>实时数据评估方案表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class StreamingPlanServiceImpl implements StreamingPlanService {

    private final StreamingPlanBaseRepository streamingPlanBaseRepository;
    private final StreamingPlanRepository streamingPlanRepository;

    public StreamingPlanServiceImpl(StreamingPlanBaseRepository streamingPlanBaseRepository, StreamingPlanRepository streamingPlanRepository) {
        this.streamingPlanBaseRepository = streamingPlanBaseRepository;
        this.streamingPlanRepository = streamingPlanRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(StreamingPlanDTO streamingPlanDTO) {
        List<StreamingPlanBaseDTO> streamingPlanBaseDTOList = streamingPlanBaseRepository.selectDTO(StreamingPlanBase.FIELD_PLAN_ID, streamingPlanDTO.getPlanId());
        if (!streamingPlanBaseDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        return streamingPlanRepository.deleteByPrimaryKey(streamingPlanDTO);
    }
}
