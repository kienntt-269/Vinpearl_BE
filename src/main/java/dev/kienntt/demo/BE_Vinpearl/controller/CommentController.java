package dev.kienntt.demo.BE_Vinpearl.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {
//    @Autowired
//    private CommentService commentService;
//
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @PostMapping
//    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
//        comment.setPost(new Post(postId));
//        Comment savedComment = commentService.saveComment(comment);
//        simpMessagingTemplate.convertAndSend("/topic/comments/" + postId, savedComment);
//        return savedComment;
//    }
}
