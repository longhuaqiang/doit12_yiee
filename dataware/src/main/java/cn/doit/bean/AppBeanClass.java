package cn.doit.bean;

import lombok.Data;

/**
 * Created by ChenLongWen on 2020/4/30.
 */
@Data
public class AppBeanClass {

    /**
     * eventid : appClickEvent
     * event : {"screen_id":"344","screen_name":"","title":"","element_id":"4"}
     * user : {"uid":"245498","account":"","email":"","phoneNbr":"18248667380","birthday":"","isRegistered":"","isLogin":"","addr":"","gender":"","phone":{"imei":"2881993463620531","mac":"2e-80-50-8e-39-a1-1e","imsi":"8616932323350461","osName":"macos","osVer":"9.0","androidId":"","resolution":"1024*768","deviceType":"360_V","deviceId":"81Kau4","uuid":"L3whyU7BgtLKEkvE"},"app":{"appid":"com.51doit.mall","appVer":"2.0.1","release_ch":"纽扣助手","promotion_ch":"12"},"loc":{"areacode":210921102,"longtitude":121.56605311428365,"latitude":41.91452099352481,"carrier":"ISP02","netType":"WIFI","cid_sn":"463485993989","ip":"138.117.92.76"},"sessionId":"sid-99fe7648-d8e4-4cbe-86af-17b5b3c3a7fc"}
     * timestamp : 1575548955000
     */

    private String eventid;
    private EventBean event;
    private UserBean user;
    private String timestamp;

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public EventBean getEvent() {
        return event;
    }

    public void setEvent(EventBean event) {
        this.event = event;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class EventBean {
        /**
         * screen_id : 344
         * screen_name :
         * title :
         * element_id : 4
         */

        private String screen_id;
        private String screen_name;
        private String title;
        private String element_id;

        public String getScreen_id() {
            return screen_id;
        }

        public void setScreen_id(String screen_id) {
            this.screen_id = screen_id;
        }

        public String getScreen_name() {
            return screen_name;
        }

        public void setScreen_name(String screen_name) {
            this.screen_name = screen_name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getElement_id() {
            return element_id;
        }

        public void setElement_id(String element_id) {
            this.element_id = element_id;
        }
    }

    public static class UserBean {
        /**
         * uid : 245498
         * account :
         * email :
         * phoneNbr : 18248667380
         * birthday :
         * isRegistered :
         * isLogin :
         * addr :
         * gender :
         * phone : {"imei":"2881993463620531","mac":"2e-80-50-8e-39-a1-1e","imsi":"8616932323350461","osName":"macos","osVer":"9.0","androidId":"","resolution":"1024*768","deviceType":"360_V","deviceId":"81Kau4","uuid":"L3whyU7BgtLKEkvE"}
         * app : {"appid":"com.51doit.mall","appVer":"2.0.1","release_ch":"纽扣助手","promotion_ch":"12"}
         * loc : {"areacode":210921102,"longtitude":121.56605311428365,"latitude":41.91452099352481,"carrier":"ISP02","netType":"WIFI","cid_sn":"463485993989","ip":"138.117.92.76"}
         * sessionId : sid-99fe7648-d8e4-4cbe-86af-17b5b3c3a7fc
         */

        private String uid;
        private String account;
        private String email;
        private String phoneNbr;
        private String birthday;
        private String isRegistered;
        private String isLogin;
        private String addr;
        private String gender;
        private PhoneBean phone;
        private AppBean app;
        private LocBean loc;
        private String sessionId;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNbr() {
            return phoneNbr;
        }

        public void setPhoneNbr(String phoneNbr) {
            this.phoneNbr = phoneNbr;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getIsRegistered() {
            return isRegistered;
        }

        public void setIsRegistered(String isRegistered) {
            this.isRegistered = isRegistered;
        }

        public String getIsLogin() {
            return isLogin;
        }

        public void setIsLogin(String isLogin) {
            this.isLogin = isLogin;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public PhoneBean getPhone() {
            return phone;
        }

        public void setPhone(PhoneBean phone) {
            this.phone = phone;
        }

        public AppBean getApp() {
            return app;
        }

        public void setApp(AppBean app) {
            this.app = app;
        }

        public LocBean getLoc() {
            return loc;
        }

        public void setLoc(LocBean loc) {
            this.loc = loc;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public static class PhoneBean {
            /**
             * imei : 2881993463620531
             * mac : 2e-80-50-8e-39-a1-1e
             * imsi : 8616932323350461
             * osName : macos
             * osVer : 9.0
             * androidId :
             * resolution : 1024*768
             * deviceType : 360_V
             * deviceId : 81Kau4
             * uuid : L3whyU7BgtLKEkvE
             */

            private String imei;
            private String mac;
            private String imsi;
            private String osName;
            private String osVer;
            private String androidId;
            private String resolution;
            private String deviceType;
            private String deviceId;
            private String uuid;

            public String getImei() {
                return imei;
            }

            public void setImei(String imei) {
                this.imei = imei;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }

            public String getImsi() {
                return imsi;
            }

            public void setImsi(String imsi) {
                this.imsi = imsi;
            }

            public String getOsName() {
                return osName;
            }

            public void setOsName(String osName) {
                this.osName = osName;
            }

            public String getOsVer() {
                return osVer;
            }

            public void setOsVer(String osVer) {
                this.osVer = osVer;
            }

            public String getAndroidId() {
                return androidId;
            }

            public void setAndroidId(String androidId) {
                this.androidId = androidId;
            }

            public String getResolution() {
                return resolution;
            }

            public void setResolution(String resolution) {
                this.resolution = resolution;
            }

            public String getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(String deviceType) {
                this.deviceType = deviceType;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }
        }

        public static class AppBean {
            /**
             * appid : com.51doit.mall
             * appVer : 2.0.1
             * release_ch : 纽扣助手
             * promotion_ch : 12
             */

            private String appid;
            private String appVer;
            private String release_ch;
            private String promotion_ch;

            public String getAppid() {
                return appid;
            }

            public void setAppid(String appid) {
                this.appid = appid;
            }

            public String getAppVer() {
                return appVer;
            }

            public void setAppVer(String appVer) {
                this.appVer = appVer;
            }

            public String getRelease_ch() {
                return release_ch;
            }

            public void setRelease_ch(String release_ch) {
                this.release_ch = release_ch;
            }

            public String getPromotion_ch() {
                return promotion_ch;
            }

            public void setPromotion_ch(String promotion_ch) {
                this.promotion_ch = promotion_ch;
            }
        }

        public static class LocBean {
            /**
             * areacode : 210921102
             * longtitude : 121.56605311428365
             * latitude : 41.91452099352481
             * carrier : ISP02
             * netType : WIFI
             * cid_sn : 463485993989
             * ip : 138.117.92.76
             */

            private int areacode;
            private double longtitude;
            private double latitude;
            private String carrier;
            private String netType;
            private String cid_sn;
            private String ip;

            public int getAreacode() {
                return areacode;
            }

            public void setAreacode(int areacode) {
                this.areacode = areacode;
            }

            public double getLongtitude() {
                return longtitude;
            }

            public void setLongtitude(double longtitude) {
                this.longtitude = longtitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public String getCarrier() {
                return carrier;
            }

            public void setCarrier(String carrier) {
                this.carrier = carrier;
            }

            public String getNetType() {
                return netType;
            }

            public void setNetType(String netType) {
                this.netType = netType;
            }

            public String getCid_sn() {
                return cid_sn;
            }

            public void setCid_sn(String cid_sn) {
                this.cid_sn = cid_sn;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }
        }
    }
}
