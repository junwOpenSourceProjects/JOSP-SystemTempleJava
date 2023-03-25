package wo1261931780.JOSPexaminationSystemJava.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*Created by Intellij IDEA.
*Project:JOSP-examinationSystemJava
*Package:wo1261931780.JOSPexaminationSystemJava.entity
*@author liujiajun_junw
*@Date 2023-03-16-16  星期六
*@description
*/
/**
    * 综合表格的模拟数据，用来测试信息展示
    */
@ApiModel(description="综合表格的模拟数据，用来测试信息展示")
@Schema(description="综合表格的模拟数据，用来测试信息展示")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "complex_table")
public class ComplexTable implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    @Schema(description="主键")
    private Integer id;

    /**
     * 时间
     */
    @TableField(value = "`timestamp`")
    @ApiModelProperty(value="时间")
    @Schema(description="时间")
    private Date timestamp;

    /**
     * 作者
     */
    @TableField(value = "author")
    @ApiModelProperty(value="作者")
    @Schema(description="作者")
    private String author;

    /**
     * 审核人
     */
    @TableField(value = "reviewer")
    @ApiModelProperty(value="审核人")
    @Schema(description="审核人")
    private String reviewer;

    /**
     * 文章标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value="文章标题")
    @Schema(description="文章标题")
    private String title;

    /**
     * 内容简介
     */
    @TableField(value = "content_short")
    @ApiModelProperty(value="内容简介")
    @Schema(description="内容简介")
    private String contentShort;

    /**
     * 文章内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value="文章内容")
    @Schema(description="文章内容")
    private String content;

    /**
     * 预测点击量
     */
    @TableField(value = "forecast")
    @ApiModelProperty(value="预测点击量")
    @Schema(description="预测点击量")
    private String forecast;

    /**
     * 优先级
     */
    @TableField(value = "importance")
    @ApiModelProperty(value="优先级")
    @Schema(description="优先级")
    private Integer importance;

    /**
     * 国家类型
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="国家类型")
    @Schema(description="国家类型")
    private String type;

    /**
     * 展示状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="展示状态")
    @Schema(description="展示状态")
    private String status;

    /**
     * 发布时间
     */
    @TableField(value = "display_time")
    @ApiModelProperty(value="发布时间")
    @Schema(description="发布时间")
    private Date displayTime;

    /**
     * 是否开启评论
     */
    @TableField(value = "comment_disabled")
    @ApiModelProperty(value="是否开启评论")
    @Schema(description="是否开启评论")
    private String commentDisabled;

    /**
     * 当前浏览量
     */
    @TableField(value = "pageviews")
    @ApiModelProperty(value="当前浏览量")
    @Schema(description="当前浏览量")
    private String pageviews;

    /**
     * 链接
     */
    @TableField(value = "image_uri")
    @ApiModelProperty(value="链接")
    @Schema(description="链接")
    private String imageUri;

    /**
     * 发布平台
     */
    @TableField(value = "platforms")
    @ApiModelProperty(value="发布平台")
    @Schema(description="发布平台")
    private String platforms;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_TIMESTAMP = "timestamp";

    public static final String COL_AUTHOR = "author";

    public static final String COL_REVIEWER = "reviewer";

    public static final String COL_TITLE = "title";

    public static final String COL_CONTENT_SHORT = "content_short";

    public static final String COL_CONTENT = "content";

    public static final String COL_FORECAST = "forecast";

    public static final String COL_IMPORTANCE = "importance";

    public static final String COL_TYPE = "type";

    public static final String COL_STATUS = "status";

    public static final String COL_DISPLAY_TIME = "display_time";

    public static final String COL_COMMENT_DISABLED = "comment_disabled";

    public static final String COL_PAGEVIEWS = "pageviews";

    public static final String COL_IMAGE_URI = "image_uri";

    public static final String COL_PLATFORMS = "platforms";
}
