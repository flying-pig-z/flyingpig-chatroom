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
 * 好友/私聊申请表
 * </p>
 *
 * @author flyingpig
 * @since 2024-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("friend_application")
@ApiModel(value="FriendApplication对象", description="好友/私聊申请表")
public class FriendApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "申请人")
    private Long applyUser;

    @ApiModelProperty(value = "申请信息")
    private String applyMsg;

    @ApiModelProperty(value = "审核人")
    private Long auditUser;

    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty(value = "0未通过，-1不通过，1通过")
    private Byte status;


}
