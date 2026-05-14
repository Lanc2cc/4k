package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.dto.CommentDTO;
import com.movie.entity.Comment;
import com.movie.entity.User;
import com.movie.mapper.CommentMapper;
import com.movie.mapper.UserMapper;
import com.movie.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<?> addComment(Long userId, CommentDTO dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setMovieId(dto.getMovieId());
        comment.setContent(dto.getContent().trim());
        comment.setStatus(1);
        this.save(comment);
        return Result.success("评论成功");
    }

    @Override
    public Result<PageResult<Comment>> getCommentsByMovieId(Long movieId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getMovieId, movieId).eq(Comment::getStatus, 1).orderByDesc(Comment::getCreateTime);
        Page<Comment> result = this.page(new Page<>(page, size), wrapper);

        // 填充用户信息
        List<Long> userIds = result.getRecords().stream().map(Comment::getUserId).distinct().collect(Collectors.toList());
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
            result.getRecords().forEach(c -> {
                User u = userMap.get(c.getUserId());
                if (u != null) {
                    c.setUsername(u.getNickname() != null ? u.getNickname() : u.getUsername());
                    c.setAvatar(u.getAvatar());
                }
            });
        }

        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<?> deleteComment(Long userId, Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            return Result.error("无权删除他人评论");
        }
        this.removeById(commentId);
        return Result.success("删除成功");
    }
}
