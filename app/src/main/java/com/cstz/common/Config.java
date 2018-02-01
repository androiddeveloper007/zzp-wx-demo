package com.cstz.common;

import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Config {

    public static final boolean ISDEMO = false;

    public static final long SPLASH_DELAY_MILLIS = 800; // 延迟2秒

    public static final long SPLASH_DELAY_MILLIS_PATTERN = 1000; // 延迟2秒

    public final static boolean isTest = false;//是否为测试环境,控制记住密码

//    static private String httpIp = isTest ? "https://m.dm.wuxingjinrong.com" : "https://app.wuxingjinrong.com";

//	static private String httpIp = "http://192.168.1.111:8080";//小军cg

//    static private String httpIp = "http://192.168.1.244:15080";//陈刚cg

//	static private String httpIp = "http://mbank.wx.loc";//测试cg

	static private String httpIp = "https://mbankdm.wuxingjinrong.com";//DM存管

//    	static private String httpIp = "http://192.168.1.216:13080";//陈刚

//	static private String httpIp = "http://192.168.1.114:18787/";//冯士良cg

//	static private String httpIp = "http://m.wx.loc";//测试

//	static private String httpIp = "http://192.168.1.168:8082";//小军

    final static public String servicePhone = "400-022-3080";//客服电话

    final static public String APP_NAME = "wuxingcaifu";
    final static public String APP_ID = "wx2aefd699219c3321";

    static public String getHttpConfig() {
        return httpIp;
    }

    static public ArrayList<String> getSexList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("男");
        list.add("女");
        return list;
    }

    static public int getBankNameLogo(String bankName) {
        int r_id = 0;
        if (bankName.equals("中国工商银行")) {
            r_id = R.drawable.bank_1;
        } else if (bankName.equals("中国建设银行")) {
            r_id = R.drawable.bank_2;
        } else if (bankName.equals("中国农业银行")) {
            r_id = R.drawable.bank_3;
        } else if (bankName.equals("中国银行")) {
            r_id = R.drawable.bank_4;
        } else if (bankName.equals("中国邮政储蓄银行")) {
            r_id = R.drawable.bank_5;
        } else if (bankName.equals("招商银行")) {
            r_id = R.drawable.bank_6;
        } else if (bankName.equals("平安银行")) {
            r_id = R.drawable.bank_7;
        } else if (bankName.equals("华夏银行")) {
            r_id = R.drawable.bank_8;
        } else if (bankName.equals("中信银行")) {
            r_id = R.drawable.bank_9;
        } else if (bankName.equals("中国民生银行")) {
            r_id = R.drawable.bank_10;
        } else if (bankName.equals("兴业银行")) {
            r_id = R.drawable.bank_11;
        } else if (bankName.equals("浦发银行")) {
            r_id = R.drawable.bank_12;
        } else if (bankName.equals("广发银行")) {
            r_id = R.drawable.bank_13;
        } else if (bankName.equals("中国光大银行")) {
            r_id = R.drawable.bank_14;
        } else if (bankName.equals("交通银行")) {
            r_id = R.drawable.bank_15;
        } else if (bankName.equals("北京银行")) {
            r_id = R.drawable.bank_16;
        } else if (bankName.equals("南京银行")) {
            r_id = R.drawable.bank_17;
        } else if (bankName.equals("宁波银行")) {
            r_id = R.drawable.bank_18;
        } else if (bankName.equals("上海农商银行")) {
            r_id = R.drawable.bank_19;
        } else if (bankName.equals("上海银行")) {
            r_id = R.drawable.bank_20;
        } else if (bankName.equals("天津银行")) {
            r_id = R.drawable.bank_21;
        } else if (bankName.equals("民生银行")) {
            r_id = R.drawable.bank_10;
        }
        return r_id;
    }

    static public String getBankCardStatus(String status) {
        String str = "";
        if (status.equals("0")) {
            str = "未绑定";
        } else if (status.equals("1")) {
            str = "认证成功";

        } else if (status.equals("2")) {
            str = "审核中";
        } else if (status.equals("3")) {
            str = "认证失败";
        }

        return str;
    }

    static public String getRealNameStatus(String status) {
        String str = "";
        if (status.equals("1")) {
            str = "审核中";
        } else if (status.equals("2")) {
            str = "审核不通过";
        } else if (status.equals("3")) {
            str = "已认证";
        }
        return str;
    }

    static public String getProductStatus(String status) {
        String str = "";

        switch (Convert.toInt(status)) {
            case -1:
                str = "初审不通过";
                break;
            case -2:
                str = "复审不通过";
                break;
            case 1:
                str = "初审中";
                break;
            case 2:
                str = "立即投资";
                break;
            case 3:
                str = "已募满";
                break;
            case 4:
                str = "兑付中";
                break;
            case 5:
                str = "已完成";
                break;
            default:
                break;
        }
        return str;
    }

    static public String getProviceCity() {
        String str = "{\"data\":[{\"name\":\"北京\",\"cities\":[\"北京市\"]}";
        str += ",{ \"name\": \"天津\", \"cities\": [\"天津市\"]}";
        str += ",{ \"name\": \"河北\", \"cities\": [\"石家庄市\", \"秦皇岛市\", \"廊坊市\", \"保定市\", \"邯郸市\", \"唐山市\", \"邢台市\", \"衡水市\", \"张家口市\", \"承德市\", \"沧州市\", \"衡水市\"] }";
        str += ",{ \"name\": \"山西\", \"cities\": [\"太原市\", \"大同市\", \"长治市\", \"晋中市\", \"阳泉市\", \"朔州市\", \"运城市\", \"临汾市\"]}";
        str += ",{ \"name\": \"内蒙古\", \"cities\": [\"呼和浩特市\", \"赤峰市\", \"通辽市\", \"锡林郭勒市\", \"兴安市\"] }";
        str += ",{ \"name\": \"辽宁\", \"cities\": [\"大连市\", \"沈阳市\", \"鞍山市\", \"抚顺市\", \"营口市\", \"锦州市\", \"丹东市\", \"朝阳市\", \"辽阳市\", \"阜新市\", \"铁岭市\", \"盘锦市\", \"本溪市\", \"葫芦岛市\"] }";
        str += ",{ \"name\": \"吉林\", \"cities\": [\"长春市\", \"吉林市\", \"四平市\", \"辽源市\", \"通化市\", \"延吉市\", \"白城市\", \"辽源市\", \"松原市\", \"临江市\", \"珲春市\"] }";
        str += ",{ \"name\": \"黑龙江\", \"cities\": [\"哈尔滨市\", \"齐齐哈尔市\", \"大庆市\", \"牡丹江市\", \"鹤岗市\", \"佳木斯市\", \"绥化市\", \"黑河市\"] }";
        str += ",{ \"name\": \"上海\", \"cities\": [\"上海市\"] }";
        str += ",{ \"name\": \"江苏\", \"cities\": [\"南京市\", \"苏州市\", \"无锡市\", \"常州市\", \"扬州市\", \"徐州市\", \"南通市\", \"镇江市\", \"泰州市\", \"淮安市\", \"连云港市\", \"宿迁市\", \"盐城市\", \"淮阴市\", \"沐阳市\", \"张家港市\"] }";
        str += ",{ \"name\": \"浙江\", \"cities\": [\"杭州市\", \"金华市\", \"宁波市\", \"温州市\", \"嘉兴市\", \"绍兴市\", \"丽水市\", \"湖州市\", \"台州市\", \"舟山市\", \"衢州市\"] }";
        str += ",{ \"name\": \"安徽\", \"cities\": [\"合肥市\", \"马鞍山市\", \"蚌埠市\", \"黄山市\", \"芜湖市\", \"淮南市\", \"铜陵市\", \"阜阳市\", \"宣城市\", \"安庆市\"] }";
        str += ",{ \"name\": \"福建\", \"cities\": [\"福州市\", \"厦门市\", \"泉州市\", \"漳州市\", \"南平市\", \"龙岩市\", \"莆田市\", \"三明市\", \"宁德市\"] }";
        str += ",{ \"name\": \"江西\", \"cities\": [\"南昌市\", \"景德镇市\", \"上饶市\", \"萍乡市\", \"九江市\", \"吉安市\", \"宜春市\", \"鹰潭市\", \"新余市\", \"赣州市\"] }";
        str += ",{ \"name\": \"山东\", \"cities\": [\"青岛市\", \"济南市\", \"淄博市\", \"烟台市\", \"泰安市\", \"临沂市\", \"日照市\", \"德州市\", \"威海市\", \"东营市\", \"荷泽市\", \"济宁市\", \"潍坊市\", \"枣庄市\", \"聊城市\"] }";
        str += ",{ \"name\": \"河南\", \"cities\": [\"郑州市\", \"洛阳市\", \"开封市\", \"平顶山市\", \"濮阳市\", \"安阳市\", \"许昌市\", \"南阳市\", \"信阳市\", \"周口市\", \"新乡市\", \"焦作市\", \"三门峡市\", \"商丘市\"] }";
        str += ",{ \"name\": \"湖北\", \"cities\": [\"武汉市\", \"襄樊市\", \"孝感市\", \"十堰市\", \"荆州市\", \"黄石市\", \"宜昌市\", \"黄冈市\", \"恩施市\", \"鄂州市\", \"江汉市\", \"随枣市\", \"荆沙市\", \"咸宁市\"] }";
        str += ",{ \"name\": \"湖南\", \"cities\": [\"长沙市\", \"湘潭市\", \"岳阳市\", \"株洲市\", \"怀化市\", \"永州市\", \"益阳市\", \"张家界市\", \"常德市\", \"衡阳市\", \"湘西市\", \"邵阳市\", \"娄底市\", \"郴州市\"] }";
        str += ",{ \"name\": \"广东\", \"cities\": [\"广州市\", \"深圳市\", \"东莞市\", \"佛山市\", \"珠海市\", \"汕头市\", \"韶关市\", \"江门市\", \"梅州市\", \"揭阳市\", \"中山市\", \"河源市\", \"惠州市\", \"茂名市\", \"湛江市\", \"阳江市\", \"潮州市\", \"云浮市\", \"汕尾市\", \"潮阳市\", \"肇庆市\", \"顺德市\", \"清远市\"] }";
        str += ",{ \"name\": \"广西\", \"cities\": [\"南宁市\", \"桂林市\", \"柳州市\", \"梧州市\", \"来宾市\", \"贵港市\", \"玉林市\", \"贺州市\"] }";
        str += ",{ \"name\": \"海南\", \"cities\": [\"海口市\", \"三亚市\"] }";
        str += ",{ \"name\": \"重庆\", \"cities\": [\"重庆市\"] }";
        str += ",{ \"name\": \"四川\", \"cities\": [\"成都市\", \"达州市\", \"南充市\", \"乐山市\", \"绵阳市\", \"德阳市\", \"内江市\", \"遂宁市\", \"宜宾市\", \"巴中市\", \"自贡市\", \"康定市\", \"攀枝花市\"] }";
        str += ",{ \"name\": \"贵州\", \"cities\": [\"贵阳市\", \"遵义市\", \"安顺市\", \"黔西南市\", \"都匀市\"] }";
        str += ",{ \"name\": \"云南\", \"cities\": [\"昆明市\", \"丽江市\", \"昭通市\", \"玉溪市\", \"临沧市\", \"文山市\", \"红河市\", \"楚雄市\", \"大理市\"] }";
        str += ",{ \"name\": \"西藏\", \"cities\": [\"拉萨市\", \"林芝市\", \"日喀则市\", \"昌都市\"] }";
        str += ",{ \"name\": \"陕西\", \"cities\": [\"西安市\", \"咸阳市\", \"延安市\", \"汉中市\", \"榆林市\", \"商南市\", \"略阳市\", \"宜君市\", \"麟游市\", \"白河市\"] }";
        str += ",{ \"name\": \"甘肃\", \"cities\": [\"兰州市\", \"金昌市\", \"天水市\", \"武威市\", \"张掖市\", \"平凉市\", \"酒泉市\"] }";
        str += ",{ \"name\": \"青海\", \"cities\": [\"黄南市\", \"海南市\", \"西宁市\", \"海东市\", \"海西市\", \"海北市\", \"果洛市\", \"玉树市\"] }";
        str += ",{ \"name\": \"宁夏\", \"cities\": [\"银川市\", \"吴忠市\"] }";
        str += ",{ \"name\": \"新疆\", \"cities\": [\"乌鲁木齐市\", \"哈密市\", \"喀什市\", \"巴音郭楞市\", \"昌吉市\", \"伊犁市\", \"阿勒泰市\", \"克拉玛依市\", \"博尔塔拉市\"] }";
        str += "]}";
        //31
        return str;
    }

    /**
     * 计算投资收益
     *
     * @param investAmount
     * @param annualRate
     * @param award
     * @param deadline
     * @return
     */
    public static String getInterest(String investAmount, float annualRate, float award, int deadline) {
        int iAmout = Convert.toInt(investAmount);
        float shouyi = (annualRate + award) / 100;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(iAmout * shouyi / 365 * deadline);
        //return decimalFormat.format(Convert.toInt(investAmount)*deadline*(annualRate+award)/365);

    }

    public static String getTiXianStatus(int status) {
        String str = "";
        switch (status) {
            case 1:
                str = "审核中";
                break;
            case 2:
                str = "成功";
                break;
            case 3:
                str = " 取消";
                break;
            case 4:
                str = "转账中";
                break;
            case 5:
                str = "失败";
                break;
            default:
                break;
        }


        return str;
    }
    public static boolean nowIsInRange(Date startTime, Date now, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date targetDate = calendar.getTime();
        long targetTime = targetDate.getTime();
        long nowTime = now.getTime();
        if (targetTime > nowTime) {
            return true;
        } else {
            return false;
        }
    }
    public static Date stringToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = java.sql.Date.valueOf(str);
        return date;
    }
}