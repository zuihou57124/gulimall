package com.project.gulimallproduct.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId(type = IdType.AUTO)
	private Long brandId;
	/**
	 * 品牌名
	 * message:校验出错信息
	 */

	@NotBlank(message = "品牌名不能为空!")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "必须为合法的url地址")
	@NotEmpty(message = "品牌logo不能为空")
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "/^[a-zA-Z]$/",message = "检索首字母必须是字母")
	@NotBlank(message = "检索首字母不能为空")
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "最小值为0")
	@NotNull(message = "排序不能为空")
	private Integer sort;

}
