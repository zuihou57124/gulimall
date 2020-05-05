package com.project.gulimallproduct.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.google.gson.internal.$Gson$Types;
import io.renren.validGroup.Add;
import io.renren.validGroup.Update;
import io.renren.validGroup.UpdateStatus;
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
	@NotNull(message = "修改时请指定id",groups = {Update.class,UpdateStatus.class})
	@Null(message = "添加时不能手动指定id",groups = {Add.class})
	@TableId(type = IdType.AUTO)
	private Long brandId;
	/**
	 * 品牌名
	 * message:校验出错信息
	 */

	@NotBlank(message = "品牌名不能为空!",groups = {Add.class,Update.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "必须为合法的url地址",groups = {Add.class,Update.class})
	@NotEmpty(message = "品牌logo不能为空",groups = {Add.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@Min(value = 0,message = "显示状态只能为0或1",groups = {Add.class,Update.class, UpdateStatus.class})
	@Max(value = 1,message = "显示状态只能为0或1",groups = {Add.class,Update.class, UpdateStatus.class})
	@NotNull(message = "请选择显示状态",groups = {Add.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是字母",groups = {Add.class,Update.class})
	@NotBlank(message = "检索首字母不能为空",groups = {Add.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "最小值为0",groups = {Add.class,Update.class})
	@NotNull(message = "排序不能为空",groups = {Add.class})
	private Integer sort;

}
