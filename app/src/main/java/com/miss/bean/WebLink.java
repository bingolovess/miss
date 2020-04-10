package com.miss.bean;

import java.util.List;

public class WebLink {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : 2019最全前端面试问题及答案总结
         * link : https://blog.csdn.net/keyandi/article/details/89227175
         * des :
         */

        private String name;
        private String link;
        private String des;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
