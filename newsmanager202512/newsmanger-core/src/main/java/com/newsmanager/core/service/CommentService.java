package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newsmanager.api.models.CommentModel;
import com.newsmanager.api.models.PagerTemplate;
import com.newsmanager.core.mapper.IComment;
import com.newsmanager.core.mapper.ICommentLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentService {

    @Autowired
    private IComment commentMapper;

    @Autowired
    private ICommentLike commentLikeMapper;

    public PagerTemplate get(int pageno, int pagesize, String sortProp, String sortOrder) {
        QueryWrapper<CommentModel> qw = new QueryWrapper<>();

        // 动态排序逻辑
        if (sortProp != null && !sortProp.isEmpty()) {
            boolean isAsc = "ascending".equals(sortOrder);
            if ("createdate".equals(sortProp)) {
                // 使用 STR_TO_DATE 确保字符串日期按时间正确排序，处理 2026/1/8 vs 2026/1/14 的问题
                qw.orderBy(true, isAsc, "STR_TO_DATE(REPLACE(createdate, '-', '/'), '%Y/%m/%d %H:%i:%s')");
            } else if ("status".equals(sortProp)) {
                qw.orderBy(true, isAsc, "status");
            } else {
                qw.orderByDesc("cid");
            }
        } else {
            qw.orderByDesc("cid"); // 默认按ID倒序
        }

        IPage<CommentModel> pager = new Page<>(pageno, pagesize);
        IPage<CommentModel> mypage = commentMapper.selectPage(pager, qw);

        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    public List<CommentModel> getbynidwithtree(String nid, Integer currentUid) {
        // 1. 获取该新闻下所有评论
        QueryWrapper<CommentModel> qw = new QueryWrapper<>();
        qw.eq("nid", nid);

        // 核心可见性逻辑：
        // 1. status = 1 (审核通过) 的对所有人可见
        // 2. status = 0 (待审核) 仅对作者自己可见 (uid = currentUid)
        qw.and(wrapper -> {
            wrapper.eq("status", 1);
            if (currentUid != null) {
                wrapper.or().eq("uid", currentUid);
            }
        });

        qw.orderByDesc("createdate");
        List<CommentModel> allComments = commentMapper.selectList(qw);

        if (allComments == null || allComments.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量获取用户信息
        Set<Integer> uids = new HashSet<>();
        for (CommentModel c : allComments) {
            if (c.getUid() != null && c.getUid() > 0) {
                uids.add(c.getUid());
            }
        }

        Map<Integer, String> userNames = new HashMap<>();
        if (!uids.isEmpty()) {
            QueryWrapper<com.newsmanager.api.models.UserModel> uqw = new QueryWrapper<>();
            uqw.in("uid", uids);
            List<com.newsmanager.api.models.UserModel> userList = new com.newsmanager.api.models.UserModel()
                    .selectList(uqw);
            if (userList != null) {
                for (com.newsmanager.api.models.UserModel u : userList) {
                    userNames.put(u.getUid(), u.getUsername());
                }
            }
        }

        // 3. 构建 Map 并填充基础信息
        List<CommentModel> rootComments = new ArrayList<>();
        Map<Integer, CommentModel> commentMap = new HashMap<>();

        for (CommentModel c : allComments) {
            c.setUsername(userNames.getOrDefault(c.getUid(), "匿名用户"));
            c.setReplyList(new ArrayList<>());

            // 点赞统计
            QueryWrapper<com.newsmanager.api.models.CommentLikeModel> lqw = new QueryWrapper<>();
            lqw.eq("cid", c.getCid());
            Long count = commentLikeMapper.selectCount(lqw);
            c.setLikeCount(count != null ? count.intValue() : 0);

            if (currentUid != null) {
                lqw.eq("uid", currentUid);
                c.setIsLiked(commentLikeMapper.selectCount(lqw) > 0);
            } else {
                c.setIsLiked(false);
            }

            commentMap.put(c.getCid(), c);
        }

        // 4. 构建树形结构
        for (CommentModel c : allComments) {
            if (c.getPid() == null || c.getPid() == 0) {
                rootComments.add(c);
            } else {
                CommentModel parent = commentMap.get(c.getPid());
                if (parent != null) {
                    parent.getReplyList().add(c);
                } else {
                    rootComments.add(c);
                }
            }
        }
        return rootComments;
    }

    public void likeComment(Integer cid, Integer uid) {
        QueryWrapper<com.newsmanager.api.models.CommentLikeModel> qw = new QueryWrapper<>();
        qw.eq("cid", cid).eq("uid", uid);
        Long count = commentLikeMapper.selectCount(qw);
        if (count == null || count == 0) {
            com.newsmanager.api.models.CommentLikeModel cl = new com.newsmanager.api.models.CommentLikeModel();
            cl.setCid(cid);
            cl.setUid(uid);
            cl.setCreatedate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            commentLikeMapper.insert(cl);
        }
    }

    public void unlikeComment(Integer cid, Integer uid) {
        QueryWrapper<com.newsmanager.api.models.CommentLikeModel> qw = new QueryWrapper<>();
        qw.eq("cid", cid).eq("uid", uid);
        commentLikeMapper.delete(qw);
    }

    public void save(CommentModel cm) {
        commentMapper.insert(cm);
    }

    public void del(CommentModel commentModel) {
        commentMapper.deleteById(commentModel.getCid());
    }

    public void modify(CommentModel commentModel) {
        commentMapper.updateById(commentModel);
    }

    public CommentModel getone(String cid) {
        return commentMapper.selectById(cid);
    }
}
