package com.project.gulimallproduct.product.vo;

import lombok.Data;
import java.util.List;

/**
 * @author qcw
 */
@Data
public class Catelog2Vo {

    private String catelog1Id;

    private List<Catalog3> catalog3List;

    private String id;

    private String name;

    @Data
    public static class Catalog3{

        private String catalog2Id;

        private String id;

        private String name;

    }

}
