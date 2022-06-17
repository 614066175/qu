package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.AimStatistics;
import com.hand.hdsp.quality.domain.repository.AimStatisticsRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>标准落标统计表 管理 API</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@RestController("aimStatisticsController.v1")
@RequestMapping("/v1/{organizationId}/aim-statisticss")
public class AimStatisticsController extends BaseController {

    private AimStatisticsRepository aimStatisticsRepository;

    public AimStatisticsController(AimStatisticsRepository aimStatisticsRepository) {
        this.aimStatisticsRepository = aimStatisticsRepository;
    }

    @ApiOperation(value = "标准落标统计表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  AimStatisticsDTO aimStatisticsDTO, @ApiIgnore @SortDefault(value = AimStatistics.FIELD_STATISTICS_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        aimStatisticsDTO.setTenantId(tenantId);
        Page<AimStatisticsDTO> list = aimStatisticsRepository.pageAndSortDTO(pageRequest, aimStatisticsDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "标准落标统计表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "statisticsId",
            value = "标准落标统计表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{statisticsId}")
    public ResponseEntity<?> detail(@PathVariable Long statisticsId) {
        AimStatisticsDTO aimStatisticsDTO = aimStatisticsRepository.selectDTOByPrimaryKeyAndTenant(statisticsId);
        return Results.success(aimStatisticsDTO);
    }

    @ApiOperation(value = "创建标准落标统计表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId, @RequestBody AimStatisticsDTO aimStatisticsDTO) {
        aimStatisticsDTO.setTenantId(tenantId);
        this.validObject(aimStatisticsDTO);
        aimStatisticsRepository.insertDTOSelective(aimStatisticsDTO);
        return Results.success(aimStatisticsDTO);
    }

    @ApiOperation(value = "修改标准落标统计表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId, @RequestBody AimStatisticsDTO aimStatisticsDTO) {
        aimStatisticsRepository.updateDTOWhereTenant(aimStatisticsDTO, tenantId);
        return Results.success(aimStatisticsDTO);
    }

    @ApiOperation(value = "删除标准落标统计表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody AimStatisticsDTO aimStatisticsDTO) {
        aimStatisticsDTO.setTenantId(tenantId);
        aimStatisticsRepository.deleteByPrimaryKey(aimStatisticsDTO);
        return Results.success();
    }
}
