package com.flyingpig.chat.dataobject.eneity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 申请表
 * </p>
 *
 * @author flyingpig
 * @since 2024-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("group_application")
@ApiModel(value="Application对象", description="申请表")
public class GroupApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "申请人")
    private Long applyUser;

    @ApiModelProperty(value = "申请添加的群聊")
    private Long applyRoom;


    @ApiModelProperty(value = "申请信息")
    private String applyMsg;


    @ApiModelProperty(value = "审核人")
    private Long auditUser;



    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;



    @ApiModelProperty(value = "申请状态，-1为不通过，0为未通过，1为通过")
    private Byte status;

}
