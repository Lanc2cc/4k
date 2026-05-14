package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> getAllCategories();
}
