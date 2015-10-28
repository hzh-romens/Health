package com.romens.yjk.health.config;

import java.lang.reflect.Field;

/**
 * Created by anlc on 2015/10/16.
 */
public class HelpQuestionConfig {

    public String question1 = "常见问题";
    public String question2 = "购药问题";
    public String question3 = "在线问诊、问药问题";

    public String question1_sub1 = "Q:如何注册要健康会员卡帐号？";
    public String question1_sub2 = "Q:为什么注册不成功，提示“手机号已经被注册”？";
    public String question1_sub3 = "Q:能否在线支付？";
    public String question1_sub4 = "Q：是否支持退换货？";
    public String question1_sub5 = "Q：是否可以在线取消订单？";

    public String question1_answer1 = "A:可根据以下步骤注册要健康帐号：点击“注册”-输入常用的手机号码，点击“获取验证码”-输入短息收到的验证码，点击“提交”即可，若您在使用要健康前已是要健康合作药店会员，则注册成功后会员信息自动绑定。";
    public String question1_answer2 = "A:1个手机号只能注册1次；该提示说明您的手机号已经有注册记录，不能再次注册。由于要健康采用使用手机号码加手机验证码的登陆方式，所以您可直接使用该手机号进行登陆。";
    public String question1_answer3 = "A：您好，要健康药店目前只支持货到付款，不支持在线支付，您可以在收到药品后采用支付现金、pos机刷卡或是微信支付等多种方式。";
    public String question1_answer4 = "A：您好，由于药品处于特殊商品，不能无条件退换。如您所购买的药品存在过期、受污染、或是药品本身存在质量问题，则您需提供药品、发票等各种单据以及专业检测机构确认商品质量问题的检测报告书到所在药店进行退换，于患者使用不当或个人体质导致的不良反应不予退换";
    public String question1_answer5 = "A；要健康支持在线取消订单，在您没有收到商品的情况下，您可以通过点击订单管理中的“取消订单“功能键来取消订单。";

    public String question2_sub1 = "Q：你们的药是正品么？";
    public String question2_sub2 = "Q：为什么这个产品跟我在药店看到的不一样？";
    public String question2_sub3 = "Q:为什么上次买的价格与现在的价格不一样？";
    public String question2_sub4 = "Q：你们都发什么快递呢？";
    public String question2_sub5 = "Q：多少钱包邮？";

    public String question2_answer1 = "A:请放心，我们网站的商品都是各大药房、药店进行发货，正品有保证。";
    public String question2_answer2 = "A:您好，由于产品包装会经常有更替，可能网站上没有做好及时通知，但请您放心，我们网站的商品都是正规渠道进货，不存在假货。";
    public String question2_answer3 = "A:您好，影响商品售价变化的原因有很多，比如促销活动的时间、商品进价的调整、物价因素等，都会导致价格的调整。";
    public String question2_answer4 = "A:您好，我们是根据您的地理位置自动为您匹配附近药店，下单成功后相关人员会立即为您配送，由于是同城运输，所以不存在发快递的问题。";
    public String question2_answer5 = "A:您好我们的商品都是免费配送的。";

    public String question3_sub1 = "Q：在线咨询是免费的吗？";
    public String question3_sub2 = "Q：提交的问题什么时候能够回复？";
    public String question3_sub3 = "Q：要健康上回复问题的医生是不是真正的医生？";

    public String question3_answer1 = "A：我们为用户提供免费在线问药服务，以及免费图文问诊服务，如您需要获得更加详细的解答，可以使用我们的语音问诊、视频问诊服务，我们会按实际情况向您收取一定的费用。";
    public String question3_answer2 = "A：由于要健康回复患者问题的医生均为各大医院的在职医生，平日医生工作繁忙，您提出的问题可能无法立即得到回复，但在24小时之内，医生会抽出业余时间尽快为您解答，提出疑问后也请您能够耐心等待，敬请谅解。如您的病情紧急，请及时就医，以免延误病情。";
    public String question3_answer3 = "A：您好，要健康百分之百真人回复。要健康在合作医生的时候会要求医生提供医师资格证明以及医生所在医院的工作证明，并对资格信息进行严格的审核，确保万无一失才会上线。";

    public static String getData(String info) {
        String result = "";
        try {
            Field field = HelpQuestionConfig.class.getField(info);
            result = field.get(new HelpQuestionConfig()).toString();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

}
