package com.hand.hdsp.quality.api.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>问题知识库表 数据传输对象</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("问题知识库表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestDTO extends AuditDomain {

    @ApiModelProperty("主键，供外键使用")
    private Long suggestId;

    @ApiModelProperty(value = "所属规则类型id")
    private Long ruleId;

    @ApiModelProperty(value = "所属问题ID")
    @NotNull
    private Long problemId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "建议序号")
    @NotNull
    private Long suggestOrder;

    @ApiModelProperty(value = "整改建议，必填")
    @NotBlank
    private String suggestContent;

    @ApiModelProperty(value = "规则类型 HDSP.XQUA.CHECK_RULE")
    @Transient
    private String ruleType;

    @ApiModelProperty(value = "校验方式 HDSP.XQUA.CHECK_WAY")
    @Transient
    private String checkWay;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    @Transient
    private String checkItem;

    @ApiModelProperty(value = "规则字段数据类型 HDSP.XMOD.FIELD_TYPE")
    @Transient
    private String columnType;

    @Transient
    private String realName;

    /**
     * 最后更新时间
     */
    @Transient
    private Date lastUpdateDate;

    /**
     * 筛选开始时间
     */
    @Transient
    private String startDate;

    /**
     * 筛选开始结束时间
     */
    @Transient
    private String endDate;

    /**
     * 字符串切割转换后的类型list
     */
    @Transient
    private List<String> types;

    @Transient
    private Long objectVersionNumber;

    @Transient
    private Long resultBaseId;

}
