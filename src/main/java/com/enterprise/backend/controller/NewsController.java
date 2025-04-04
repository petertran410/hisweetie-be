package com.enterprise.backend.controller;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.model.request.NewsRequest;
import com.enterprise.backend.model.request.SearchNewsRequest;
import com.enterprise.backend.model.response.NewsResponse;
import com.enterprise.backend.model.response.NewsSearchResponse;
import com.enterprise.backend.service.NewsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @PostMapping
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public void createNews(@RequestBody @Valid NewsRequest request) {
        newsService.createNews(request);
    }

    @GetMapping("/get-all")
    public Page<NewsSearchResponse> search(@ModelAttribute @Valid SearchNewsRequest searchNewsRequest) {
        return newsService.search(searchNewsRequest);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long newsId) {
        return ResponseEntity.ok(newsService.getNewsById(newsId));
    }

    @GetMapping("/client/{newsId}")
    public ResponseEntity<NewsResponse> clientGetNewsById(@PathVariable Long newsId) {
        return ResponseEntity.ok(newsService.clientGetNewsById(newsId));
    }

    @PatchMapping("/{newsId}")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public void updateNews(@RequestBody NewsRequest request,
                           @PathVariable Long newsId) {
        newsService.updateNews(request, newsId);
    }

    @DeleteMapping("/{newsId}")
    @Transactional
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public void deleteById(@PathVariable Long newsId) {
        newsService.deleteNews(newsId);
    }
}
