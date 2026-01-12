/*
 Navicat Premium Dump SQL

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : news_system

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 12/01/2026 12:29:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_admin
-- ----------------------------
DROP TABLE IF EXISTS `tb_admin`;
CREATE TABLE `tb_admin`  (
  `aid` int NOT NULL AUTO_INCREMENT,
  `adminname` varchar(25) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `pwd` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `isadmin` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`aid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_admin
-- ----------------------------
INSERT INTO `tb_admin` VALUES (1, 'momo', 'RA58Prm7zUwzw1NTVKUWBQ==', 1, 1);
INSERT INTO `tb_admin` VALUES (2, 'admin', 'ICy5YqxZB1uWSwcVLSNLcA==', 0, 1);

-- ----------------------------
-- Table structure for tb_collection
-- ----------------------------
DROP TABLE IF EXISTS `tb_collection`;
CREATE TABLE `tb_collection`  (
  `colid` int NOT NULL AUTO_INCREMENT,
  `phone` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `nid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `createdate` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`colid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_collection
-- ----------------------------
INSERT INTO `tb_collection` VALUES (2, '19171977208', 'fb692c8f1cep', '2026/1/8 10:35:16');

-- ----------------------------
-- Table structure for tb_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment`  (
  `cid` int NOT NULL AUTO_INCREMENT,
  `uid` int NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `createdate` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `nid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`cid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_comment
-- ----------------------------
INSERT INTO `tb_comment` VALUES (2, 0, '一般', '2026/1/7 12:51:49', '23cdd569nvr6', 1);
INSERT INTO `tb_comment` VALUES (3, 0, '46', '2026/1/7 12:52:06', '23cdd569nvr6', 1);
INSERT INTO `tb_comment` VALUES (4, 1, '未来可期', '2026/1/7 13:08:02', '23cdd569nvr6', 1);
INSERT INTO `tb_comment` VALUES (5, 1, 'hao', '2026/1/8 10:35:30', '066473d29hyw', 1);

-- ----------------------------
-- Table structure for tb_histroy
-- ----------------------------
DROP TABLE IF EXISTS `tb_histroy`;
CREATE TABLE `tb_histroy`  (
  `hid` int NOT NULL AUTO_INCREMENT,
  `uid` int NULL DEFAULT NULL,
  `nid` int NULL DEFAULT NULL,
  PRIMARY KEY (`hid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_histroy
-- ----------------------------

-- ----------------------------
-- Table structure for tb_like
-- ----------------------------
DROP TABLE IF EXISTS `tb_like`;
CREATE TABLE `tb_like`  (
  `lid` int NOT NULL AUTO_INCREMENT,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `createdate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`lid`) USING BTREE,
  UNIQUE INDEX `unique_like`(`phone` ASC, `nid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_like
-- ----------------------------
INSERT INTO `tb_like` VALUES (6, '19171977208', '066473d29hyw', '2026-01-09 13:07:18');

-- ----------------------------
-- Table structure for tb_news
-- ----------------------------
DROP TABLE IF EXISTS `tb_news`;
CREATE TABLE `tb_news`  (
  `nid` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ntitle` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `createdate` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `author` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `cnt` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  `tid` int NULL DEFAULT NULL,
  PRIMARY KEY (`nid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_news
-- ----------------------------
INSERT INTO `tb_news` VALUES ('066473d29hyw', '沙特主导联军：也门南方过渡委员会领导人逃往不明地点', '沙特主导联军：也门南方过渡委员会领导人逃往不明地点 新华网', '2026-01-07 03:29:36', '新华网', 1, 1, 2);
INSERT INTO `tb_news` VALUES ('0c55bba1rtex', '从文本跨界到媒介跨界：舞剧创作在突破中带来新看点', '从文本跨界到媒介跨界：舞剧创作在突破中带来新看点 光明网', '2026-01-06 18:15:00', '光明网', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('13253139gstz', '人民币市场汇价（1月7日）', '人民币市场汇价（1月7日） 新华网人民币对美元即期汇率升破6.98 专家表示1月人民币汇率将维持平稳双向波动格局 新浪财经人工智能公司“Clipto.AI”完成新一轮Pre-A++轮融资 36kr.com今年首个交易日延续升势：人民币对美元即期汇率升破6.98 thepaper.cn1月5日人民币对美元中间价报7.0230 上调58个基点 中国经济网', '2026-01-07 01:59:16', '新华网', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('1464772cuves', '2026首批“国补”落地，新政“新”在哪儿？', '2026首批“国补”落地，新政“新”在哪儿？ chinanews.com.cn', '2026-01-06 22:25:54', '中国新闻网', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('176ec05brn8y', '委内瑞拉将为美军袭击牺牲者举行一周哀悼', '委内瑞拉将为美军袭击牺牲者举行一周哀悼 新华网委内瑞拉将为美国袭击牺牲者哀悼7天 新华网古巴外长：古巴人民将抵御侵略捍卫祖国 新浪财经美对委军事行动致55名军人死亡 中国网新闻中心委内瑞拉被古巴寄生的二十年 凤凰网', '2026-01-07 00:50:41', '新华网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('2389388e9jl0', '【文化中国行】话剧《父母爱情》即将亮相合肥', '【文化中国行】话剧《父母爱情》即将亮相合肥 安徽新闻联播', '2026-01-06 23:15:00', '安徽新闻联播', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('23cdd569nvr6', '环球音乐集团与英伟达签署新的 AI 合作协议', '环球音乐集团与英伟达签署新的 AI 合作协议 动点科技环球音乐与英伟达签署全新人工智能合作协议 SOHU英伟达与环球音乐合作，打造人工智能驱动的音乐发现和粉丝互动平台 Cryptopolitan环球音乐携手英伟达，AI重塑音乐发现与创作方式！ SOHU环球音乐与英伟达签署全新人工智能合作协议-AI云资讯 icloudnews.net', '2026-01-07 03:10:00', '动点科技', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('265642a51ip7', '短道速滑奥运冠军武大靖官宣退役：转身不是落幕', '短道速滑奥运冠军武大靖官宣退役：转身不是落幕 中国网武大靖：冰刃未凉，热爱永续 新华网31岁奥运冠军武大靖官宣退役 新浪财经武大靖宣布退役：没有退役仪式 观察人物 | 安静转身，冬奥会冠军武大靖的冰面告别 SOHU', '2026-01-06 23:58:00', '中国网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('28113fcc6on4', '黄仁勋新年首场演讲：AI超级芯片平台Rubin全面投产，开源自动驾驶推理模型', '黄仁勋新年首场演讲：AI超级芯片平台Rubin全面投产，开源自动驾驶推理模型 thepaper.cn马斯克：英伟达Alpamayo 5 - 6年后或成特斯拉FSD竞争对手 凤凰网黄仁勋称“今年将非常好”：将持续投资上下游和合作伙伴，要为中国市场做贡献要加入竞争 thepaper.cn国际消费电子展 英伟达AMD推AI新硬件摘要 新浪新闻_手机新浪网英伟达CES发了堆“怪物” 但跟你的电脑机箱已经毫无关系 驱动之家', '2026-01-05 23:38:00', '澎湃新闻', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('28706e53iiqn', '韩国总统李在明在上海出席中韩创新创业论坛', '韩国总统李在明在上海出席中韩创新创业论坛 新华网', '2026-01-07 02:31:16', '新华网', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('2c7e19f6yxb7', '羽毛球——马来西亚公开赛：石宇奇晋级', '羽毛球——马来西亚公开赛：石宇奇晋级 新华网惨遭一轮游！中国队输球引发网友不满，头号组合被日本队逆转 凤凰网马来西亚公开赛首日国羽7胜2负 陈雨菲逆转“梁王”出局 新浪财经马来西亚羽毛球公开赛：郭新娃/陈芳卉晋级混双次轮 光明网羽毛球丨马来西亚公开赛：韩悦晋级 SOHU', '2026-01-07 01:33:09', '新华网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('3583887csi03', '2026年中国将推出数据领域国家标准 智能体等前沿方向标准加速布局', '2026年中国将推出数据领域国家标准 智能体等前沿方向标准加速布局 中华网2026年我国将在数据领域发布不少于30项重点标准 新华网从“有数据”到“有生产力”：2026数据要素价值如何兑现 thepaper.cn2026年我国将推出30余项数据领域国家标准 新浪财经2026前瞻｜迈入“价值释放年” 持续点亮数据要素价值 SOHU', '2026-01-06 23:32:05', '中华网', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('3f7d352c-9efc-4d19-ab4e-1f2cc20301e8', '黄仁勋挖角谷歌资深高管，英伟达任命首位 CMO', '北京时间 1 月 9 日，据《华尔街日报》报道，AI 芯片巨头英伟达已聘请谷歌营销高管艾莉森・瓦贡菲尔德（Alison Wagonfeld）担任其首席营销官（CMO）。\n知情人士称，瓦贡菲尔德将成为英伟达的首位 CMO，她将承担此前由多名员工负责的职责，并向 CEO 黄仁勋（Jensen Huang）汇报工作。瓦贡菲尔德将在今年 2 月上任，届时英伟达营销和传播团队的所有成员都将向她汇报工作。\n\n瓦贡菲尔德周四在领英上宣布，她将离开工作近十年的谷歌，跳槽英伟达。在谷歌，她负责谷歌云计算业务的营销工作。\n\n英伟达在过去三年中快速发展，这得益于该公司在先进 AI 工具和服务领域的核心计算机芯片供应商地位。自 2022 年 OpenAI 发布其 ChatGPT 聊天机器人以来，AI 工具和服务迅速兴起。\n\n这一无与伦比的增长使得英伟达的市场营销需求愈发迫切，也让黄仁勋成为 AI 热潮中最具代表性的公众人物之一。\n\n周一，黄仁勋在 CES 2026 上比往年更早地介绍了公司下一代 AI 服务器系统。他表示，巨大的市场需求正在推动整个行业加快开发进度。他对观众说：“AI 竞赛已经开始，每个人都在努力迈向下一个前沿。”\n\n英伟达最新一季度财报显示，公司实现了创纪录的 570 亿美元销售额，同比增长 62%，超出分析师预期。', '2026-01-09 08:23:23', '凤凰科技', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('459b47b7mhy8', '新疆塔什库尔干塔吉克自治县发生5.2级地震 暂未接到人员伤亡报告', '新疆塔什库尔干塔吉克自治县发生5.2级地震 暂未接到人员伤亡报告 新华网', '2026-01-06 23:44:35', '新华网', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('5fec049dagyv', '一城千面拍遍古今 荆州入选文旅短剧活力城市', '一城千面拍遍古今 荆州入选文旅短剧活力城市 湖北文明网预计贡献两百多万就业岗位 微短剧产业呈爆发式增长 新浪财经微短剧这一年②|流量变“留量” 赋能地域文化传播 “微短剧+文旅”打开内容升维新局面 thepaper.cn“我被《盛夏芬德拉》打蒙了！”2026年新年晚会短剧演员刷屏，短剧播放量创新高，头部演员成本增长两三倍，档期排队3个月起 每日经济新闻2025，短剧的“成年礼” 36kr.com', '2026-01-07 01:20:16', '湖北文明网', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('6529f7986mq7', '网球——香港公开赛：商竣程晋级', '网球——香港公开赛：商竣程晋级 新华网【赛场】新年首胜！吴易昺完胜No.54玛洛桑，晋级香港赛次轮再战卢布列夫 新浪财经吴易昺出战香港网球公开赛 期待新赛季“开门红” 上海热线中国男网开门红！商竣程、吴易昺双双晋级ATP香港站次轮 SOHU黄泽林晋级ATP250香港公开赛第二轮 chinanews.com.cn', '2026-01-07 01:33:07', '新华网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('6854cb5fqzd0', '广东影视政策落地观察｜创新激活市场，第一票仓的担当', '广东影视政策落地观察｜创新激活市场，第一票仓的担当 南方网', '2026-01-07 01:38:00', '南方网', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('68928ffb8exy', '加速时代如何打造“长寿”剧集——《唐朝诡事录》系列引发的思考', '加速时代如何打造“长寿”剧集——《唐朝诡事录》系列引发的思考 新华网还得是原班人马，《唐朝诡事录2》新预告一出，拍第三季都有可能 SOHU‘唐诡’系列为何成为长寿剧王？揭秘IP保鲜的制作经验 SOHU《唐诡奇谭》：长寿剧集的成功密码 SOHU唐诡系列启示录：加速时代如何打造“长寿”剧集 SOHU', '2026-01-07 01:33:40', '新华网', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('68e239f6uslo', '冰雪旅游让世界看见冬季“酷中国”', '冰雪旅游让世界看见冬季“酷中国” 新华网“世界市长对话·哈尔滨”聚焦冰雪经济发展 人民网国际“文旅推介官”上蓝天 黑龙江文旅携手南航黑龙江分公司共庆第42届中国·哈尔滨国际冰雪节到来 凤凰网镜头里的冬日哈尔滨 新华网冰雪节盛大启幕哈市呼兰警方为冰雪盛会保驾护航- 中国日报网 China Daily', '2026-01-06 08:06:26', '新华网', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('6edcd4c2tbtv', '特朗普：委内瑞拉将向美国移交数千万桶石油', '特朗普：委内瑞拉将向美国移交数千万桶石油 凤凰网特朗普据悉拟会晤石油高管 力促委内瑞拉产量复苏 新浪财经美政府欲迅速“变现”委石油资源 美石油巨头持谨慎态度 中国网特朗普称委内瑞拉将向美国移交3000万至5000万桶石油 thepaper.cn石油巨头集体观望，美国政府的委内瑞拉石油蓝图将落空？ 凤凰网', '2026-01-07 00:25:27', '凤凰网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('714a96c3rs18', '委临时总统：委内瑞拉没有“外国代理人”', '委临时总统：委内瑞拉没有“外国代理人” 新华网就任委内瑞拉代总统后的罗德里格斯，只是个开始！ 新浪新闻_手机新浪网委内瑞拉代总统：委未受外部势力统治 中国网新闻中心CIA被曝早已作出机密评估：若马杜罗失去权力，原政权高层最适合领导委内瑞拉 观察释新闻｜委内瑞拉临时总统如何为政权“极限求生”？ thepaper.cn', '2026-01-07 00:01:45', '新华网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('71b39fcadqcp', '委内瑞拉要求美国法官承认无权审理马杜罗案', '委内瑞拉要求美国法官承认无权审理马杜罗案 新浪财经专家：美对委袭击是“头疼医脚”“内病外治” 光明网委内瑞拉遇到的不是“靖康之变”，而是“土木堡” 新浪财经国际观察丨“庭审”马杜罗实为“美式强权秀” 中国日报网子思：如果“马杜罗事件”是个转折点，世界将走向何方？ 观察', '2026-01-06 16:59:32', '新浪财经', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('72b569ad5162', '骄阳似我女主第一人选赵今麦 顾漫严选实至名归', '骄阳似我女主第一人选赵今麦 顾漫严选实至名归 中华网', '2026-01-07 03:19:37', '中华网', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('75e5fe0crrki', '梅派经典《穆桂英挂帅》即将亮相合肥', '梅派经典《穆桂英挂帅》即将亮相合肥 安徽新闻联播', '2026-01-06 23:18:00', '安徽新闻联播', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('793f7de6qkce', '大美中国 气象万千丨朝霞染京华 破晓即惊艳', '大美中国 气象万千丨朝霞染京华 破晓即惊艳 cma.gov.cn北京朝霞，浪漫满分！ 新浪财经北京天空上演绚丽粉色与金色交织的朝霞 SOHU美丽北京｜朝霞染京城 白塔映焰霞 SOHU北京绝美朝霞，来了！ 央广网', '2026-01-07 02:26:18', '中国气象局', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('7b16f4d44c0z', '第十二届全国大众冰雪季在长春启动', '第十二届全国大众冰雪季在长春启动 新华网冰雕雪塑扮靓冬日“北国春城” 新华网第十二届全国大众冰雪季将在长春开幕 赛事活动推动冰雪运动走向大众 新浪财经吉林 让资源优势成为产业胜势 激活冰雪产业发展新动能 人民网四川省第八届全民健身冰雪季启幕- 中国日报网 China Daily', '2026-01-06 15:40:03', '新华网', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('7b70322ep1sl', '谁会是下一个委内瑞拉？起底美国军事干涉颠覆的黑手', '谁会是下一个委内瑞拉？起底美国军事干涉颠覆的黑手 珠海网“最恶劣的帝国主义行径” 多国民众怒斥美对委军事行动 新华网联合国人权高专办：美国干涉委内瑞拉“让每个国家变得更加不安全” 光明网参考消息：马克龙的表态变了 新浪财经美国点名想“欺负”的国家 在安理会批评美国干涉委内瑞拉内政 中国日报网', '2026-01-06 15:59:00', '珠海网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('8e33d310vmgg', '台外交部：期盼委内瑞拉顺利过渡到民主政体', '台外交部：期盼委内瑞拉顺利过渡到民主政体 联合早报外交部发言人：反对丛林法则，反对干涉内政，共同维护世界和平稳定 新华网中方强烈谴责美方对委的单边、非法、霸凌行径 新浪财经中国常驻联合国代表团临时代办孙磊：美国违反《联合国宪章》 中国日报网有记者在委内瑞拉报道时被拘押，外交部：目前在委中国记者都安全 thepaper.cn', '2026-01-06 06:49:00', '联合早报', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('9e00f53cfuts', '综述丨地缘政治风险推动国际金价油价上涨', '综述丨地缘政治风险推动国际金价油价上涨 新华网油价上涨，委内瑞拉不确定性主导市场情绪 新浪财经周二原油价格下跌 SOHU美委、俄乌、以伊--2025的尾声，地缘风险走高，推动油价大涨 QQ News国际油价上涨超1% 委内瑞拉局势扰动市场 中国日报网', '2026-01-06 04:42:00', '新华网', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('9fdd5ca9c1um', '央行定调2026货币政策 降准降息“灵活高效”', '央行定调2026货币政策 降准降息“灵活高效” 新华网东方财富财经早餐 1月7日周三 东方财富2026年适度宽松的货币政策将着力服务经济高质量发展 光明网央行定调2026年七大重点工作：保持社会融资条件相对宽松，强化虚拟货币监管 thepaper.cn央行：继续实施适度宽松的货币政策，持续深化金融改革和对外开放 新浪财经', '2026-01-07 01:59:59', '新华网', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('a7067236n8c0', '埃塞俄比亚东北部发生车祸致22人死亡', '埃塞俄比亚东北部发生车祸致22人死亡 人民网国际', '2026-01-07 00:40:00', '人民网国际', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('b05384d5zzau', '十五五”20项举措发力绿色消费', '十五五”20项举措发力绿色消费 武汉市商务局市场潜力持续激发 9部门20项举措推动 绿色消费渐成风尚 北京市人民政府商务部表示 已培育绿色商场1161家 绿色饭店3500家 china.zjol.com.cn市场监管总局：鼓励互联网平台企业上线“绿色餐饮”服务标识 新浪新闻_手机新浪网商务部：以旧换新政策带动消费品销售额3.92万亿元 中国网新闻中心', '2026-01-07 02:24:48', '武汉市商务局', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('b0e75819f8gm', '国台办：将刘世芳、郑英耀列为“台独”顽固分子', '国台办：将刘世芳、郑英耀列为“台独”顽固分子 观察国台办：有利于两岸同胞利益福祉的事我们都乐观其成 thepaper.cn最新！“台独”顽固分子清单 中国青年网国台办：已公布“台独”顽固分子14人，“台独”打手帮凶12人 新京报国台办：截至目前已公布“台独”顽固分子14人 “台独”打手帮凶12人 中国日报网', '2026-01-07 02:30:10', '观察者网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('b785c29a22qq', '外汇局：稳妥有序推进银行外汇展业改革', '外汇局：稳妥有序推进银行外汇展业改革 央广网外汇局部署新年工作：有效保障各类主体用汇需求 防范化解外部冲击风险 财新国家外汇管理局2025年重点工作回顾 同花顺我省首笔跨境融资便利化业务落地 新浪财经国家外汇管理局：有序扩大跨境贸易高水平开放试点加大力度支持跨境电商等贸易新业态发展财经新闻Financial News AASTOCKS.com', '2026-01-07 01:50:27', '央广网', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('c292db8ctc5w', '洪森承诺全面执行与泰国停火协议', '洪森承诺全面执行与泰国停火协议 新华网泰国军方：泰柬边境发生爆炸 一泰国军人受伤 新华网洪森承诺全面执行与泰国停火协议，以恢复柬泰和平 thepaper.cn外交部：中方对柬泰停火共识得到逐步落实感到欣慰 人民日报泰军方称柬方违反停火声明致士兵受伤 新浪财经', '2026-01-07 02:48:04', '新华网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('c59346775kqn', '关于举办2026中国足球协会超级杯比赛的通知', '关于举办2026中国足球协会超级杯比赛的通知 中国足球协会超级杯3月1日南京奥体举办 国安作为客队对阵海港 新浪财经2026超级杯3月1日开球 北京国安对阵上海海港 南京奥体中心对决 中华网官方：海港vs国安的超级杯将于3月1日15:30在南京奥体中心进行-腾讯新闻 QQ.com根据中国足协官方通知，2026中国足球协会超级杯...|上海海港 懂球帝', '2026-01-06 04:33:00', '中国足球协会', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('cbf2f8f6i25k', '榆林市气象台发布大风蓝色预警[Ⅳ级/一般]', '榆林市气象台发布大风蓝色预警[Ⅳ级/一般] 中国气象局-天气预报辽宁省大连市旅顺口区气象台发布大风蓝色预警信号 新浪财经小雪+降温10℃+阵风9级！今天，河北这些地方有降雪…… 光明网山西省平遥县发布大风蓝色预警 天气网天津海洋中心气象台发布海上大风蓝色预警/Ⅳ级/一般 中国气象局-天气预报', '2026-01-07 02:51:00', '中国气象局-天气预报', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('cc74674e5po8', '中国禁止向日本出口涉军两用物项', '中国禁止向日本出口涉军两用物项 RFI中方出手了，日方竟叫屈 观察消息人士：中方正研究收紧对日稀土出口许可审查 新浪财经新闻8点见丨商务部：加强两用物项对日本出口管制；武大靖宣布退役 新京报中方宣布加强两用物项对日本出口管制，日媒先慌了 凤凰网', '2026-01-06 13:55:50', '法广中文网', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('cd61d9bc9jyi', '导演李路、编剧陈宇新作《人之初》热播 在对人性的追问里，发现端详亲情的新角度', '导演李路、编剧陈宇新作《人之初》热播 在对人性的追问里，发现端详亲情的新角度 新华网《人之初》：三重迷境探索人性本源的创新之作 thepaper.cn《人之初》：这就是演技！张若昀与杨玏的隔空飙戏看得让人头皮发麻 QQ News《人之初》：双时空叙事剖析人性善恶 欧洲头条人之初 叙事破局引领观剧新体验 中华网', '2026-01-06 01:06:57', '新华网', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('d418f7d83swo', '顽强拼搏、突破极限 中国冰雪运动员高燃壁纸来了', '顽强拼搏、突破极限 中国冰雪运动员高燃壁纸来了 中国青年网潜心追赶，力争突破——米兰冬奥会中国健儿参赛前景展望 新华网追光丨一个月后，冬奥会见！ 光明网助力米兰冬奥会·中国加油！“冰雪有我·激情绽放”全国高校青年宣传活动正式启动！ 中华网中国冰雪健儿全力备赛（竞技观察） 人民日报', '2026-01-07 01:11:00', '中国青年网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('e409ac650ktf', '常规赛半程胜场数过半 北京女排磨合渐入佳境', '常规赛半程胜场数过半 北京女排磨合渐入佳境 京报网', '2026-01-06 14:57:00', '京报网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('e5ebbab0o6m8', '意甲-戴维传射米雷蒂破门 尤文3-0完胜萨索洛', '意甲-戴维传射米雷蒂破门 尤文3-0完胜萨索洛 央视体育意甲：尤文图斯胜萨索洛 新华网体坛联播｜梅西透露曾接受心理治疗，保级大战西汉姆不敌森林 thepaper.cn[图]意甲-戴维传射米雷蒂破门 尤文3-0完胜萨索洛 央视体育凯夫伦：斯帕莱蒂要求我做所有的事，他知道我能防守也能进球 懂球帝', '2026-01-06 23:43:20', '央视体育', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('e6b78bcbn2dp', '打开长江经济带高质量可持续发展之窗', '打开长江经济带高质量可持续发展之窗 求是网长江大保护十周年｜离天最近的地方谁在守望 新华网AI图说：长江经济带十年焕新 光明网【新思想引领新征程】十年蝶变 谱写新时代长江之歌 中国青年网一见·十年，大江之变映照发展之变 央视网', '2026-01-07 02:00:23', '求是网', 0, 1, 1);
INSERT INTO `tb_news` VALUES ('ed560de58vb9', '公开市场业务交易公告 [2026]第3号', '公开市场业务交易公告 [2026]第3号 pbc.gov.cn央行6日开展162亿元7天期逆回购操作 中国经济网本周中国央行公开市场将有13236亿元逆回购到期 新浪财经每日债市速递 | 央行公开市场单日净回笼2963亿 SOHU人行今逆回购286亿人币 单日净回笼5,002亿人币 AASTOCKS.com', '2026-01-06 01:20:30', '中国人民银行', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('f1064992xrqq', '两场NBA季前赛将于10月在澳门登场--大湾区频道', '两场NBA季前赛将于10月在澳门登场--大湾区频道 人民网两场NBA季前赛将于10月在澳门举办 新华网2026中国赛落户澳门，阿门和弗拉格手持26号“Macao”球衣庆祝 懂球帝NBA官宣：26年中国赛火箭vs独行侠 10月10-12日继续澳门举办 SOHU达拉斯独行侠和休斯顿火箭将于10 月在澳门进行两场季前赛 美通社官网', '2026-01-07 00:52:00', '人民网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('f3dc4a94yj90', '白宫高层称：没人会为了格陵兰与美国军事对抗-观察者网', '白宫高层称：没人会为了格陵兰与美国军事对抗-观察者网 观察特朗普，为何盯着格陵兰岛不放 新华网白宫：特朗普政府正谋划获取格陵兰岛 国家安全优先事项 中华网部分共和党人抵制特朗普对格陵兰岛的企图 新浪财经联合国回应特朗普涉格陵兰岛言论：坚信成员国领土完整的不可侵犯性 thepaper.cn', '2026-01-06 08:47:00', '观察者网', 0, 1, 2);
INSERT INTO `tb_news` VALUES ('f78787bfstlv', '时隔30多年，75岁刘晓庆将再演武则天，还是毛戈平为她化妆--国内', '时隔30多年，75岁刘晓庆将再演武则天，还是毛戈平为她化妆--国内 globalpeople.com.cn最新 | 32年后，刘晓庆再演武则天！ 新浪财经75岁刘晓庆再演武则天，不服老状态逆天，毛戈平化妆助力经典重现 SOHU75岁刘晓庆再演武则天，与毛戈平王炸组合挑战竖屏微短剧 SOHU75岁刘晓庆再演武则天，本人发文称“必是王炸” SOHU', '2026-01-07 02:24:00', '《环球人物》杂志', 0, 1, 4);
INSERT INTO `tb_news` VALUES ('fb692c8f1cep', '中国男足如何开启重建新征程？', '中国男足如何开启重建新征程？ chinanews.com.cn冲世界杯！国足主帅邵佳一谈执教目标 凤凰网足球丨国足在广东集训 新华网广东新国足在肇庆开启2026年首训 新浪财经颜骏凌：一哥能力强也有人格魅力；我来国家队要帮助年轻人 懂球帝', '2026-01-07 02:57:37', '中国日报网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('fc779340fq3o', '脑机接口商业化进程提速--经济·科技', '脑机接口商业化进程提速--经济·科技 人民网财经北京脑机接口交互技术位于全球头部行列 新浪财经马斯克量产宣言引爆市场 脑机接口商业化加速 中华网301363，连续20%涨停！利好来袭，这一概念大爆发 凤凰网脑机接口治病：从科幻到现实还有多远？ 人民日报', '2026-01-06 00:50:00', '人民网财经', 0, 1, 3);
INSERT INTO `tb_news` VALUES ('fdc4a5a4xw4w', 'WTT多哈冠军赛今日拉开帷幕，国乒首日3人出战', 'WTT多哈冠军赛今日拉开帷幕，国乒首日3人出战 珠海网2026年WTT多哈冠军赛抽签仪式举行 新华网今日！央视直播国乒，林诗栋+蒯曼+王艺迪等出战，张本美和+伊藤美诚亮相 凤凰网多哈赛女单签表：王曼昱首轮对阵大藤沙月，蒯曼将承担抗日重任-腾讯新闻 QQ News正式退出，孙颖莎遗憾，官宣决定，原因找到，王楚钦祝福 SOHU', '2026-01-06 23:46:00', '珠海网', 0, 1, 5);
INSERT INTO `tb_news` VALUES ('fe1d32ad4wnx', '外交部回应日本议员石平窜访中国台湾：宵小狂言不值一评', '外交部回应日本议员石平窜访中国台湾：宵小狂言不值一评 新华网受中国制裁的日本参议员石平访台 机场高喊“台湾绝对不是中国一部分” DW日本参议员石平窜台兜售“两国论”，国台办回应：宵小之辈 thepaper.cn原籍中国的日本参议员石平抵台：台湾绝非中国一部分 台湾就是台湾 RFI毛宁回应日本议员石平窜台：宵小狂言，不值一提 中华网', '2026-01-06 07:38:19', '新华网', 0, 1, 1);

-- ----------------------------
-- Table structure for tb_notice
-- ----------------------------
DROP TABLE IF EXISTS `tb_notice`;
CREATE TABLE `tb_notice`  (
  `noid` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `createdate` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `target` int NULL DEFAULT 0 COMMENT '公告展示对象: 0=用户端, 1=客户端管理员, 2=面向所有人',
  `visible` int NULL DEFAULT 1 COMMENT '是否展示: 0=隐藏, 1=展示',
  `priority` int NULL DEFAULT 2 COMMENT '优先级: 1=高, 2=中, 3=低'
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_notice
-- ----------------------------
INSERT INTO `tb_notice` VALUES ('20260112101438174', '因服务器扩容及数据库结构优化需求，本平台将于 2026 年 1 月 14 日（周日）02:00 - 04:00 进行停机维护。', '2026年1月12日 上午10:14:38', 2, 1, 1);
INSERT INTO `tb_notice` VALUES ('20260112111248207', '重要通知', '2026年1月12日 上午11:12:48', 1, 0, 2);

-- ----------------------------
-- Table structure for tb_theme
-- ----------------------------
DROP TABLE IF EXISTS `tb_theme`;
CREATE TABLE `tb_theme`  (
  `tid` int NOT NULL AUTO_INCREMENT,
  `tname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`tid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_theme
-- ----------------------------
INSERT INTO `tb_theme` VALUES (1, '国内');
INSERT INTO `tb_theme` VALUES (2, '国际');
INSERT INTO `tb_theme` VALUES (3, '商业');
INSERT INTO `tb_theme` VALUES (4, '娱乐');
INSERT INTO `tb_theme` VALUES (5, '体育');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `uid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `sex` int NULL DEFAULT NULL,
  `age` int NULL DEFAULT NULL,
  `pic` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, 'momo', '19171977208', 1, 18, '');

-- ----------------------------
-- View structure for v_theme_news
-- ----------------------------
DROP VIEW IF EXISTS `v_theme_news`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_theme_news` AS select (select `t`.`tname` from `tb_theme` `t` where (`t`.`tid` = `n`.`tid`)) AS `tname`,count(`n`.`tid`) AS `cnt` from `tb_news` `n` group by `n`.`tid`;

SET FOREIGN_KEY_CHECKS = 1;
