package com.ad.menghanyao.ad.enumeration;

public enum ShopEnum {
    SHOP_TYPE_STORE(0,"实体店"),
    SHOP_TYPE_NET(1,"网店"),
    SHOP_TYPE_ACTIVITY(2,"活动店铺"),

    SHOP_STATUS_FORBID(0, "forbid"),
    SHOP_STATUS_NORMAL(1, "normal"),

    SHOP_ARRANGE_FOOD(0,"餐饮类"),
    SHOP_ARRANGE_CLOTHES(1,"服装类"),
    SHOP_ARRANGE_FEMALE(2,"女性类"),
    SHOP_ARRANGE_CHILD(3,"儿童类"),
    SHOP_ARRANGE_OUTDOOR(4,"户外类"),
    SHOP_ARRANGE_PET(5,"宠物类"),

    FORBID_DAYS(3,"封禁天数")
    ;
    private Integer code;
    private String message;

    ShopEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
