package com.onit.authentication.jwts.modules.post;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Post
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */

@RestController
@RequestMapping("/posts")
public class Post {

  @GetMapping
  public String getPosts(){
    return "It Works!";
  }
}
