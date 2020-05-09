package com.project.gulimallware.ware.vo;

import lombok.Data;
import org.bouncycastle.cms.PasswordRecipientId;

import java.util.List;

/**
 * @author qcw
 */
@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
