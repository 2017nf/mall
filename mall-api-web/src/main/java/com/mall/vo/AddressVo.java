package com.mall.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @date 2016年12月12日
 */
public class AddressVo implements Serializable {

    private static final long serialVersionUID = -8261886280598614119L;


    private String oldId;
    private String newId;

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }
}
