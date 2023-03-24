package wo1261931780.JOSPexaminationSystemJava.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*Created by Intellij IDEA.
*Project:JOSP-examinationSystemJava
*Package:wo1261931780.JOSPexaminationSystemJava.entity
*@author liujiajun_junw
*@Date 2023-03-04-38  星期六
*@description 
*/
/**
    * 红果研，考研盒子合并数据库
    */
@ApiModel(description="红果研，考研盒子合并数据库")
@Schema(description="红果研，考研盒子合并数据库")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "merge_database")
public class MergeDatabase implements Serializable {
    /**
     * 排名
     */
    @TableId(value = "rank", type = IdType.AUTO)
    @ApiModelProperty(value="排名")
    @Schema(description="排名")
    private Integer rank;

    /**
     * 考生姓名
     */
    @TableField(value = "student_name")
    @ApiModelProperty(value="考生姓名")
    @Schema(description="考生姓名")
    private String studentName;

    /**
     * 政治
     */
    @TableField(value = "score_polite")
    @ApiModelProperty(value="政治")
    @Schema(description="政治")
    private Integer scorePolite;

    /**
     * 英语
     */
    @TableField(value = "score_english")
    @ApiModelProperty(value="英语")
    @Schema(description="英语")
    private Integer scoreEnglish;

    /**
     * 专业课一
     */
    @TableField(value = "score_professional_1")
    @ApiModelProperty(value="专业课一")
    @Schema(description="专业课一")
    private Integer scoreProfessional1;

    /**
     * 专业课二
     */
    @TableField(value = "score_professional_2")
    @ApiModelProperty(value="专业课二")
    @Schema(description="专业课二")
    private Integer scoreProfessional2;

    /**
     * 总分
     */
    @TableField(value = "score_total")
    @ApiModelProperty(value="总分")
    @Schema(description="总分")
    private Integer scoreTotal;

    /**
     * 公共课总分
     */
    @TableField(value = "score_total_public")
    @ApiModelProperty(value="公共课总分")
    @Schema(description="公共课总分")
    private Integer scoreTotalPublic;

    /**
     * 专业课总分
     */
    @TableField(value = "score_total_professional")
    @ApiModelProperty(value="专业课总分")
    @Schema(description="专业课总分")
    private Integer scoreTotalProfessional;

    /**
     * 红果研排名
     */
    @TableField(value = "hgy_rank")
    @ApiModelProperty(value="红果研排名")
    @Schema(description="红果研排名")
    private Integer hgyRank;

    /**
     * 考研盒子排名
     */
    @TableField(value = "ky_box_rank")
    @ApiModelProperty(value="考研盒子排名")
    @Schema(description="考研盒子排名")
    private Integer kyBoxRank;

    /**
     * 小白考研排名
     */
    @TableField(value = "xb_rank")
    @ApiModelProperty(value="小白考研排名")
    @Schema(description="小白考研排名")
    private Integer xbRank;

    private static final long serialVersionUID = 1L;

    public static final String COL_RANK = "rank";

    public static final String COL_STUDENT_NAME = "student_name";

    public static final String COL_SCORE_POLITE = "score_polite";

    public static final String COL_SCORE_ENGLISH = "score_english";

    public static final String COL_SCORE_PROFESSIONAL_1 = "score_professional_1";

    public static final String COL_SCORE_PROFESSIONAL_2 = "score_professional_2";

    public static final String COL_SCORE_TOTAL = "score_total";

    public static final String COL_SCORE_TOTAL_PUBLIC = "score_total_public";

    public static final String COL_SCORE_TOTAL_PROFESSIONAL = "score_total_professional";

    public static final String COL_HGY_RANK = "hgy_rank";

    public static final String COL_KY_BOX_RANK = "ky_box_rank";

    public static final String COL_XB_RANK = "xb_rank";
}