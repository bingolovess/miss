package com.miss.base.enums;

/**
 * 文件大小的枚举
 */
public enum FileSizeEnum {
    B(1),
    KB(2),
    MB(3),
    GB(4);
    private int type;
    FileSizeEnum(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }
}
