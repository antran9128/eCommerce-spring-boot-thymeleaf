package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.Category;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Category> listAll() {
		return (List<Category>) categoryRepo.findAll();
	}
	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesInForm = new ArrayList<>();
		
		Iterable<Category> categories = categoryRepo.findAll();

		for (Category category : categories) {
			if (category.getParent() == null) {
				categoriesInForm.add(category);
				Set<Category> children = category.getChildren();

				for (Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesInForm.add(new Category(name));
					
					printChildren(categoriesInForm,subCategory, 1);
				}
			}
		}
		
		return categoriesInForm;
	}
	
	private void printChildren(List<Category> categoriesInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {				
				name += "--";
			}
			name += subCategory.getName();
			categoriesInForm.add(new Category(name));

			printChildren(categoriesInForm, subCategory, newSubLevel);
		}		
	}
}
