package com.github.emailtohl.frame.site.tld;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * SimpleTagSupport继承了SimpleTag接口，已帮用户实现了3个功能：
 * （1）setJspContext(JspContext pc)
 * JspContext是PageContext的父类，传入JspContext后可以获取系统所有信息，如request、session、ServletContext
 * （2）setParent(JspTag parent)
 * 在有父标签的情况下，容器会调用此方法，将该标签的父标签作为对象传入，以便执行父标签中的方法，如<c:choose>是<c:when>的父标签
 * （3）setJspBody(JspFragment jspBody)
 * 容器将标签中的内容以JspFragment对象的形式传递进来，它的involve方法可将内容输出到浏览器，也可以将它传递给其他组件使用
 * 
 * 关于标签的属性值
 * 由于标签的属性名未定，所以需要用户来完成set方法，容器会按照类似JavaBean的命名约定，将标签的属性值传入SimpleTagSupport实例中
 */

public class ShowGreeting extends SimpleTagSupport {
	private String[] greetings = {"东风如梦风过无痕，只为心的思念，遥寄一份浓浓的祝福",
			"你是快乐的天才，你是幸福的奇才，你是幸运的人才",
			"人生有一杯酒，只喝一口就会醉倒，那就是朋友间的赤诚",
			"祝你天天好心情，日日好快乐",
			"海内存知己，天涯若比邻，真正的朋友没有距离",
			"乐观豁达的人，能把平凡的日子变得富有情趣，能把沉重的生活变得轻松活泼，能把苦难的光阴变得甜美珍贵，能把繁琐的事变得简单可行",
			"让星星送去我的祝福,让小雨送去清爽的凉风,让好运和美丽永远追随你一生",
			"请微风替我传送;缕缕关怀,托流水替我寄予",
			"送你一片月色，捎去我的思念；送你一片星光，照亮你的心田",
			"生活是一杯清水，你放一点糖它就甜；放一点盐它就咸",
			"生活的原貌是五彩缤纷的，赤橙黄绿青蓝紫是并存的",
			"风吹起如花般破碎的流年，而你的笑容摇晃摇晃，成为我命途中最美的点缀，看天，看雪，看季节深深的暗影",
			"百千夜尽，谁为我，化青盏一座，谁倚门独望过千年烟火",
			"多少黄昏烟雨斜檐，翻开诗篇，勾起了一纸江南",
			"汉霄苍茫，牵住繁华哀伤，弯眉间，命中注定，成为过往",
			"即使摔得满身是伤，笑容依旧可以灿烂如花",
			"落日五湖游，烟波处处愁。浮沈千古事，谁与问东流",
			"随你走在天际，看繁花满地",
			"昙花一现可倾城，美人一顾可倾国。不羡倾城与倾国，蓝天如梦雁飞过",
			"童话已经结束，遗忘就是幸福",
			"终是谁使弦断，花落肩头，恍惚迷离"};
	
	private Integer randomNum;
	
	/*	对应标签中的属性名，如
	<mine:showGreeting greeting="2">
		你好朋友:
		${greeting}
	</mine:showGreeting>
	*/
	public void setRandomNum(Integer num) {
		this.randomNum = num;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		if(randomNum < greetings.length)
			getJspContext().setAttribute("greeting", greetings[randomNum]);
		else
			getJspContext().setAttribute("greeting", "恭喜发财");
		
		getJspBody().invoke(null);//参数null，默认输出到response中
	}
}
