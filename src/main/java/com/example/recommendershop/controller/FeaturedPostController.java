package com.example.recommendershop.controller;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.featuredPost.request.FeaturedPostRequest;
import com.example.recommendershop.dto.featuredPost.response.FeaturedPostResponse;
import com.example.recommendershop.service.featuredPost.FeaturedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/featuredPosts")
public class FeaturedPostController {
    @Autowired
    private FeaturedPostService featuredPostService;
    @PostMapping("/")
    public FeaturedPostResponse add(@RequestBody FeaturedPostRequest featuredPostRequest){
        return featuredPostService.create(featuredPostRequest);
    }
    @PutMapping("/{featuredPostId}")
    public FeaturedPostResponse edit(@PathVariable(name = "featuredPostId")UUID featuredPostId, @RequestBody FeaturedPostRequest featuredPostRequest){
        return featuredPostService.update(featuredPostId, featuredPostRequest);
    }
    @DeleteMapping("/{featuredPostId}")
    public ResponseData<?> delete(@PathVariable(name = "featuredPostId") UUID featuredPostId){
        return featuredPostService.delete(featuredPostId);
    }
    @GetMapping("/")
    public List<FeaturedPostResponse> findAll(){
        return featuredPostService.getAll();
    }
}
