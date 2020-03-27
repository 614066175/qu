package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanDTO;
import com.hand.hdsp.quality.app.service.StreamingPlanService;
import com.hand.hdsp.quality.domain.entity.StreamingPlanBase;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>实时数据评估方案表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class StreamingPlanServiceImpl implements StreamingPlanService {

    private StreamingPlanBaseRepository streamingPlanBaseRepository;
    private StreamingPlanRepository streamingPlanRepository;

    public StreamingPlanServiceImpl(StreamingPlanBaseRepository streamingPlanBaseRepository, StreamingPlanRepository streamingPlanRepository) {
        this.streamingPlanBaseRepository = streamingPlanBaseRepository;
        this.streamingPlanRepository = streamingPlanRepository;
    }

    @Override
    public int delete(StreamingPlanDTO streamingPlanDTO) {
        List<StreamingPlanBaseDTO> streamingPlanBaseDTOList = streamingPlanBaseRepository.selectDTO(StreamingPlanBase.FIELD_PLAN_ID, streamingPlanDTO.getPlanId());
        if (!streamingPlanBaseDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        return streamingPlanRepository.deleteByPrimaryKey(streamingPlanDTO);
    }
}
