package com.example.model.vo;

import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "统计数据")
public class SummaryVO extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "统计名称")
    private String name;
    @ApiModelProperty(value = "统计值")
    private String value;

}
