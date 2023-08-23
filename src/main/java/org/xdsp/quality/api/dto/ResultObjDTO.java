package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * <p>
 * 返回给前台数据的封装类
 * </p>
 * 
 * @author 邓志龙
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultObjDTO {

  private Integer code;
  private String message;
  private String status;
  private Boolean result;

}
