package com.miss.bean;

import java.util.List;

public class News {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id:1
         * sub : 副标题
         * title : 标题
         * page: 目标页面
         */
        private int id;
        private String sub;
        private String title;
        private String page;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", sub='" + sub + '\'' +
                    ", title='" + title + '\'' +
                    ", page='" + page + '\'' +
                    '}';
        }
    }
}
