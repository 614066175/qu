package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.quality.app.service.StandardTeamService;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.domain.repository.StandardTeamRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>标准表应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Service
public class StandardTeamServiceImpl implements StandardTeamService {
    private final StandardTeamRepository standardTeamRepository;

    public StandardTeamServiceImpl(StandardTeamRepository standardTeamRepository) {
        this.standardTeamRepository = standardTeamRepository;
    }

    @Override
    public Page<StandardTeamDTO> list(PageRequest pageRequest, StandardTeamDTO standardTeamDTO) {
        return null;
    }

    @Override
    public List<StandardTeamDTO> listAll(StandardTeamDTO standardTeamDTO) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long standardTeamId) {
        StandardTeamDTO standardTeamDTO = standardTeamRepository.selectDTOByPrimaryKey(standardTeamId);
        if (standardTeamDTO == null) {
            throw new CommonException(ErrorCode.STANDARD_TEAM_NOT_EXIST);
        }
        //（删除的字段标准组下若有子级字段标准组，子级字段标准组更新为顶层字段标准组，即父级字段标准组字段清空；
        // 存在其他字段标准组的继承自为当前删除行字段标准组时，提示“当前字段标准组已被其他字段标准组继承，删除失败，请确认”）

        List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByCondition(Condition.builder(StandardTeam.class)
                .andWhere(Sqls.custom().andEqualTo(StandardTeam.FIELD_INHERITE_TEAM_ID, standardTeamId))
                .build());
        // 存在其他字段标准组的继承自为当前删除行字段标准组时，提示“当前字段标准组已被其他字段标准组继承，删除失败，请确认”）
        if (CollectionUtils.isNotEmpty(standardTeamDTOS)) {
            throw new CommonException(ErrorCode.STANDARD_TEAM_IS_INHERITED);
        }


        //子标准组
        List<StandardTeam> sonStandardTeamList = standardTeamRepository.selectByCondition(Condition.builder(StandardTeam.class)
                .andWhere(Sqls.custom().andEqualTo(StandardTeam.FIELD_PARENT_TEAM_ID, standardTeamId))
                .build());

        //删除的字段标准组下若有子级字段标准组，子级字段标准组更新为顶层字段标准组，即父级字段标准组字段清空；
        if (CollectionUtils.isNotEmpty(sonStandardTeamList)) {
            sonStandardTeamList.forEach(sonStandardTeam -> sonStandardTeam.setParentTeamId(null));
            standardTeamRepository.batchUpdateOptional(sonStandardTeamList, StandardTeam.FIELD_PARENT_TEAM_ID);
        }
    }
}
