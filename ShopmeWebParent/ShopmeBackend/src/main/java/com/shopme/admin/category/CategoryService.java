package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Category;


@Service
@Transactional
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Category> listAll(String sortDir) {
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("desc")) {
			sort = sort.descending();
		}else if(sortDir.equals("asc")) {
			sort = sort.ascending();
		}
		
		List<Category> rootCategories =  categoryRepo.listRootCategories(sort);
		
		return listHierarchicalCategories(rootCategories, sortDir);
	}
	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesInForm = new ArrayList<>();
		
		Iterable<Category> categories = categoryRepo.listRootCategories(Sort.by("name").ascending());

		for (Category category : categories) {
			if (category.getParent() == null) {
				categoriesInForm.add(Category.copyIdAndName(category));
				Set<Category> children = sortSubCategories(category.getChildren());

				for (Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					
					printChildren(categoriesInForm,subCategory, 1);
				}
			}
		}
		
		return categoriesInForm;
	}
	
	private void printChildren(List<Category> categoriesInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {				
				name += "--";
			}
			name += subCategory.getName();
			categoriesInForm.add(Category.copyIdAndName(subCategory.getId(), name));

			printChildren(categoriesInForm, subCategory, newSubLevel);
		}		
	}
	
	public Category save(Category category) {
		return categoryRepo.save(category);
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));

			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}

		return hierarchicalCategories;
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});

		sortedChildren.addAll(children);

		return sortedChildren;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
			Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {				
				name += "--";
			}
			name += subCategory.getName();

			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}

	}
	
	public Category getById(Integer id) throws CategoryNotFoundException {
		try {
			return categoryRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any user with ID " + id);
		}

	}
	
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Category categoryByName = categoryRepo.findByName(name);

		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = categoryRepo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";	
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}

			Category categoryByAlias = categoryRepo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}

		}

		return "OK";
	}

	public void updateUserStatus(Integer id, boolean status) {
		categoryRepo.updateEnabledStatus(id, status);
		
	}
	
	
}
