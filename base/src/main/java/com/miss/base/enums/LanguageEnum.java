package com.miss.base.enums;

/**
 * 语言枚举
 */
public enum LanguageEnum {
        LANGUAGE("language"),//语言，用于SharedPreferences存储的Key值
        LANGUAGE_zh("zh"),          //中文，
        LANGUAGE_zh_hk("zh_hk"),    // 繁体中文(中国香港)
        LANGUAGE_zh_tw("zh_tw"),    // 繁体中文(中国台湾地区)
        LANGUAGE_en("en"),          //英语
        LANGUAGE_es("es"),          //西班牙语
        LANGUAGE_fr("fr"),          //法语
        LANGUAGE_ar("ar"),          //阿拉伯语
        LANGUAGE_ko("ko"),          //韩语
        LANGUAGE_pt("pt"),          //葡萄牙语
        LANGUAGE_de("de"),          //德语
        LANGUAGE_tr("tr"),          //土耳其语
        LANGUAGE_ja("ja"),          //日语
        LANGUAGE_vi("vi"),          //越南语
        LANGUAGE_my("my"),          //缅甸语
        LANGUAGE_ru("ru");          //俄语

        private String language;//自定义属性

        /**构造函数，枚举类型只能为私有*/
        LanguageEnum(String language) {
            this.language = language;
        }

        //自定义方法
        public String getLanguage(){
            return language;
        }
}
