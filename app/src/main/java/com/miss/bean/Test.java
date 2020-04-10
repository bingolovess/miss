package com.miss.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Test {

    /**
     * success : true
     * data : {"projects":[{"name":"演示用","url":"rlogin://zqyecml.travel/cjfkb","email":"i.potkw@vuoecxf.ie","address":"湖北省 黄石市 下陆区","string":"★★★★★","number":5,"boolean":false,"object":{"320000":"江苏省","330000":"浙江省"}},{"name":"演示用","url":"mailto://mdvmneii.re/pjr","email":"l.bli@nrucckshj.fi","address":"香港特别行政区 香港岛 南区","string":"★★★★","number":74,"boolean":false,"object":{"310000":"上海市","330000":"浙江省"}},{"name":"演示用","url":"mid://lnl.bj/xvoahgcc","email":"n.bosbyi@xgcva.ci","address":"新疆维吾尔自治区 昌吉回族自治州 玛纳斯县","string":"★★★★★★","number":29,"boolean":false,"object":{"320000":"江苏省","330000":"浙江省"}},{"name":"演示用","url":"cid://merxj.br/dqqeeun","email":"q.qfwtd@hint.ro","address":"山东省 淄博市 桓台县","string":"★★★★★★★★★","number":69,"boolean":false,"object":{"310000":"上海市","330000":"浙江省"}},{"name":"演示用","url":"mailto://dgeqgkrto.fo/oua","email":"m.cuk@qiuddzymg.bg","address":"湖北省 武汉市 汉南区","string":"★★★★★★★★★","number":74,"boolean":true,"object":{"320000":"江苏省","330000":"浙江省"}},{"name":"演示用","url":"mid://pydmr.ne/fpzue","email":"b.esmvgxqvyf@lqryf.nu","address":"云南省 昆明市 五华区","string":"★★","number":14,"boolean":true,"object":{"310000":"上海市","320000":"江苏省"}},{"name":"演示用","url":"mid://ygpcgr.aw/bsepfmxu","email":"q.hahc@wysrm.pl","address":"福建省 漳州市 龙海市","string":"★★","number":38,"boolean":false,"object":{"310000":"上海市","320000":"江苏省"}},{"name":"演示用","url":"http://gyvbhxsp.sy/cvu","email":"o.ymvcs@gdwvtp.gu","address":"辽宁省 本溪市 南芬区","string":"★★★★","number":89,"boolean":false,"object":{"320000":"江苏省","330000":"浙江省"}},{"name":"演示用","url":"mid://vtqithg.cd/nijeykf","email":"d.ksgq@xke.np","address":"重庆 重庆市 沙坪坝区","string":"★★★★★★★","number":12,"boolean":false,"object":{"310000":"上海市","330000":"浙江省"}}]}
     */

    private boolean success;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<ProjectsBean> projects;

        public List<ProjectsBean> getProjects() {
            return projects;
        }

        public void setProjects(List<ProjectsBean> projects) {
            this.projects = projects;
        }

        public static class ProjectsBean {
            /**
             * name : 演示用
             * url : rlogin://zqyecml.travel/cjfkb
             * email : i.potkw@vuoecxf.ie
             * address : 湖北省 黄石市 下陆区
             * string : ★★★★★
             * number : 5
             * boolean : false
             * object : {"320000":"江苏省","330000":"浙江省"}
             */

            private String name;
            private String url;
            private String email;
            private String address;
            private String string;
            private int number;
            @SerializedName("boolean")
            private boolean booleanX;
            private ObjectBean object;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getString() {
                return string;
            }

            public void setString(String string) {
                this.string = string;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public boolean isBooleanX() {
                return booleanX;
            }

            public void setBooleanX(boolean booleanX) {
                this.booleanX = booleanX;
            }

            public ObjectBean getObject() {
                return object;
            }

            public void setObject(ObjectBean object) {
                this.object = object;
            }

            public static class ObjectBean {
                /**
                 * 320000 : 江苏省
                 * 330000 : 浙江省
                 */

                @SerializedName("320000")
                private String _$320000;
                @SerializedName("330000")
                private String _$330000;

                public String get_$320000() {
                    return _$320000;
                }

                public void set_$320000(String _$320000) {
                    this._$320000 = _$320000;
                }

                public String get_$330000() {
                    return _$330000;
                }

                public void set_$330000(String _$330000) {
                    this._$330000 = _$330000;
                }
            }
        }
    }
}
