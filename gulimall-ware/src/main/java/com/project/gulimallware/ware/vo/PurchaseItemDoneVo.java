package com.project.gulimallware.ware.vo;

import io.swagger.models.auth.In;
import lombok.Data;
import org.bouncycastle.cms.PasswordRecipientId;

/**
 * @author qcw
 */
@Data
public class PurchaseItemDoneVo {

        private Long itemId;

        private Integer status;

        private String reson;

}
